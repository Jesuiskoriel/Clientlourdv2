package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainSceneController {

    @FXML private BorderPane rootPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        loadCenter("/views/client/client-list.fxml", "Gestion des clients");
    }

    @FXML
    private void handleShowClients() {
        loadCenter("/views/client/client-list.fxml", "Gestion des clients");
    }

    @FXML
    private void handleShowEvents() {
        loadCenter("/views/event/event-list.fxml", "Gestion des événements");
    }

    @FXML
    private void handleShowTickets() {
        loadCenter("/views/ticket/ticket-list.fxml", "Billets et paiements");
    }

    @FXML
    private void handleShowAccounts() {
        loadCenter("/views/admin/user-list.fxml", "Comptes utilisateurs");
    }

    @FXML
    private void handleQuit() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

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
