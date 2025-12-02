package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Client;
import Dao.ClientDAO;
import java.util.List;

public class ClientController {
    
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TextField searchField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    
    private ClientDAO clientDAO;
    private ObservableList<Client> clientList;
    private Client currentClient;
    
    @FXML
    public void initialize() {
        clientDAO = new ClientDAO();
        setupTableColumns();
        loadClients();
    }
    
    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
    }
    
    private void loadClients() {
        List<Client> clients = clientDAO.findAll();
        clientList = FXCollections.observableArrayList(clients);
        clientTable.setItems(clientList);
    }
    
    @FXML
    private void handleCreateClient() {
        if (validateForm()) {
            Client client = new Client();
            client.setNom(nomField.getText());
            client.setPrenom(prenomField.getText());
            client.setEmail(emailField.getText());
            client.setTelephone(telephoneField.getText());
            client.setAdresse(adresseField.getText());
            
            if (clientDAO.create(client)) {
                clearForm();
                loadClients();
                showAlert("Succès", "Client créé avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleUpdateClient() {
        if (currentClient != null && validateForm()) {
            currentClient.setNom(nomField.getText());
            currentClient.setPrenom(prenomField.getText());
            currentClient.setEmail(emailField.getText());
            currentClient.setTelephone(telephoneField.getText());
            currentClient.setAdresse(adresseField.getText());
            
            if (clientDAO.update(currentClient)) {
                clearForm();
                loadClients();
                showAlert("Succès", "Client modifié avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleDeleteClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer le client");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (clientDAO.delete(selected.getId())) {
                    loadClients();
                    showAlert("Succès", "Client supprimé avec succès", Alert.AlertType.INFORMATION);
                }
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadClients();
        } else {
            List<Client> clients = clientDAO.findByName(searchTerm);
            clientList = FXCollections.observableArrayList(clients);
            clientTable.setItems(clientList);
        }
    }
    
    @FXML
    private void handleRowSelection() {
        currentClient = clientTable.getSelectionModel().getSelectedItem();
        if (currentClient != null) {
            populateForm(currentClient);
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
    }
    
    private void populateForm(Client client) {
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        emailField.setText(client.getEmail());
        telephoneField.setText(client.getTelephone());
        adresseField.setText(client.getAdresse());
    }
    
    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();
        currentClient = null;
    }
    
    private boolean validateForm() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
            showAlert("Erreur", "Le nom et prénom sont obligatoires", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}