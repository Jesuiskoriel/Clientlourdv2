package controllers;

import dao.EvenementDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Evenement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Gère la création, la mise à jour et la liste des événements.
public class EventController {

    @FXML private TableView<Evenement> eventTable;
    @FXML private TableColumn<Evenement, String> nomColumn;
    @FXML private TableColumn<Evenement, LocalDate> dateColumn;
    @FXML private TableColumn<Evenement, LocalTime> heureColumn;
    @FXML private TableColumn<Evenement, String> lieuColumn;
    @FXML private TableColumn<Evenement, Integer> capaciteColumn;
    @FXML private TableColumn<Evenement, Double> prixColumn;

    @FXML private TextField nomField;
    @FXML private DatePicker dateField;
    @FXML private TextField heureField;
    @FXML private TextField lieuField;
    @FXML private Spinner<Integer> capaciteSpinner;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionArea;

    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final ObservableList<Evenement> events = FXCollections.observableArrayList();
    private Evenement selectedEvent;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Configure l'UI et charge les événements.
    @FXML
    public void initialize() {
        configureTable();
        configureSpinner();
        loadEvents();
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            selectedEvent = val;
            if (val != null) {
                populateForm(val);
            } else {
                clearForm();
            }
        });
    }

    // Associe les colonnes aux propriétés du modèle.
    private void configureTable() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        heureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        capaciteColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prixBase"));
        eventTable.setItems(events);
    }

    // Configure la plage de capacité.
    private void configureSpinner() {
        capaciteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 100, 50));
    }

    // Recharge la table et nettoie la sélection.
    @FXML
    private void handleRefresh() {
        loadEvents();
        eventTable.getSelectionModel().clearSelection();
    }

    // Crée ou met à jour l'événement depuis le formulaire.
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Evenement evenement = selectedEvent == null ? new Evenement() : selectedEvent;
        evenement.setNom(nomField.getText().trim());
        evenement.setDateEvent(dateField.getValue());
        evenement.setHeure(LocalTime.parse(heureField.getText().trim(), timeFormatter));
        evenement.setLieu(lieuField.getText().trim());
        evenement.setCapacite(capaciteSpinner.getValue());
        evenement.setPrixBase(Double.parseDouble(prixField.getText().trim()));
        evenement.setDescription(descriptionArea.getText().trim());

        boolean success = selectedEvent == null
                ? evenementDAO.create(evenement)
                : evenementDAO.update(evenement);

        if (success) {
            loadEvents();
            clearForm();
            showAlert("Succès", "Événement enregistré.", Alert.AlertType.INFORMATION);
        }
    }

    // Supprime l'événement sélectionné.
    @FXML
    private void handleDelete() {
        Evenement toDelete = eventTable.getSelectionModel().getSelectedItem();
        if (toDelete == null) {
            showAlert("Information", "Sélectionnez un événement à supprimer.", Alert.AlertType.WARNING);
            return;
        }

        if (evenementDAO.delete(toDelete.getId())) {
            events.remove(toDelete);
            clearForm();
        }
    }

    // Vide le formulaire d'édition.
    @FXML
    private void handleClearForm() {
        eventTable.getSelectionModel().clearSelection();
        clearForm();
    }

    // Remplit les champs depuis l'événement choisi.
    private void populateForm(Evenement evenement) {
        nomField.setText(evenement.getNom());
        dateField.setValue(evenement.getDateEvent());
        heureField.setText(evenement.getHeure() != null ? evenement.getHeure().toString() : "");
        lieuField.setText(evenement.getLieu());
        capaciteSpinner.getValueFactory().setValue(evenement.getCapacite());
        prixField.setText(String.valueOf(evenement.getPrixBase()));
        descriptionArea.setText(evenement.getDescription());
    }

    // Réinitialise les champs et la sélection.
    private void clearForm() {
        nomField.clear();
        dateField.setValue(null);
        heureField.clear();
        lieuField.clear();
        capaciteSpinner.getValueFactory().setValue(100);
        prixField.clear();
        descriptionArea.clear();
        selectedEvent = null;
    }

    // Charge les événements depuis la base.
    private void loadEvents() {
        events.setAll(evenementDAO.findAll());
    }

    // Vérifie la validité des entrées du formulaire.
    private boolean validateForm() {
        if (nomField.getText().isBlank() || lieuField.getText().isBlank()) {
            showAlert("Erreur", "Nom et lieu sont obligatoires.", Alert.AlertType.ERROR);
            return false;
        }
        if (dateField.getValue() == null) {
            showAlert("Erreur", "La date est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }
        if (heureField.getText().isBlank()) {
            showAlert("Erreur", "L'heure est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }
        try {
            LocalTime.parse(heureField.getText().trim(), timeFormatter);
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide (HH:mm).", Alert.AlertType.ERROR);
            return false;
        }
        try {
            Double.parseDouble(prixField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    // Affiche une alerte simple.
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
