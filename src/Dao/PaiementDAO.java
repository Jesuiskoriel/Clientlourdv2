package dao;

import model.Paiement;
import utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAO {

    private static final String BASE_SELECT = """
            SELECT id_paiement, fk_billet, montant, mode_paiement, date_paiement, reference
            FROM paiement
            """;

    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();
        try (
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY date_paiement DESC")
        ) {
            while (rs.next()) {
                paiements.add(mapPaiement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paiements;
    }

    public List<Paiement> findByBillet(int billetId) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE fk_billet = ? ORDER BY date_paiement DESC";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, billetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapPaiement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paiements;
    }

    public Optional<Paiement> findById(int id) {
        String sql = BASE_SELECT + " WHERE id_paiement = ?";
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaiement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean create(Paiement paiement) {
        String sql = """
                INSERT INTO paiement (fk_billet, montant, mode_paiement, reference)
                VALUES (?, ?, ?, ?)
                """;
        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, paiement.getBilletId());
            ps.setDouble(2, paiement.getMontant());
            ps.setString(3, paiement.getModePaiement());
            ps.setString(4, paiement.getReference());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        paiement.setId(keys.getInt(1));
                        paiement.setDatePaiement(LocalDateTime.now());
                    }
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Paiement mapPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        paiement.setId(rs.getInt("id_paiement"));
        paiement.setBilletId(rs.getInt("fk_billet"));
        paiement.setMontant(rs.getDouble("montant"));
        paiement.setModePaiement(rs.getString("mode_paiement"));
        Timestamp timestamp = rs.getTimestamp("date_paiement");
        if (timestamp != null) {
            paiement.setDatePaiement(timestamp.toLocalDateTime());
        }
        paiement.setReference(rs.getString("reference"));
        return paiement;
    }
}
