    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class BilletDAO {
        private ClientDAO clientDAO = new ClientDAO();
        private EvenementDAO evenementDAO = new EvenementDAO();
        private StatutBilletDAO statutDAO = new StatutBilletDAO();

        public List<Billet> getAllBillets() throws SQLException {
            List<Billet> billets = new ArrayList<>();
            String sql = "SELECT * FROM billet";
            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Client client = clientDAO.getClientById(rs.getInt("fk_client"));
                    Evenement event = evenementDAO.getEvenementById(rs.getInt("fk_evenement"));
                    StatutBillet statut = statutDAO.getStatutById(rs.getInt("fk_statut"));

                    Billet b = new Billet(
                            rs.getInt("id_billet"),
                            rs.getString("code_unique"),
                            client,
                            event,
                            statut,
                            rs.getTimestamp("date_achat").toLocalDateTime(),
                            rs.getBigDecimal("prix_paye")
                    );
                    billets.add(b);
                }
            }
            return billets;
        }

        public Billet getBilletById(int id) throws SQLException {
            String sql = "SELECT * FROM billet WHERE id_billet = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Client client = clientDAO.getClientById(rs.getInt("fk_client"));
                        Evenement event = evenementDAO.getEvenementById(rs.getInt("fk_evenement"));
                        StatutBillet statut = statutDAO.getStatutById(rs.getInt("fk_statut"));

                        return new Billet(
                                rs.getInt("id_billet"),
                                rs.getString("code_unique"),
                                client,
                                event,
                                statut,
                                rs.getTimestamp("date_achat").toLocalDateTime(),
                                rs.getBigDecimal("prix_paye")
                        );
                    }
                }
            }
            return null;
        }

        public void addBillet(Billet b) throws SQLException {
            String sql = "INSERT INTO billet (code_unique, fk_client, fk_evenement, fk_statut, date_achat, prix_paye) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, b.getCodeUnique());
                ps.setInt(2, b.getClient().getId());
                ps.setInt(3, b.getEvenement().getId());
                ps.setInt(4, b.getStatut().getId());
                ps.setTimestamp(5, Timestamp.valueOf(b.getDateAchat()));
                ps.setBigDecimal(6, b.getPrixPaye());
                ps.executeUpdate();
            }
        }

        public void updateBillet(Billet b) throws SQLException {
            String sql = "UPDATE billet SET code_unique = ?, fk_client = ?, fk_evenement = ?, fk_statut = ?, date_achat = ?, prix_paye = ? WHERE id_billet = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, b.getCodeUnique());
                ps.setInt(2, b.getClient().getId());
                ps.setInt(3, b.getEvenement().getId());
                ps.setInt(4, b.getStatut().getId());
                ps.setTimestamp(5, Timestamp.valueOf(b.getDateAchat()));
                ps.setBigDecimal(6, b.getPrixPaye());
                ps.setInt(7, b.getId());
                ps.executeUpdate();
            }
        }

        public void deleteBillet(int id) throws SQLException {
            String sql = "DELETE FROM billet WHERE id_billet = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
