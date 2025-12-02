    import java.sql.*;
    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.ArrayList;
    import java.util.List;

    public class EvenementDAO {

        // Récupérer tous les événements
        public List<Evenement> getAllEvenements() throws SQLException {
            List<Evenement> evenements = new ArrayList<>();
            String sql = "SELECT * FROM evenement";
            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Evenement e = new Evenement(
                            rs.getInt("id_evenement"),
                            rs.getString("nom"),
                            rs.getDate("date_event").toLocalDate(),
                            rs.getTime("heure").toLocalTime(),
                            rs.getString("lieu"),
                            rs.getInt("capacite"),
                            rs.getBigDecimal("prix_base"),
                            rs.getString("description")
                    );
                    evenements.add(e);
                }
            }
            return evenements;
        }

        // Récupérer un événement par ID
        public Evenement getEvenementById(int id) throws SQLException {
            String sql = "SELECT * FROM evenement WHERE id_evenement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Evenement(
                                rs.getInt("id_evenement"),
                                rs.getString("nom"),
                                rs.getDate("date_event").toLocalDate(),
                                rs.getTime("heure").toLocalTime(),
                                rs.getString("lieu"),
                                rs.getInt("capacite"),
                                rs.getBigDecimal("prix_base"),
                                rs.getString("description")
                        );
                    }
                }
            }
            return null;
        }

        // Ajouter un événement
        public void addEvenement(Evenement e) throws SQLException {
            String sql = "INSERT INTO evenement (nom, date_event, heure, lieu, capacite, prix_base, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, e.getNom());
                ps.setDate(2, Date.valueOf(e.getDateEvent()));
                ps.setTime(3, Time.valueOf(e.getHeure()));
                ps.setString(4, e.getLieu());
                ps.setInt(5, e.getCapacite());
                ps.setBigDecimal(6, e.getPrixBase());
                ps.setString(7, e.getDescription());
                ps.executeUpdate();
            }
        }

        // Modifier un événement
        public void updateEvenement(Evenement e) throws SQLException {
            String sql = "UPDATE evenement SET nom = ?, date_event = ?, heure = ?, lieu = ?, capacite = ?, prix_base = ?, description = ? WHERE id_evenement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, e.getNom());
                ps.setDate(2, Date.valueOf(e.getDateEvent()));
                ps.setTime(3, Time.valueOf(e.getHeure()));
                ps.setString(4, e.getLieu());
                ps.setInt(5, e.getCapacite());
                ps.setBigDecimal(6, e.getPrixBase());
                ps.setString(7, e.getDescription());
                ps.setInt(8, e.getId());
                ps.executeUpdate();
            }
        }

        // Supprimer un événement
        public void deleteEvenement(int id) throws SQLException {
            String sql = "DELETE FROM evenement WHERE id_evenement = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
