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

public class EvenementDAO {

    private static final String BASE_SELECT = """
            SELECT
                id_evenement,
                nom,
                COALESCE(date_event, date_evenement) AS date_event,
                heure,
                lieu,
                COALESCE(capacite, 0) AS capacite,
                COALESCE(prix_base, prix, 0) AS prix_base,
                description
            FROM evenement
            """;

    public List<Evenement> findAll() {
        List<Evenement> evenements = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY date_event")
        ) {
            while (rs.next()) {
                evenements.add(mapEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public Optional<Evenement> findById(int id) {
        String sql = BASE_SELECT + " WHERE id_evenement = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
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
}
