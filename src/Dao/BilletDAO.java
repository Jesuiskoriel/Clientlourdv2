package dao;

import model.Billet;
import model.Client;
import model.Evenement;
import model.StatutBillet;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BilletDAO {

    private static final String BASE_SELECT = """
            SELECT b.id_billet,
                   b.code_unique,
                   b.fk_client,
                   b.fk_evenement,
                   b.fk_statut,
                   b.date_achat,
                   b.prix_paye,
                   c.nom            AS client_nom,
                   c.prenom         AS client_prenom,
                   c.email          AS client_email,
                   c.telephone      AS client_tel,
                   c.ville          AS client_ville,
                   e.nom            AS event_nom,
                   e.date_event     AS event_date,
                   e.heure          AS event_time,
                   e.lieu           AS event_lieu,
                   e.capacite       AS event_capacite,
                   e.prix_base      AS event_prix,
                   e.description    AS event_description,
                   s.id_statut,
                   s.libelle        AS statut_libelle
            FROM billet b
            JOIN client c ON c.id_client = b.fk_client
            JOIN evenement e ON e.id_evenement = b.fk_evenement
            JOIN statut_billet s ON s.id_statut = b.fk_statut
            """;

    public List<Billet> findAll() {
        List<Billet> billets = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY b.date_achat DESC")
        ) {
            while (rs.next()) {
                billets.add(mapBillet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billets;
    }

    public List<Billet> findByClient(int clientId) {
        List<Billet> billets = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE b.fk_client = ? ORDER BY b.date_achat DESC";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    billets.add(mapBillet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billets;
    }

    public boolean create(Billet billet) {
        String sql = """
                INSERT INTO billet (code_unique, fk_client, fk_evenement, fk_statut, prix_paye)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, billet.getCodeUnique());
            ps.setInt(2, billet.getClient().getId());
            ps.setInt(3, billet.getEvenement().getId());
            ps.setInt(4, billet.getStatut().getId());
            ps.setDouble(5, billet.getPrixPaye());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        billet.setId(keys.getInt(1));
                        billet.setDateAchat(LocalDateTime.now());
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatut(int billetId, int statutId) {
        String sql = "UPDATE billet SET fk_statut = ? WHERE id_billet = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, statutId);
            ps.setInt(2, billetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateClient(int billetId, int clientId) {
        String sql = "UPDATE billet SET fk_client = ? WHERE id_billet = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, clientId);
            ps.setInt(2, billetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int billetId) {
        String sql = "DELETE FROM billet WHERE id_billet = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, billetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Billet mapBillet(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("fk_client"));
        client.setNom(rs.getString("client_nom"));
        client.setPrenom(rs.getString("client_prenom"));
        client.setEmail(rs.getString("client_email"));
        client.setTelephone(rs.getString("client_tel"));
        client.setVille(rs.getString("client_ville"));

        Evenement event = new Evenement();
        event.setId(rs.getInt("fk_evenement"));
        event.setNom(rs.getString("event_nom"));
        event.setDateEvent(rs.getObject("event_date", LocalDate.class));
        event.setHeure(rs.getObject("event_time", LocalTime.class));
        event.setLieu(rs.getString("event_lieu"));
        event.setCapacite(rs.getInt("event_capacite"));
        event.setPrixBase(rs.getDouble("event_prix"));
        event.setDescription(rs.getString("event_description"));

        StatutBillet statut = new StatutBillet(
                rs.getInt("id_statut"),
                rs.getString("statut_libelle")
        );

        Billet billet = new Billet();
        billet.setId(rs.getInt("id_billet"));
        billet.setCodeUnique(rs.getString("code_unique"));
        billet.setClient(client);
        billet.setEvenement(event);
        billet.setStatut(statut);
        Timestamp timestamp = rs.getTimestamp("date_achat");
        if (timestamp != null) {
            billet.setDateAchat(timestamp.toLocalDateTime());
        }
        billet.setPrixPaye(rs.getDouble("prix_paye"));
        return billet;
    }
}
