package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Ticket;
import model.Event;
import model.Client;
import Dao.TicketDAO;
import Dao.EventDAO;
import Dao.ClientDAO;
import java.util.List;

public class TicketController {
    
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, String> eventColumn;
    @FXML private TableColumn<Ticket, String> clientColumn;
    @FXML private TableColumn<Ticket, String> dateColumn;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private TextField prixField;
    @FXML private DatePicker dateAchatField;
    @FXML private TextField searchField;
    
    private TicketDAO ticketDAO;
    private EventDAO eventDAO;
    private ClientDAO clientDAO;
    private ObservableList<Ticket> ticketList;
    private Ticket currentTicket;
    
    @FXML
    public void initialize() {
        ticketDAO = new TicketDAO();
        eventDAO = new EventDAO();
        clientDAO = new ClientDAO();
        
        setupTableColumns();
        loadComboboxData();
        loadTickets();
    }
    
    private void setupTableColumns() {
        eventColumn.setCellValueFactory(cellData -> cellData.getValue().eventProperty());
        clientColumn.setCellValueFactory(cellData -> cellData.getValue().clientProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateAchatProperty());
    }
    
    private void loadComboboxData() {
        // Charger les événements
        List<Event> events = eventDAO.findAll();
        eventComboBox.setItems(FXCollections.observableArrayList(events));
        
        // Charger les clients
        List<Client> clients = clientDAO.findAll();
        clientComboBox.setItems(FXCollections.observableArrayList(clients));
    }
    
    private void loadTickets() {
        List<Ticket> tickets = ticketDAO.findAll();
        ticketList = FXCollections.observableArrayList(tickets);
        ticketTable.setItems(ticketList);
    }
    
    @FXML
    private void handleCreateTicket() {
        if (validateForm()) {
            Ticket ticket = new Ticket();
            ticket.setEventId(eventComboBox.getValue().getId());
            ticket.setClientId(clientComboBox.getValue().getId());
            ticket.setPrix(Double.parseDouble(prixField.getText()));
            ticket.setDateAchat(dateAchatField.getValue().toString());
            
            if (ticketDAO.create(ticket)) {
                clearForm();
                loadTickets();
                showAlert("Succès", "Ticket créé avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleUpdateTicket() {
        if (currentTicket != null && validateForm()) {
            currentTicket.setEventId(eventComboBox.getValue().getId());
            currentTicket.setClientId(clientComboBox.getValue().getId());
            currentTicket.setPrix(Double.parseDouble(prixField.getText()));
            currentTicket.setDateAchat(dateAchatField.getValue().toString());
            
            if (ticketDAO.update(currentTicket)) {
                clearForm();
                loadTickets();
                showAlert("Succès", "Ticket modifié avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleDeleteTicket() {
        Ticket selected = ticketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer le ticket");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce ticket ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (ticketDAO.delete(selected.getId())) {
                    loadTickets();
                    showAlert("Succès", "Ticket supprimé avec succès", Alert.AlertType.INFORMATION);
                }
            }
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadTickets();
        } else {
            List<Ticket> tickets = ticketDAO.findByClientName(searchTerm);
            ticketList = FXCollections.observableArrayList(tickets);
            ticketTable.setItems(ticketList);
        }
    }
    
    @FXML
    private void handleRowSelection() {
        currentTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (currentTicket != null) {
            populateForm(currentTicket);
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
    }
    
    private void populateForm(Ticket ticket) {
        
        Event event = eventDAO.findById(ticket.getEventId());
        if (event != null) {
            eventComboBox.setValue(event);
        }
        
        
        Client client = clientDAO.findById(ticket.getClientId());
        if (client != null) {
            clientComboBox.setValue(client);
        }
        
        prixField.setText(String.valueOf(ticket.getPrix()));
        
    }
    
    private void clearForm() {
        eventComboBox.setValue(null);
        clientComboBox.setValue(null);
        prixField.clear();
        dateAchatField.setValue(null);
        currentTicket = null;
    }
    
    private boolean validateForm() {
        if (eventComboBox.getValue() == null || 
            clientComboBox.getValue() == null || 
            prixField.getText().isEmpty() ||
            dateAchatField.getValue() == null) {
            
            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return false;
        }
        
        try {
            Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide", Alert.AlertType.ERROR);
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