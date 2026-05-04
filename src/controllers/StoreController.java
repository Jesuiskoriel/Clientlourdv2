package controllers;

import dao.AchatDAO;
import dao.EvenementDAO;
import dao.UserDAO;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import model.Achat;
import model.Evenement;
import model.User;
import utils.EventRecommendationService;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.util.Duration;
import javax.imageio.ImageIO;

// Contrôleur de la boutique utilisateur (achat de billets + solde).
public class StoreController {

    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label feedbackLabel;
    @FXML private Label rechargeFeedback;
    @FXML private TextField rechargeField;

    @FXML private FlowPane eventGrid;
    @FXML private VBox recommendationBox;

    @FXML private TableView<Achat> purchaseTable;
    @FXML private TableColumn<Achat, String> purchaseEventColumn;
    @FXML private TableColumn<Achat, Number> purchasePriceColumn;
    @FXML private TableColumn<Achat, String> purchaseDateColumn;

    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final AchatDAO achatDAO = new AchatDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Evenement> events = FXCollections.observableArrayList();
    private final ObservableList<Achat> achats = FXCollections.observableArrayList();
    private final ObservableList<Evenement> recommendedEvents = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private User currentUser;
    private Evenement selectedEvent;
    private VBox selectedCard;

    // Prépare la table des achats.
    @FXML
    public void initialize() {
        configureTables();
    }

    // Injecte l'utilisateur connecté et charge ses données.
    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Bonjour " + user.getNomComplet());
        updateBalanceLabel();
        loadPurchases();
        loadEvents();
    }

    // Configure les colonnes de la table des achats.
    private void configureTables() {
        purchaseEventColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        purchaseDateColumn.setCellValueFactory(cellData -> {
            Achat achat = cellData.getValue();
            String formatted = achat.getDateAchat() != null ? formatter.format(achat.getDateAchat()) : "";
            return new SimpleStringProperty(formatted);
        });
        purchaseTable.setItems(achats);
        purchaseTable.setRowFactory(tv -> {
            TableRow<Achat> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showTicketQr(row.getItem());
                }
            });
            return row;
        });
    }

    // Charge les événements et génère les cartes.
    private void loadEvents() {
        events.setAll(evenementDAO.findAll());
        renderEventCards();
        refreshRecommendations();
    }

    // Charge les achats de l'utilisateur.
    private void loadPurchases() {
        if (currentUser != null) {
            achats.setAll(achatDAO.findByUser(currentUser.getId()));
            refreshRecommendations();
        }
    }

    // Achète un billet pour l'événement sélectionné.
    @FXML
    private void handleBuyTicket() {
        if (currentUser == null) {
            return;
        }
        if (selectedEvent == null) {
            showAlert("Sélection requise", "Choisissez un événement à acheter.", Alert.AlertType.INFORMATION);
            return;
        }
        double prix = selectedEvent.getPrixBase();
        int placesRestantes = getPlacesRestantes(selectedEvent);
        if (placesRestantes <= 0) {
            showAlert("Complet", "Cet événement est complet. Choisissez-en un autre.", Alert.AlertType.INFORMATION);
            return;
        }
        if (currentUser.getSolde() < prix) {
            showAlert("Solde insuffisant", "Votre solde ne permet pas cet achat.", Alert.AlertType.WARNING);
            return;
        }
        boolean saved = achatDAO.create(currentUser.getId(), selectedEvent.getId(), prix);
        if (saved && userDAO.updateSolde(currentUser.getId(), currentUser.getSolde() - prix)) {
            currentUser.setSolde(currentUser.getSolde() - prix);
            updateBalanceLabel();
            feedbackLabel.setText("Achat confirmé pour " + selectedEvent.getNom());
            loadPurchases();
            loadEvents();
        } else {
            showAlert("Erreur", "Impossible d'enregistrer l'achat.", Alert.AlertType.ERROR);
        }
    }

    // Affiche le QR du billet sélectionné.
    @FXML
    private void handleShowTicketQr() {
        Achat achat = purchaseTable.getSelectionModel().getSelectedItem();
        if (achat == null) {
            showAlert("Sélection requise", "Choisissez un achat dans la liste.", Alert.AlertType.INFORMATION);
            return;
        }
        showTicketQr(achat);
    }

    // Démarre une recharge de solde.
    @FXML
    private void handleAddFunds() {
        if (currentUser == null) {
            return;
        }
        String value = rechargeField != null ? rechargeField.getText() : "";
        double amount;
        try {
            amount = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            showAlert("Montant invalide", "Saisissez un nombre valide.", Alert.AlertType.WARNING);
            return;
        }
        if (amount <= 0) {
            showAlert("Montant invalide", "Le montant doit être positif.", Alert.AlertType.WARNING);
            return;
        }
        showCardPaymentDialog(amount);
    }

    // Déconnecte l'utilisateur et revient à l'auth.
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/auth.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            URL css = getClass().getResource("/views/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Système de Gestion de Billetterie");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à l'écran de connexion.", Alert.AlertType.ERROR);
        }
    }

    // Met à jour l'affichage du solde.
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Solde : %.2f €", currentUser.getSolde()));
    }

    // Calcule les places restantes pour un événement.
    private int getPlacesRestantes(Evenement event) {
        if (event == null) {
            return 0;
        }
        int achetes = achatDAO.countByEvent(event.getId());
        int restantes = event.getCapacite() - achetes;
        return Math.max(restantes, 0);
    }

    // Génère les cartes d'événements dans la grille.
    private void renderEventCards() {
        if (eventGrid == null) {
            return;
        }
        eventGrid.getChildren().clear();
        for (Evenement event : events) {
            VBox card = createEventCard(event);
            eventGrid.getChildren().add(card);
        }
    }

    // Recalcule et affiche les recommandations personnalisees.
    private void refreshRecommendations() {
        if (recommendationBox == null) {
            return;
        }

        recommendationBox.getChildren().clear();
        recommendedEvents.setAll(EventRecommendationService.recommend(events, achats, 3));

        if (recommendedEvents.isEmpty()) {
            Label empty = new Label("Aucune recommandation pour le moment.");
            empty.setStyle("-fx-text-fill: #6c757d;");
            recommendationBox.getChildren().add(empty);
            return;
        }

        for (Evenement event : recommendedEvents) {
            recommendationBox.getChildren().add(createRecommendationCard(event));
        }
    }

    // Cree une carte compacte pour les recommandations.
    private VBox createRecommendationCard(Evenement event) {
        Label badge = new Label("Pour vous");
        badge.setStyle("-fx-background-color: #e7f5ff; -fx-text-fill: #1971c2; -fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 999;");

        Label title = new Label(eventTitle(event));
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #1f2937;");
        title.setWrapText(true);

        Label meta = new Label(formatDate(event.getDateEvent(), event.getHeure()) + " • " + (event.getLieu() != null ? event.getLieu() : "Lieu à venir"));
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");
        meta.setWrapText(true);

        Label price = new Label(String.format("%.2f €", event.getPrixBase()));
        price.setStyle("-fx-text-fill: #087f5b; -fx-font-weight: bold;");

        Button chooseButton = new Button("Choisir");
        chooseButton.setStyle("-fx-background-color: #364fc7; -fx-text-fill: white;");
        chooseButton.setOnAction(e -> {
            selectedEvent = event;
            selectedCard = null;
            feedbackLabel.setText("Recommandé sélectionné : " + eventTitle(event));
            showAlert("Événement sélectionné", "L'événement recommandé a été sélectionné. Vous pouvez maintenant l'acheter.", Alert.AlertType.INFORMATION);
        });

        HBox footer = new HBox(10, price, chooseButton);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, badge, title, meta, footer);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #dbe4ff; -fx-border-radius: 12;");
        return card;
    }

    // Crée une carte visuelle pour un événement.
    private VBox createEventCard(Evenement event) {
        ImageView poster = new ImageView(getPosterImage(event));
        poster.setFitWidth(180);
        poster.setFitHeight(260);
        poster.setSmooth(true);
        poster.setPreserveRatio(false);

        Label title = new Label(eventTitle(event));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #fff; -fx-font-size: 14;");
        title.setWrapText(true);

        String dateStr = formatDate(event.getDateEvent(), event.getHeure());
        Label date = new Label(dateStr);
        date.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 12;");

        Label lieu = new Label(event.getLieu());
        lieu.setStyle("-fx-text-fill: rgba(255,255,255,0.75); -fx-font-size: 12;");

        Label price = new Label(String.format("%.2f €", event.getPrixBase()));
        price.setStyle("-fx-text-fill: #ffd43b; -fx-font-weight: bold;");

        int restantes = getPlacesRestantes(event);
        Label places = new Label(restantes + " places");
        places.getStyleClass().add("tag-pill");
        places.setStyle(places.getStyle() + "; -fx-text-fill: #fff; -fx-background-color: rgba(0,0,0,0.35);");

        HBox footer = new HBox(8, price, places);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4, title, date, lieu, footer);
        info.setPadding(new Insets(8, 10, 10, 10));
        info.setStyle("-fx-background-color: rgba(0,0,0,0.55);");

        VBox card = new VBox(poster, info);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0.2, 0, 4);");
        card.setPadding(new Insets(0,0,0,0));
        card.setOnMouseClicked(e -> setSelectedEvent(event, card));
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "; -fx-translate-y: -2;"));
        card.setOnMouseExited(e -> {
            if (card != selectedCard) {
                card.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0.2, 0, 4);");
            }
        });
        return card;
    }

    // Marque une carte comme sélectionnée.
    private void setSelectedEvent(Evenement event, VBox card) {
        this.selectedEvent = event;
        if (selectedCard != null) {
            selectedCard.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0.2, 0, 4);");
        }
        selectedCard = card;
        selectedCard.setStyle("-fx-background-color: linear-gradient(to bottom, #3b82f6, #0f172a); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.45), 16, 0.35, 0, 6);");
        feedbackLabel.setText("Sélectionné : " + eventTitle(event));
    }

    // Récupère ou génère l'image d'affiche de l'événement.
    private Image getPosterImage(Evenement event) {
        File posterDir = new File(System.getProperty("user.dir"), "posters");
        if (!posterDir.exists()) {
            posterDir.mkdirs();
        }
        String safeName = "event-" + event.getId() + ".png";
        File posterFile = new File(posterDir, safeName);
        if (posterFile.exists()) {
            return new Image(posterFile.toURI().toString(), false);
        }
        Image generated = createPosterImage(event);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(generated, null), "png", posterFile);
        } catch (IOException e) {
            // ignore save error, we still return generated image
        }
        return generated;
    }

    // Génère une affiche graphique (gradient + texte).
    private Image createPosterImage(Evenement event) {
        int width = 360;
        int height = 520;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int seed = event.getNom() != null ? event.getNom().hashCode() : event.getId();
        java.util.Random r = new java.util.Random(seed);
        Color c1 = Color.hsb((seed % 360 + 360) % 360, 0.55 + r.nextDouble() * 0.2, 0.9);
        Color c2 = Color.hsb((seed * 1.3 % 360 + 360) % 360, 0.65, 0.35 + r.nextDouble() * 0.2);

        gc.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, c1),
                new javafx.scene.paint.Stop(1, c2)));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.rgb(0, 0, 0, 0.35));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial Black", 28));
        String title = eventTitle(event);
        wrapText(gc, title, 24, 360, 40, 260);

        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.setFill(Color.rgb(255, 212, 59));
        gc.fillText(formatDate(event.getDateEvent(), event.getHeure()), 20, height - 70);

        gc.setFill(Color.WHITE);
        gc.fillText(event.getLieu() != null ? event.getLieu() : "Lieu à venir", 20, height - 40);

        WritableImage snapshot = new WritableImage(width, height);
        canvas.snapshot(null, snapshot);
        return snapshot;
    }

    // Écrit un texte multi-lignes dans l'affiche.
    private void wrapText(GraphicsContext gc, String text, int maxLines, double maxWidth, double x, double y) {
        if (text == null) return;
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        Font font = gc.getFont();

        for (String word : words) {
            String candidate = line + (line.length() == 0 ? "" : " ") + word;
            if (computeWidth(candidate, font) > maxWidth) {
                gc.fillText(line.toString(), x, y + lineCount * 34);
                line = new StringBuilder(word);
                lineCount++;
                if (lineCount >= maxLines) break;
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (lineCount < maxLines) {
            gc.fillText(line.toString(), x, y + lineCount * 34);
        }
    }

    // Calcule la largeur d'un texte pour le wrapping.
    private double computeWidth(String value, Font font) {
        Text t = new Text(value);
        t.setFont(font);
        return t.getLayoutBounds().getWidth();
    }

    // Formate une date/heure en une ligne lisible.
    private String formatDate(LocalDate date, LocalTime time) {
        String dateStr = date != null ? date.toString() : "";
        if (time != null) {
            dateStr += " " + time.toString();
        }
        return dateStr.trim();
    }

    // Construit un titre affichable pour la carte.
    private String eventTitle(Evenement event) {
        String title = event != null && event.getNom() != null && !event.getNom().isBlank()
                ? event.getNom()
                : "Pièce de théâtre";
        return "🎭 " + title;
    }

    // Ouvre une fenêtre avec un QR (factice) du billet.
    private void showTicketQr(Achat achat) {
        String code = buildTicketCode(achat);
        Image qr = generateFakeQr(code, 260);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Billet - QR code");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ImageView imageView = new ImageView(qr);
        imageView.setFitWidth(260);
        imageView.setFitHeight(260);

        Label eventLabel = new Label("Événement : " + achat.getEventName());
        Label codeLabel = new Label("Code billet : " + code);

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(12, eventLabel, imageView, codeLabel);
        box.setStyle("-fx-padding: 12;");
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    // Construit un code unique de billet.
    private String buildTicketCode(Achat achat) {
        return "TKT-" + achat.getId() + "-" + achat.getUserId() + "-" + achat.getEventId();
    }

    // Génère un QR fictif (pattern unique) sans dépendance externe
    // Construit un faux QR pour la démo.
    private Image generateFakeQr(String seed, int size) {
        int grid = 29;
        double cellSize = (double) size / grid;
        WritableImage image = new WritableImage(size, size);
        PixelWriter pw = image.getPixelWriter();

        // fond blanc
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pw.setColor(x, y, Color.WHITE);
            }
        }

        java.util.Random random = new java.util.Random(seed.hashCode());

        // pattern finder simplifié aux coins pour le look
        drawFinder(pw, 2, 2, cellSize);
        drawFinder(pw, grid - 9, 2, cellSize);
        drawFinder(pw, 2, grid - 9, cellSize);

        for (int gy = 0; gy < grid; gy++) {
            for (int gx = 0; gx < grid; gx++) {
                // évite d'écraser les finders
                if (isInsideFinder(gx, gy, 2, 2) ||
                    isInsideFinder(gx, gy, grid - 9, 2) ||
                    isInsideFinder(gx, gy, 2, grid - 9)) {
                    continue;
                }
                boolean black = random.nextBoolean();
                Color color = black ? Color.BLACK : Color.WHITE;
                fillCell(pw, gx, gy, cellSize, color);
            }
        }
        return image;
    }

    // Dessine un pattern "finder" simplifié.
    private void drawFinder(PixelWriter pw, int gx, int gy, double cell) {
        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                boolean border = x == 0 || y == 0 || x == 6 || y == 6;
                boolean center = x >= 2 && x <= 4 && y >= 2 && y <= 4;
                Color color = (border || center) ? Color.BLACK : Color.WHITE;
                fillCell(pw, gx + x, gy + y, cell, color);
            }
        }
    }

    // Détecte si un point est dans un finder.
    private boolean isInsideFinder(int gx, int gy, int fx, int fy) {
        return gx >= fx && gx < fx + 7 && gy >= fy && gy < fy + 7;
    }

    // Remplit une cellule du QR à une couleur donnée.
    private void fillCell(PixelWriter pw, int gx, int gy, double cell, Color color) {
        int startX = (int) Math.round(gx * cell);
        int startY = (int) Math.round(gy * cell);
        int endX = (int) Math.round((gx + 1) * cell);
        int endY = (int) Math.round((gy + 1) * cell);
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                pw.setColor(x, y, color);
            }
        }
    }

    // Affiche une alerte utilisateur.
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Affiche un mini formulaire de carte bancaire et valide toujours le paiement après un court délai.
    // Simule un paiement par carte pour recharger le solde.
    private void showCardPaymentDialog(double amount) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Paiement par carte");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        TextField cardNumber = new TextField();
        cardNumber.setPromptText("Numéro de carte");
        TextField holder = new TextField();
        holder.setPromptText("Nom du titulaire");
        TextField expiry = new TextField();
        expiry.setPromptText("MM/AA");
        TextField cvv = new TextField();
        cvv.setPromptText("CVV");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setPrefSize(28, 28);
        Label status = new Label();

        javafx.scene.control.Button payBtn = new javafx.scene.control.Button(
                "Payer " + String.format("%.2f €", amount));
        payBtn.setDefaultButton(true);
        payBtn.setOnAction((ActionEvent ev) -> {
            // Pas de vraie validation : on simule un paiement toujours accepté.
            payBtn.setDisable(true);
            cardNumber.setDisable(true);
            holder.setDisable(true);
            expiry.setDisable(true);
            cvv.setDisable(true);
            spinner.setVisible(true);
            status.setText("Traitement du paiement...");
            PauseTransition pause = new PauseTransition(Duration.seconds(1.4));
            pause.setOnFinished(done -> {
                dialog.close();
                applyRecharge(amount);
            });
            pause.play();
        });

        VBox content = new VBox(10,
                new Label("Montant : " + String.format("%.2f €", amount)),
                holder,
                cardNumber,
                new HBox(8, expiry, cvv),
                new HBox(8, spinner, status),
                payBtn
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // Applique la recharge au solde et met à jour l'UI.
    private void applyRecharge(double amount) {
        double newSolde = currentUser.getSolde() + amount;
        if (userDAO.updateSolde(currentUser.getId(), newSolde)) {
            currentUser.setSolde(newSolde);
            updateBalanceLabel();
            if (rechargeFeedback != null) {
                rechargeFeedback.setText(String.format("+%.2f € ajoutés ✔ (paiement accepté)", amount));
            }
            if (rechargeField != null) {
                rechargeField.clear();
            }
        } else {
            showAlert("Erreur", "Impossible de mettre à jour le solde.", Alert.AlertType.ERROR);
        }
    }
}
