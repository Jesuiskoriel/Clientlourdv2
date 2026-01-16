package dao;

import model.SecurityQuestion;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gère les questions de sécurité et les codes OTP 2FA.
 */
public class SecurityDAO {

    private static final List<SecurityQuestion> DEFAULT_QUESTIONS = List.of(
            new SecurityQuestion(1, "Quel est le nom de votre premier animal ?"),
            new SecurityQuestion(2, "Dans quelle ville êtes-vous né(e) ?"),
            new SecurityQuestion(3, "Quel est votre film préféré ?"),
            new SecurityQuestion(4, "Quel est le prénom de votre enseignant marquant ?"),
            new SecurityQuestion(5, "Quel est le nom de jeune fille de votre mère ?"),
            new SecurityQuestion(6, "Quel est votre plat préféré ?"),
            new SecurityQuestion(7, "Quel est votre livre préféré ?"),
            new SecurityQuestion(8, "Quelle est votre chanson fétiche ?"),
            new SecurityQuestion(9, "Où avez-vous passé vos meilleures vacances ?"),
            new SecurityQuestion(10, "Quel est le prénom de votre ami d’enfance ?")
    );

    // Initialise les tables de sécurité et les questions par défaut.
    public void ensureSetup() {
        try (Connection conn = Database.getConnection()) {
            // Crée les tables liées à la sécurité si elles n'existent pas.
            try (PreparedStatement ps = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS security_question (
                        id INT PRIMARY KEY,
                        libelle VARCHAR(255) NOT NULL
                    )
                    """)) {
                ps.execute();
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS security_answer (
                        user_id INT NOT NULL,
                        question_id INT NOT NULL,
                        answer_hash VARCHAR(255) NOT NULL,
                        PRIMARY KEY (user_id, question_id)
                    )
                    """)) {
                ps.execute();
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS otp_token (
                        user_id INT PRIMARY KEY,
                        code VARCHAR(10) NOT NULL,
                        expires_at TIMESTAMP NOT NULL
                    )
                    """)) {
                ps.execute();
            }
            seedQuestions(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insère les questions par défaut si absentes.
    private void seedQuestions(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO security_question (id, libelle) VALUES (?, ?)")) {
            for (SecurityQuestion q : DEFAULT_QUESTIONS) {
                ps.setInt(1, q.getId());
                ps.setString(2, q.getLibelle());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Retourne toutes les questions disponibles.
    public List<SecurityQuestion> getQuestions() {
        List<SecurityQuestion> questions = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, libelle FROM security_question ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                questions.add(new SecurityQuestion(rs.getInt("id"), rs.getString("libelle")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    // Sauvegarde les réponses de sécurité (hashées).
    public void saveSecurityAnswers(int userId, Map<Integer, String> answersByQuestion) {
        String sql = """
                INSERT INTO security_answer (user_id, question_id, answer_hash)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE answer_hash = VALUES(answer_hash)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, String> entry : answersByQuestion.entrySet()) {
                ps.setInt(1, userId);
                ps.setInt(2, entry.getKey());
                ps.setString(3, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère les questions associées à un utilisateur.
    public List<SecurityQuestion> findUserQuestions(int userId) {
        List<SecurityQuestion> result = new ArrayList<>();
        String sql = """
                SELECT q.id, q.libelle
                FROM security_answer a
                JOIN security_question q ON q.id = a.question_id
                WHERE a.user_id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new SecurityQuestion(rs.getInt("id"), rs.getString("libelle")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Vérifie si les réponses fournies correspondent (au moins 3).
    public boolean validateAnswers(int userId, Map<Integer, String> providedHashes) {
        String sql = "SELECT question_id, answer_hash FROM security_answer WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int ok = 0;
                while (rs.next()) {
                    int qId = rs.getInt("question_id");
                    String stored = rs.getString("answer_hash");
                    String provided = providedHashes.get(qId);
                    if (provided != null && provided.equals(stored)) {
                        ok++;
                    }
                }
                return ok >= 3;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Enregistre un code OTP avec date d'expiration.
    public void storeOtp(int userId, String code, LocalDateTime expiresAt) {
        String sql = """
                INSERT INTO otp_token (user_id, code, expires_at)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE code = VALUES(code), expires_at = VALUES(expires_at)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, code);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère le code OTP courant pour un utilisateur.
    public Optional<OtpToken> getOtp(int userId) {
        String sql = "SELECT code, expires_at FROM otp_token WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OtpToken token = new OtpToken();
                    token.code = rs.getString("code");
                    Timestamp ts = rs.getTimestamp("expires_at");
                    if (ts != null) {
                        token.expiresAt = ts.toLocalDateTime();
                    }
                    return Optional.of(token);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Supprime le code OTP stocké.
    public void clearOtp(int userId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM otp_token WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Expose les questions par défaut.
    public List<SecurityQuestion> defaultQuestions() {
        return DEFAULT_QUESTIONS;
    }

    public static class OtpToken {
        public String code;
        public LocalDateTime expiresAt;
    }
}
