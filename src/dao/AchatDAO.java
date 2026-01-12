package dao;

import model.Achat;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AchatDAO {

    public boolean create(int userId, int eventId, double prix) {
        String sql = """
                INSERT INTO achat_utilisateur (id_utilisateur, id_evenement, prix_achat)
                VALUES (?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setDouble(3, prix);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Achat> findByUser(int userId) {
        List<Achat> achats = new ArrayList<>();
        String sql = """
                SELECT a.id_achat, a.id_utilisateur, a.id_evenement, a.prix_achat, a.date_achat,
                       e.nom AS event_name
                FROM achat_utilisateur a
                JOIN evenement e ON e.id_evenement = a.id_evenement
                WHERE a.id_utilisateur = ?
                ORDER BY a.date_achat DESC
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Achat achat = new Achat();
                    achat.setId(rs.getInt("id_achat"));
                    achat.setUserId(rs.getInt("id_utilisateur"));
                    achat.setEventId(rs.getInt("id_evenement"));
                    achat.setPrix(rs.getDouble("prix_achat"));
                    achat.setEventName(rs.getString("event_name"));
                    java.sql.Timestamp ts = rs.getTimestamp("date_achat");
                    if (ts != null) {
                        achat.setDateAchat(ts.toLocalDateTime());
                    } else {
                        achat.setDateAchat(LocalDateTime.now());
                    }
                    achats.add(achat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return achats;
    }

    public boolean delete(int achatId) {
        String sql = "DELETE FROM achat_utilisateur WHERE id_achat = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, achatId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countByEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM achat_utilisateur WHERE id_evenement = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
