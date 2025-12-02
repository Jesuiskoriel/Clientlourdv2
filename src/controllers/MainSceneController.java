import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.io.IOException;

public class MainSceneController {

    @FXML
    private TextField tfTitre;

    @FXML
    void btnOk(ActionEvent event) {
        Stage mainWindow = (Stage) tfTitre.getScene().getWindow();
        mainWindow.setTitle(tfTitre.getText());
    }

    
    @FXML
    private void handleShowClients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/client/client-list.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Clients");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface clients", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleShowEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/event/event-list.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Événements");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface événements", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleShowTickets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ticket/ticket-list.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Tickets");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface tickets", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleQuit() {
        System.exit(0);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}