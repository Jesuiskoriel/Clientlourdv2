package dao;

import model.StatutBillet;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Accès aux données pour les statuts de billet.
public class StatutBilletDAO {

    private static final String BASE_SELECT = """
            SELECT id_statut, libelle
            FROM statut_billet
            """;

    // Retourne tous les statuts disponibles.
    public List<StatutBillet> findAll() {
        List<StatutBillet> statuts = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY libelle")
        ) {
            while (rs.next()) {
                statuts.add(mapStatut(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuts;
    }

    // Recherche un statut par id.
    public Optional<StatutBillet> findById(int id) {
        String sql = BASE_SELECT + " WHERE id_statut = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapStatut(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Transforme un ResultSet en objet StatutBillet.
    private StatutBillet mapStatut(ResultSet rs) throws SQLException {
        return new StatutBillet(
                rs.getInt("id_statut"),
                rs.getString("libelle")
        );
    }
}
