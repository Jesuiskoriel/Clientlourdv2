package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// Contrôleur du tableau de bord principal (menu + zone centrale).
public class MainSceneController {

    @FXML private BorderPane rootPane;
    @FXML private Label statusLabel;

    // Charge la vue par défaut à l'ouverture.
    @FXML
    public void initialize() {
        loadCenter("/views/client/client-list.fxml", "Gestion des clients");
    }

    // Affiche la liste des clients.
    @FXML
    private void handleShowClients() {
        loadCenter("/views/client/client-list.fxml", "Gestion des clients");
    }

    // Affiche la gestion des événements.
    @FXML
    private void handleShowEvents() {
        loadCenter("/views/event/event-list.fxml", "Gestion des événements");
    }

    // Affiche la gestion des billets/paiements.
    @FXML
    private void handleShowTickets() {
        loadCenter("/views/ticket/ticket-list.fxml", "Billets et paiements");
    }

    // Affiche l'administration des comptes.
    @FXML
    private void handleShowAccounts() {
        loadCenter("/views/admin/user-list.fxml", "Comptes utilisateurs");
    }

    // Ferme la fenêtre principale.
    @FXML
    private void handleQuit() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    // Charge une vue FXML dans la zone centrale et met à jour le statut.
    private void loadCenter(String fxmlPath, String label) {
        try {
            // Charge dynamiquement une sous-vue et l'affiche dans la zone centrale.
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            rootPane.setCenter(content);
            statusLabel.setText(label);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la vue " + fxmlPath);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
