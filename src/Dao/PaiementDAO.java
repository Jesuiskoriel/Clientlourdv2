    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class PaiementDAO {
        private BilletDAO billetDAO = new BilletDAO();

        public List<Paiement> getAllPaiements() throws SQLException {
            List<Paiement> paiements = new ArrayList<>();
            String sql = "SELECT * FROM paiement";
            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Billet billet = billetDAO.getBilletById(rs.getInt("fk_billet"));
                    Paiement p = new Paiement(
                            rs.getInt("id_paiement"),
                            billet,
                            rs.getBigDecimal("montant"),
                            rs.getString("mode_paiement"),
                            rs.getTimestamp("date_paiement").toLocalDateTime(),
                            rs.getString("reference")
                    );
                    paiements.add(p);
                }
            }
            return paiements;
        }

        public Paiement getPaiementById(int id) throws SQLException {
            String sql = "SELECT * FROM paiement WHERE id_paiement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Billet billet = billetDAO.getBilletById(rs.getInt("fk_billet"));
                        return new Paiement(
                                rs.getInt("id_paiement"),
                                billet,
                                rs.getBigDecimal("montant"),
                                rs.getString("mode_paiement"),
                                rs.getTimestamp("date_paiement").toLocalDateTime(),
                                rs.getString("reference")
                        );
                    }
                }
            }
            return null;
        }

        public void addPaiement(Paiement p) throws SQLException {
            String sql = "INSERT INTO paiement (fk_billet, montant, mode_paiement, date_paiement, reference) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, p.getBillet().getId());
                ps.setBigDecimal(2, p.getMontant());
                ps.setString(3, p.getModePaiement());
                ps.setTimestamp(4, Timestamp.valueOf(p.getDatePaiement()));
                ps.setString(5, p.getReference());
                ps.executeUpdate();
            }
        }

        public void updatePaiement(Paiement p) throws SQLException {
            String sql = "UPDATE paiement SET fk_billet = ?, montant = ?, mode_paiement = ?, date_paiement = ?, reference = ? WHERE id_paiement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, p.getBillet().getId());
                ps.setBigDecimal(2, p.getMontant());
                ps.setString(3, p.getModePaiement());
                ps.setTimestamp(4, Timestamp.valueOf(p.getDatePaiement()));
                ps.setString(5, p.getReference());
                ps.setInt(6, p.getId());
                ps.executeUpdate();
            }
        }

        public void deletePaiement(int id) throws SQLException {
            String sql = "DELETE FROM paiement WHERE id_paiement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
