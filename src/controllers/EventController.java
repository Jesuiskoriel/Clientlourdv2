package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Event;
import Dao.EventDAO;
import java.util.List;

public class EventController {
    
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> nomColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> lieuColumn;
    @FXML private TextField searchField;
    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dateField;
    @FXML private TextField lieuField;
    @FXML private TextField prixField;
    @FXML private Pagination pagination;
    
    private EventDAO eventDAO;
    private ObservableList<Event> eventList;
    private Event currentEvent;
    private int itemsPerPage = 10;
    
    @FXML
    public void initialize() {
        eventDAO = new EventDAO();
        setupTableColumns();
        loadEvents();
        setupPagination();
    }
    
    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        lieuColumn.setCellValueFactory(cellData -> cellData.getValue().lieuProperty());
    }
    
    private void loadEvents() {
        List<Event> events = eventDAO.findAll();
        eventList = FXCollections.observableArrayList(events);
        eventTable.setItems(eventList);
    }
    
    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) eventList.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            showPage(newIndex.intValue());
        });
    }
    
    private void showPage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, eventList.size());
        
        if (fromIndex < eventList.size()) {
            ObservableList<Event> pageItems = FXCollections.observableArrayList(
                eventList.subList(fromIndex, toIndex)
            );
            eventTable.setItems(pageItems);
        }
    }
    
    @FXML
    private void handleCreateEvent() {
        if (validateForm()) {
            Event event = new Event();
            event.setNom(nomField.getText());
            event.setDescription(descriptionField.getText());
            event.setDate(dateField.getValue().toString());
            event.setLieu(lieuField.getText());
            event.setPrix(Double.parseDouble(prixField.getText()));
            
            if (eventDAO.create(event)) {
                clearForm();
                loadEvents();
                showAlert("Succès", "Événement créé avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleUpdateEvent() {
        if (currentEvent != null && validateForm()) {
            currentEvent.setNom(nomField.getText());
            currentEvent.setDescription(descriptionField.getText());
            currentEvent.setDate(dateField.getValue().toString());
            currentEvent.setLieu(lieuField.getText());
            currentEvent.setPrix(Double.parseDouble(prixField.getText()));
            
            if (eventDAO.update(currentEvent)) {
                clearForm();
                loadEvents();
                showAlert("Succès", "Événement modifié avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }
    
    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer l'événement");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (eventDAO.delete(selected.getId())) {
                    loadEvents();
                    showAlert("Succès", "Événement supprimé avec succès", Alert.AlertType.INFORMATION);
                }
            }
        }
    }
    
    @FXML
    private void handleViewEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
    
            showEventDetails(selected);
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadEvents();
        } else {
            List<Event> events = eventDAO.findByName(searchTerm);
            eventList = FXCollections.observableArrayList(events);
            eventTable.setItems(eventList);
        }
    }
    
    @FXML
    private void handleRowSelection() {
        currentEvent = eventTable.getSelectionModel().getSelectedItem();
        if (currentEvent != null) {
            populateForm(currentEvent);
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
    }
    
    private void populateForm(Event event) {
        nomField.setText(event.getNom());
        descriptionField.setText(event.getDescription());
        lieuField.setText(event.getLieu());
        prixField.setText(String.valueOf(event.getPrix()));
    }
    
    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        dateField.setValue(null);
        lieuField.clear();
        prixField.clear();
        currentEvent = null;
    }
    
    private void showEventDetails(Event event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'événement");
        alert.setHeaderText(event.getNom());
        alert.setContentText(
            "Description: " + event.getDescription() + "\n" +
            "Date: " + event.getDate() + "\n" +
            "Lieu: " + event.getLieu() + "\n" +
            "Prix: " + event.getPrix() + "€"
        );
        alert.showAndWait();
    }
    
    private boolean validateForm() {
        if (nomField.getText().isEmpty() || 
            dateField.getValue() == null || 
            lieuField.getText().isEmpty() ||
            prixField.getText().isEmpty()) {
            
            showAlert("Erreur", "Tous les champs obligatoires doivent être remplis", Alert.AlertType.ERROR);
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