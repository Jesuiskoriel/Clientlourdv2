package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainSceneController {

    @FXML
    private TextField tfTitre;

    @FXML
    private void btnOk() {
        System.out.println("Titre saisi : " + tfTitre.getText());
    }

    @FXML
    private void handleQuit() {
        System.exit(0);
    }

    @FXML
    private void handleShowClients() {
        System.out.println("Afficher Clients");
    }

    @FXML
    private void handleShowEvents() {
        System.out.println("Afficher Événements");
    }

    @FXML
    private void handleShowTickets() {
        System.out.println("Afficher Tickets");
    }
}
