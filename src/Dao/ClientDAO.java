    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class ClientDAO {

        public List<Client> getAllClients() throws SQLException {
            List<Client> clients = new ArrayList<>();
            String sql = "SELECT * FROM client";
            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Client c = new Client(
                            rs.getInt("id_client"),
                            rs.getString("prenom"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("ville"),
                            rs.getTimestamp("date_creation").toLocalDateTime()
                    );
                    clients.add(c);
                }
            }
            return clients;
        }

        public Client getClientById(int id) throws SQLException {
            String sql = "SELECT * FROM client WHERE id_client = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Client(
                                rs.getInt("id_client"),
                                rs.getString("prenom"),
                                rs.getString("nom"),
                                rs.getString("email"),
                                rs.getString("telephone"),
                                rs.getString("ville"),
                                rs.getTimestamp("date_creation").toLocalDateTime()
                        );
                    }
                }
            }
            return null;
        }

        public void addClient(Client c) throws SQLException {
            String sql = "INSERT INTO client (prenom, nom, email, telephone, ville) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getPrenom());
                ps.setString(2, c.getNom());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getTelephone());
                ps.setString(5, c.getVille());
                ps.executeUpdate();
            }
        }

        public void updateClient(Client c) throws SQLException {
            String sql = "UPDATE client SET prenom = ?, nom = ?, email = ?, telephone = ?, ville = ? WHERE id_client = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getPrenom());
                ps.setString(2, c.getNom());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getTelephone());
                ps.setString(5, c.getVille());
                ps.setInt(6, c.getId());
                ps.executeUpdate();
            }
        }

        public void deleteClient(int id) throws SQLException {
            String sql = "DELETE FROM client WHERE id_client = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
