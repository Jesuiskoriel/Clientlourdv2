    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class StatutBilletDAO {

        public List<StatutBillet> getAllStatuts() throws SQLException {
            List<StatutBillet> statuts = new ArrayList<>();
            String sql = "SELECT * FROM statut_billet";
            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    StatutBillet s = new StatutBillet(
                            rs.getInt("id_statut"),
                            rs.getString("libelle")
                    );
                    statuts.add(s);
                }
            }
            return statuts;
        }

        public StatutBillet getStatutById(int id) throws SQLException {
            String sql = "SELECT * FROM statut_billet WHERE id_statut = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new StatutBillet(
                                rs.getInt("id_statut"),
                                rs.getString("libelle")
                        );
                    }
                }
            }
            return null;
        }

        public void addStatut(StatutBillet s) throws SQLException {
            String sql = "INSERT INTO statut_billet (libelle) VALUES (?)";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getLibelle());
                ps.executeUpdate();
            }
        }

        public void updateStatut(StatutBillet s) throws SQLException {
            String sql = "UPDATE statut_billet SET libelle = ? WHERE id_statut = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getLibelle());
                ps.setInt(2, s.getId());
                ps.executeUpdate();
            }
        }

        public void deleteStatut(int id) throws SQLException {
            String sql = "DELETE FROM statut_billet WHERE id_statut = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
