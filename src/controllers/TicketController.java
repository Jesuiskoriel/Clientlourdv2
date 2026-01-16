package controllers;

import dao.BilletDAO;
import dao.ClientDAO;
import dao.StatutBilletDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import model.Billet;
import model.Client;
import model.StatutBillet;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Gère la liste des billets, filtres, transferts et remboursements.
public class TicketController {

    @FXML private TableView<Billet> ticketTable;
    @FXML private TableColumn<Billet, String> codeColumn;
    @FXML private TableColumn<Billet, String> clientColumn;
    @FXML private TableColumn<Billet, String> eventColumn;
    @FXML private TableColumn<Billet, String> statutColumn;
    @FXML private TableColumn<Billet, String> dateColumn;
    @FXML private TableColumn<Billet, Number> prixColumn;

    @FXML private TextField filterField;
    @FXML private TextField transferEmailField;
    @FXML private ComboBox<StatutBillet> statutCombo;

    private final BilletDAO billetDAO = new BilletDAO();
    private final StatutBilletDAO statutBilletDAO = new StatutBilletDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ObservableList<Billet> billets = FXCollections.observableArrayList();
    private final ObservableList<Billet> filteredBillets = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Configure la table et charge les billets.
    @FXML
    public void initialize() {
        configureTable();
        configureStatutCombo();
        loadTickets();
    }

    // Lie les colonnes aux données des billets.
    private void configureTable() {
        codeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodeUnique()));
        clientColumn.setCellValueFactory(data -> {
            String label = data.getValue().getClient() != null
                    ? data.getValue().getClient().getNom() + " " + data.getValue().getClient().getPrenom()
                    : "";
            return new SimpleStringProperty(label);
        });
        eventColumn.setCellValueFactory(data -> {
            String label = data.getValue().getEvenement() != null
                    ? data.getValue().getEvenement().getNom()
                    : "";
            return new SimpleStringProperty(label);
        });
        statutColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatut() != null ? data.getValue().getStatut().getLibelle() : ""
        ));
        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().getDateAchat() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(dateFormatter.format(data.getValue().getDateAchat()));
        });
        prixColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrixPaye()));
        ticketTable.setItems(filteredBillets);
    }

    // Charge la liste des statuts possibles.
    private void configureStatutCombo() {
        List<StatutBillet> statuts = statutBilletDAO.findAll();
        statutCombo.setItems(FXCollections.observableArrayList(statuts));
        statutCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(StatutBillet statutBillet) {
                return statutBillet != null ? statutBillet.getLibelle() : "";
            }

            @Override
            public StatutBillet fromString(String string) {
                return statutCombo.getItems().stream()
                        .filter(s -> s.getLibelle().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    // Recharge la liste complète.
    @FXML
    private void handleRefresh() {
        loadTickets();
    }

    // Filtre la liste selon le champ de recherche.
    @FXML
    private void handleFilter() {
        String term = filterField.getText();
        if (term == null || term.isBlank()) {
            filteredBillets.setAll(billets);
            return;
        }
        String lower = term.toLowerCase();
        filteredBillets.setAll(billets.stream()
                .filter(b -> matchesFilter(b, lower))
                .collect(Collectors.toList()));
    }

    // Met à jour le statut d'un billet.
    @FXML
    private void handleChangeStatut() {
        Billet billet = ticketTable.getSelectionModel().getSelectedItem();
        StatutBillet statut = statutCombo.getSelectionModel().getSelectedItem();
        if (billet == null || statut == null) {
            showAlert("Information", "Sélectionnez un billet et un statut.", Alert.AlertType.WARNING);
            return;
        }
        if (billetDAO.updateStatut(billet.getId(), statut.getId())) {
            billet.setStatut(statut);
            ticketTable.refresh();
            showAlert("Succès", "Statut mis à jour.", Alert.AlertType.INFORMATION);
        }
    }

    // Transfère un billet vers un autre client.
    @FXML
    private void handleTransferTicket() {
        Billet billet = ticketTable.getSelectionModel().getSelectedItem();
        String email = transferEmailField != null ? transferEmailField.getText() : null;
        if (billet == null) {
            showAlert("Sélection requise", "Choisissez un billet à transférer.", Alert.AlertType.WARNING);
            return;
        }
        if (email == null || email.isBlank()) {
            showAlert("Email manquant", "Renseignez l'email du client destinataire.", Alert.AlertType.WARNING);
            return;
        }
        Optional<Client> clientOpt = clientDAO.findByEmail(email.trim());
        if (clientOpt.isEmpty()) {
            showAlert("Client introuvable", "Aucun client avec cet email.", Alert.AlertType.ERROR);
            return;
        }
        Client client = clientOpt.get();
        if (billetDAO.updateClient(billet.getId(), client.getId())) {
            billet.setClient(client);
            ticketTable.refresh();
            transferEmailField.clear();
            showAlert("Transfert confirmé", "Le billet a été associé à " + client.getNom() + " " + client.getPrenom(),
                    Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de transférer ce billet.", Alert.AlertType.ERROR);
        }
    }

    // Supprime un billet et rembourse l'achat.
    @FXML
    private void handleRefundTicket() {
        Billet billet = ticketTable.getSelectionModel().getSelectedItem();
        if (billet == null) {
            showAlert("Sélection requise", "Choisissez un billet à rembourser.", Alert.AlertType.WARNING);
            return;
        }
        Optional<ButtonType> confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmer le remboursement du billet " + billet.getCodeUnique() + " ?", ButtonType.OK, ButtonType.CANCEL)
                .showAndWait();
        if (confirmation.isEmpty() || confirmation.get() != ButtonType.OK) {
            return;
        }
        if (billetDAO.delete(billet.getId())) {
            billets.remove(billet);
            filteredBillets.remove(billet);
            ticketTable.refresh();
            showAlert("Remboursement effectué", "Le billet a été supprimé.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de rembourser ce billet.", Alert.AlertType.ERROR);
        }
    }

    // Charge tous les billets depuis la base.
    private void loadTickets() {
        billets.setAll(billetDAO.findAll());
        filteredBillets.setAll(billets);
    }

    // Vérifie si un billet correspond au filtre texte.
    private boolean matchesFilter(Billet billet, String term) {
        if (billet.getClient() != null) {
            if (billet.getClient().getNom() != null
                    && billet.getClient().getNom().toLowerCase().contains(term)) {
                return true;
            }
            if (billet.getClient().getPrenom() != null
                    && billet.getClient().getPrenom().toLowerCase().contains(term)) {
                return true;
            }
        }
        if (billet.getEvenement() != null && billet.getEvenement().getNom() != null
                && billet.getEvenement().getNom().toLowerCase().contains(term)) {
            return true;
        }
        return billet.getCodeUnique() != null && billet.getCodeUnique().toLowerCase().contains(term);
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
