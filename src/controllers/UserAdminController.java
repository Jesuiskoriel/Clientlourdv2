package controllers;

import dao.UserDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Gère la liste des comptes utilisateurs pour l'admin.
public class UserAdminController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> balanceColumn;
    @FXML private TableColumn<User, String> adminColumn;
    @FXML private TableColumn<User, String> createdColumn;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Configure la table et charge les utilisateurs.
    @FXML
    public void initialize() {
        configureTable();
        loadUsers();
    }

    // Définit les colonnes et formats d'affichage.
    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        balanceColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> String.format("%.2f €", cellData.getValue().getSolde())
                ));
        adminColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().isAdmin() ? "Oui" : "Non"
                ));
        createdColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> {
                    if (cellData.getValue().getDateCreation() == null) {
                        return "";
                    }
                    return dateFormatter.format(cellData.getValue().getDateCreation());
                }));
        userTable.setItems(users);
    }

    // Recharge la liste des utilisateurs.
    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    // Supprime un utilisateur non admin après confirmation.
    @FXML
    private void handleDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Choisissez un compte à supprimer.", Alert.AlertType.WARNING);
            return;
        }
        if (selected.isAdmin()) {
            showAlert("Action interdite", "Impossible de supprimer un compte administrateur.", Alert.AlertType.ERROR);
            return;
        }
        // Confirmation avant purge (sécurité, achats, OTP) puis suppression de l'utilisateur.
        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le compte " + selected.getEmail() + " ?", ButtonType.OK, ButtonType.CANCEL).showAndWait();
        if (confirm.isEmpty() || confirm.get() != ButtonType.OK) {
            return;
        }
        if (userDAO.deleteUser(selected.getId())) {
            users.remove(selected);
            showAlert("Compte supprimé", "Le compte a été supprimé avec succès.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de supprimer ce compte (contrôlez les dépendances en base).", Alert.AlertType.ERROR);
        }
    }

    // Charge les comptes depuis la base.
    private void loadUsers() {
        users.setAll(userDAO.findAll());
        userTable.refresh();
    }

    // Affiche une alerte.
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
