package controllers;

import dao.AchatDAO;
import dao.BilletDAO;
import dao.ClientDAO;
import dao.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.Achat;
import model.Billet;
import model.Client;
import model.User;

import java.util.List;
import java.util.Optional;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> idColumn;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> telephoneColumn;
    @FXML private TableColumn<Client, String> villeColumn;

    @FXML private TextField searchField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField villeField;

    private final ClientDAO clientDAO = new ClientDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BilletDAO billetDAO = new BilletDAO();
    private final AchatDAO achatDAO = new AchatDAO();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private Client selectedClient;

    @FXML
    public void initialize() {
        configureTable();
        loadClients();
        clientTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedClient = newSelection;
                    if (newSelection != null) {
                        populateForm(newSelection);
                    } else {
                        clearForm();
                    }
                }
        );
        clientTable.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showClientDetails(row.getItem());
                }
            });
            return row;
        });
    }

    private void configureTable() {
        // Lie chaque colonne aux propriétés du modèle Client et connecte la liste observable.
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
        clientTable.setItems(clients);
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadClients();
    }

    @FXML
    private void handleSearch() {
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            loadClients();
            return;
        }
        List<Client> result = clientDAO.searchByName(term.trim());
        clients.setAll(result);
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        if (selectedClient == null) {
            // Création d'un nouveau client.
            Client client = new Client(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    emailField.getText().trim(),
                    telephoneField.getText().trim(),
                    villeField.getText().trim()
            );
            if (clientDAO.create(client)) {
                clients.add(client);
                clientTable.getSelectionModel().select(client);
                showAlert("Succès", "Client créé avec succès.", Alert.AlertType.INFORMATION);
                clearForm();
            }
        } else {
            // Mise à jour du client sélectionné.
            selectedClient.setNom(nomField.getText().trim());
            selectedClient.setPrenom(prenomField.getText().trim());
            selectedClient.setEmail(emailField.getText().trim());
            selectedClient.setTelephone(telephoneField.getText().trim());
            selectedClient.setVille(villeField.getText().trim());
            if (clientDAO.update(selectedClient)) {
                clientTable.refresh();
                showAlert("Succès", "Client mis à jour.", Alert.AlertType.INFORMATION);
                clearForm();
            }
        }
    }

    @FXML
    private void handleDelete() {
        Client toDelete = clientTable.getSelectionModel().getSelectedItem();
        if (toDelete == null) {
            showAlert("Information", "Sélectionnez un client à supprimer.", Alert.AlertType.WARNING);
            return;
        }

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + toDelete.getNom() + " ?", ButtonType.OK, ButtonType.CANCEL).showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clientDAO.delete(toDelete.getId())) {
                clients.remove(toDelete);
                clearForm();
            }
        }
    }

    @FXML
    private void handleClearForm() {
        clientTable.getSelectionModel().clearSelection();
        clearForm();
    }

    private void loadClients() {
        clients.setAll(clientDAO.findAll());
    }

    private void populateForm(Client client) {
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        emailField.setText(client.getEmail());
        telephoneField.setText(client.getTelephone());
        villeField.setText(client.getVille());
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        villeField.clear();
        selectedClient = null;
    }

    private boolean validateForm() {
        if (nomField.getText().isBlank() || prenomField.getText().isBlank()) {
            showAlert("Erreur", "Le nom et le prénom sont obligatoires.", Alert.AlertType.ERROR);
            return false;
        }
        if (emailField.getText().isBlank()) {
            showAlert("Erreur", "L'email est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showClientDetails(Client client) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Client : " + client.getNom() + " " + client.getPrenom());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        User user = userDAO.findByEmail(client.getEmail()).orElse(null);
        String soldeText = user != null ? String.format("%.2f €", user.getSolde()) : "N/A";
        Label soldeLabel = new Label("Solde disponible : " + soldeText);

        VBox infoBox = new VBox(6);
        infoBox.getChildren().addAll(
                new Label("Nom : " + client.getNom()),
                new Label("Prénom : " + client.getPrenom()),
                new Label("Email : " + client.getEmail()),
                new Label("Téléphone : " + client.getTelephone()),
                new Label("Ville : " + client.getVille()),
                soldeLabel
        );

        TableView<Billet> ticketTable = new TableView<>();
        TableColumn<Billet, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodeUnique()));
        TableColumn<Billet, String> eventCol = new TableColumn<>("Événement");
        eventCol.setCellValueFactory(data -> {
            String label = data.getValue().getEvenement() != null ? data.getValue().getEvenement().getNom() : "";
            return new SimpleStringProperty(label);
        });
        TableColumn<Billet, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatut() != null ? data.getValue().getStatut().getLibelle() : ""
        ));
        TableColumn<Billet, String> prixCol = new TableColumn<>("Prix payé");
        prixCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getPrixPaye())));

        ticketTable.getColumns().addAll(codeCol, eventCol, statutCol, prixCol);
        ObservableList<Billet> tickets = FXCollections.observableArrayList(billetDAO.findByClient(client.getId()));
        ticketTable.setItems(tickets);

        Button refundButton = new Button("Rembourser le billet sélectionné");
        refundButton.setOnAction(e -> {
            Billet selectedBillet = ticketTable.getSelectionModel().getSelectedItem();
            if (selectedBillet == null) {
                showAlert("Sélection requise", "Choisissez un billet à rembourser.", Alert.AlertType.WARNING);
                return;
            }
            Optional<ButtonType> confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Rembourser le billet " + selectedBillet.getCodeUnique() + " ?", ButtonType.OK, ButtonType.CANCEL)
                    .showAndWait();
            if (confirmation.isEmpty() || confirmation.get() != ButtonType.OK) {
                return;
            }
            if (billetDAO.delete(selectedBillet.getId())) {
                tickets.remove(selectedBillet);
                ticketTable.refresh();
                showAlert("Remboursement effectué", "Le billet a été supprimé.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de rembourser ce billet.", Alert.AlertType.ERROR);
            }
        });

        TableView<Achat> achatTable = new TableView<>();
        ObservableList<Achat> achats = FXCollections.observableArrayList();
        if (user != null) {
            achats.setAll(achatDAO.findByUser(user.getId()));
        }
        TableColumn<Achat, String> achatEventCol = new TableColumn<>("Événement");
        achatEventCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEventName()));
        TableColumn<Achat, String> achatPrixCol = new TableColumn<>("Prix");
        achatPrixCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getPrix())));
        TableColumn<Achat, String> achatDateCol = new TableColumn<>("Date");
        achatDateCol.setCellValueFactory(data -> {
            String formatted = data.getValue().getDateAchat() != null
                    ? data.getValue().getDateAchat().toString()
                    : "";
            return new SimpleStringProperty(formatted);
        });
        achatTable.getColumns().addAll(achatEventCol, achatPrixCol, achatDateCol);
        achatTable.setItems(achats);

        Button refundPurchaseButton = new Button("Rembourser l'achat sélectionné");
        refundPurchaseButton.setDisable(user == null);
        refundPurchaseButton.setOnAction(e -> {
            Achat achat = achatTable.getSelectionModel().getSelectedItem();
            if (achat == null) {
                showAlert("Sélection requise", "Choisissez un achat à rembourser.", Alert.AlertType.WARNING);
                return;
            }
            Optional<ButtonType> confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Rembourser l'achat de " + achat.getEventName() + " ?", ButtonType.OK, ButtonType.CANCEL)
                    .showAndWait();
            if (confirmation.isEmpty() || confirmation.get() != ButtonType.OK) {
                return;
            }
            if (achatDAO.delete(achat.getId())) {
                if (user != null) {
                    double nouveauSolde = user.getSolde() + achat.getPrix();
                    if (userDAO.updateSolde(user.getId(), nouveauSolde)) {
                        user.setSolde(nouveauSolde);
                        soldeLabel.setText(String.format("Solde disponible : %.2f €", nouveauSolde));
                    }
                }
                achats.remove(achat);
                showAlert("Remboursement effectué", "L'achat a été remboursé.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de rembourser cet achat.", Alert.AlertType.ERROR);
            }
        });

        VBox content = new VBox(12,
                infoBox,
                new Label("Billets enregistrés"),
                ticketTable,
                refundButton,
                new Label("Achats en ligne"),
                achatTable,
                refundPurchaseButton);
        content.setPrefSize(650, 520);
        dialog.getDialogPane().setContent(new BorderPane(content));
        dialog.showAndWait();
    }
}
