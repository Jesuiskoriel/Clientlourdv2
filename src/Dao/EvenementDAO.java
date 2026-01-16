package dao;

import model.Evenement;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Accès aux données pour la table evenement.
public class EvenementDAO {

    // Retourne la liste complète des événements.
    public List<Evenement> findAll() {
        List<Evenement> evenements = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(buildSelect(conn) + " ORDER BY date_event")
        ) {
            while (rs.next()) {
                evenements.add(mapEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    // Recherche un événement par id.
    public Optional<Evenement> findById(int id) {
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(buildSelect(conn) + " WHERE id_evenement = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEvent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Crée un événement et récupère l'id généré.
    public boolean create(Evenement evenement) {
        String sql = """
                INSERT INTO evenement (nom, date_event, heure, lieu, capacite, prix_base, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, evenement.getNom());
            ps.setObject(2, evenement.getDateEvent());
            ps.setObject(3, evenement.getHeure());
            ps.setString(4, evenement.getLieu());
            ps.setInt(5, evenement.getCapacite());
            ps.setDouble(6, evenement.getPrixBase());
            ps.setString(7, evenement.getDescription());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        evenement.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Met à jour un événement existant.
    public boolean update(Evenement evenement) {
        String sql = """
                UPDATE evenement
                SET nom = ?, date_event = ?, heure = ?, lieu = ?, capacite = ?, prix_base = ?, description = ?
                WHERE id_evenement = ?
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, evenement.getNom());
            ps.setObject(2, evenement.getDateEvent());
            ps.setObject(3, evenement.getHeure());
            ps.setString(4, evenement.getLieu());
            ps.setInt(5, evenement.getCapacite());
            ps.setDouble(6, evenement.getPrixBase());
            ps.setString(7, evenement.getDescription());
            ps.setInt(8, evenement.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprime un événement par id.
    public boolean delete(int id) {
        String sql = "DELETE FROM evenement WHERE id_evenement = ?";
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

    // Transforme un ResultSet en objet Evenement.
    private Evenement mapEvent(ResultSet rs) throws SQLException {
        Evenement event = new Evenement();
        event.setId(rs.getInt("id_evenement"));
        event.setNom(rs.getString("nom"));
        event.setDateEvent(rs.getObject("date_event", LocalDate.class));
        event.setHeure(rs.getObject("heure", LocalTime.class));
        event.setLieu(rs.getString("lieu"));
        event.setCapacite(rs.getInt("capacite"));
        event.setPrixBase(rs.getDouble("prix_base"));
        event.setDescription(rs.getString("description"));
        return event;
    }

    // Construit un SELECT compatible selon le schéma existant.
    private String buildSelect(Connection conn) throws SQLException {
        String dateExpr = pickColumn(conn, "evenement", "date_event", "date_evenement", "NULL");
        String prixExpr = pickColumn(conn, "evenement", "prix_base", "prix", "0");
        String heureExpr = pickColumn(conn, "evenement", "heure", null, "NULL");
        String capaciteExpr = pickColumn(conn, "evenement", "capacite", null, "0");
        String lieuExpr = pickColumn(conn, "evenement", "lieu", null, "NULL");
        String descriptionExpr = pickColumn(conn, "evenement", "description", null, "NULL");
        return """
                SELECT
                    id_evenement,
                    nom,
                    %s AS date_event,
                    %s AS heure,
                    %s AS lieu,
                    %s AS capacite,
                    %s AS prix_base,
                    %s AS description
                FROM evenement
                """.formatted(dateExpr, heureExpr, lieuExpr, capaciteExpr, prixExpr, descriptionExpr);
    }

    // Choisit la bonne colonne (ou valeur par défaut) selon le schéma.
    private String pickColumn(Connection conn, String table, String primary, String fallback, String defaultLiteral)
            throws SQLException {
        String primaryResolved = resolveColumn(conn, table, primary);
        if (primaryResolved != null) {
            return primaryResolved;
        }
        if (fallback != null) {
            String fallbackResolved = resolveColumn(conn, table, fallback);
            if (fallbackResolved != null) {
                return fallbackResolved;
            }
        }
        return defaultLiteral;
    }

    // Vérifie si une colonne existe dans la table.
    private String resolveColumn(Connection conn, String table, String column) throws SQLException {
        if (column == null) {
            return null;
        }
        String catalog = conn.getCatalog();
        try (ResultSet rs = conn.getMetaData().getColumns(catalog, null, table, null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (name != null && name.equalsIgnoreCase(column)) {
                    return name;
                }
            }
        }
        return null;
    }
}
