package dao;

import model.Client;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Accès aux données pour la table client.
public class ClientDAO {

    private static final String BASE_SELECT = """
            SELECT id_client, nom, prenom, email, telephone, ville, date_creation
            FROM client
            """;

    // Retourne tous les clients.
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY nom, prenom")
        ) {
            while (rs.next()) {
                clients.add(mapClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Recherche des clients par nom/prénom.
    public List<Client> searchByName(String term) {
        List<Client> clients = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            String wildcard = "%" + term + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapClient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Recherche un client par id.
    public Optional<Client> findById(int id) {
        String sql = BASE_SELECT + " WHERE id_client = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapClient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Recherche un client par email.
    public Optional<Client> findByEmail(String email) {
        String sql = BASE_SELECT + " WHERE email = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapClient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Crée un client et remplit son id.
    public boolean create(Client client) {
        String sql = """
                INSERT INTO client (nom, prenom, email, telephone, ville)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelephone());
            ps.setString(5, client.getVille());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        client.setId(keys.getInt(1));
                        client.setDateCreation(LocalDateTime.now());
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Met à jour un client existant.
    public boolean update(Client client) {
        String sql = """
                UPDATE client
                SET nom = ?, prenom = ?, email = ?, telephone = ?, ville = ?
                WHERE id_client = ?
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelephone());
            ps.setString(5, client.getVille());
            ps.setInt(6, client.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprime un client par id.
    public boolean delete(int id) {
        String sql = "DELETE FROM client WHERE id_client = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Transforme un ResultSet en objet Client.
    private Client mapClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("id_client"));
        client.setNom(rs.getString("nom"));
        client.setPrenom(rs.getString("prenom"));
        client.setEmail(rs.getString("email"));
        client.setTelephone(rs.getString("telephone"));
        client.setVille(rs.getString("ville"));
        Timestamp timestamp = rs.getTimestamp("date_creation");
        if (timestamp != null) {
            client.setDateCreation(timestamp.toLocalDateTime());
        }
        return client;
    }
}
