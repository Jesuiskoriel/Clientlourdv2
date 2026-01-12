package dao;

import model.User;
import utils.Database;
import utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM utilisateur WHERE email = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean create(User user) {
        String sql = """
                INSERT INTO utilisateur (prenom, nom, nom_complet, email, telephone, mot_de_passe, solde, is_admin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            String[] names = splitNames(user.getNomComplet());
            ps.setString(1, names[0]);
            ps.setString(2, names[1]);
            ps.setString(3, user.getNomComplet());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getTelephone());
            ps.setString(6, user.getPasswordHash());
            ps.setDouble(7, user.getSolde());
            ps.setBoolean(8, user.isAdmin());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT id_utilisateur, prenom, nom, nom_complet, email, telephone, mot_de_passe, date_creation, solde, is_admin
                FROM utilisateur
                WHERE email = ?
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.matches(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT id_utilisateur, prenom, nom, nom_complet, email, telephone, mot_de_passe, date_creation, solde, is_admin
                FROM utilisateur
                ORDER BY date_creation DESC
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        // Supprime d'abord les dépendances (réponses sécurité, OTP, achats) puis l'utilisateur.
        String deleteSecurityAnswers = "DELETE FROM security_answer WHERE user_id = ?";
        String deleteOtp = "DELETE FROM otp_token WHERE user_id = ?";
        String deletePurchases = "DELETE FROM achat_utilisateur WHERE id_utilisateur = ?";
        String deleteUser = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        try (Connection conn = Database.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement psSec = conn.prepareStatement(deleteSecurityAnswers);
                 PreparedStatement psOtp = conn.prepareStatement(deleteOtp);
                 PreparedStatement psPurchases = conn.prepareStatement(deletePurchases);
                 PreparedStatement psUser = conn.prepareStatement(deleteUser)) {
                psSec.setInt(1, userId);
                psSec.executeUpdate();
                psOtp.setInt(1, userId);
                psOtp.executeUpdate();
                psPurchases.setInt(1, userId);
                psPurchases.executeUpdate();
                psUser.setInt(1, userId);
                int rows = psUser.executeUpdate();
                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id_utilisateur"));
        String fullName = rs.getString("nom_complet");
        if (fullName == null || fullName.isBlank()) {
            String prenom = rs.getString("prenom");
            String nom = rs.getString("nom");
            fullName = ((prenom != null ? prenom : "") + " " + (nom != null ? nom : "")).trim();
        }
        user.setNomComplet(fullName);
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setPasswordHash(rs.getString("mot_de_passe"));
        user.setSolde(rs.getDouble("solde"));
        user.setAdmin(rs.getBoolean("is_admin"));
        Timestamp creation = rs.getTimestamp("date_creation");
        if (creation != null) {
            user.setDateCreation(creation.toLocalDateTime());
        } else {
            user.setDateCreation(LocalDateTime.now());
        }
        return user;
    }

    public boolean updateSolde(int userId, double newSolde) {
        String sql = "UPDATE utilisateur SET solde = ? WHERE id_utilisateur = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setDouble(1, newSolde);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String hashed) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id_utilisateur = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String[] splitNames(String fullName) {
        String value = fullName != null ? fullName.trim() : "";
        if (value.isEmpty()) {
            return new String[]{"Utilisateur", "Inconnu"};
        }
        String[] parts = value.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return parts;
    }
}
