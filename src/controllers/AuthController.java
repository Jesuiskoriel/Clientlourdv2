package controllers;

import dao.ClientDAO;
import dao.UserDAO;
import dao.SecurityDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Client;
import model.SecurityQuestion;
import model.User;
import utils.PasswordUtils;
import utils.MailService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AuthController {

    @FXML private StackPane contentStack;
    @FXML private VBox landingPane;
    @FXML private VBox loginPane;
    @FXML private VBox registerPane;
    @FXML private VBox resetPane;
    @FXML private VBox otpPane;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;

    @FXML private TextField registerNameField;
    @FXML private TextField registerEmailField;
    @FXML private TextField registerPhoneField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmField;
    @FXML private ComboBox<SecurityQuestion> question1Combo;
    @FXML private ComboBox<SecurityQuestion> question2Combo;
    @FXML private ComboBox<SecurityQuestion> question3Combo;
    @FXML private TextField answer1Field;
    @FXML private TextField answer2Field;
    @FXML private TextField answer3Field;

    @FXML private TextField otpCodeField;
    @FXML private Label otpInfoLabel;

    @FXML private TextField resetEmailField;
    @FXML private TextField resetAnswer1Field;
    @FXML private TextField resetAnswer2Field;
    @FXML private TextField resetAnswer3Field;
    @FXML private PasswordField resetNewPasswordField;
    @FXML private PasswordField resetConfirmField;
    @FXML private Label resetQuestion1Label;
    @FXML private Label resetQuestion2Label;
    @FXML private Label resetQuestion3Label;
    @FXML private TextField resetOtpField;
    @FXML private Label resetOtpInfoLabel;

    private final UserDAO userDAO = new UserDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final SecurityDAO securityDAO = new SecurityDAO();
    private final MailService mailService = new MailService();
    private static final String ADMIN_EMAIL = "jhawadlajimi@hotmail.com";
    private static final String ADMIN_PASSWORD = "lajimi04";
    private User pendingUserForOtp;
    private User resetTargetUser;

    @FXML
    public void initialize() {
        // Prépare les données (questions, admin par défaut) et affiche l'écran d'accueil.
        securityDAO.ensureSetup();
        populateSecurityCombos();
        ensureDefaultAdmin();
        showPane(landingPane);
    }

    @FXML
    private void handleShowLanding() {
        showPane(landingPane);
    }

    @FXML
    private void handleShowLogin() {
        showPane(loginPane);
    }

    @FXML
    private void handleShowRegister() {
        showPane(registerPane);
    }

    @FXML
    private void handleShowReset() {
        showPane(resetPane);
        clearResetForm();
    }

    @FXML
    private void handleShowOtpPane() {
        showPane(otpPane);
    }

    @FXML
    private void handleSubmitRegister() {
        String nom = safeValue(registerNameField);
        String email = safeValue(registerEmailField).toLowerCase();
        String telephone = safeValue(registerPhoneField);
        String password = registerPasswordField.getText();
        String confirm = registerConfirmField.getText();
        SecurityQuestion q1 = question1Combo.getSelectionModel().getSelectedItem();
        SecurityQuestion q2 = question2Combo.getSelectionModel().getSelectedItem();
        SecurityQuestion q3 = question3Combo.getSelectionModel().getSelectedItem();
        String a1 = safeValue(answer1Field);
        String a2 = safeValue(answer2Field);
        String a3 = safeValue(answer3Field);

        if (nom.isBlank() || email.isBlank() || password == null || password.isBlank()) {
            showAlert("Champs manquants", "Veuillez renseigner au minimum nom, email et mot de passe.", Alert.AlertType.WARNING);
            return;
        }
        if (!isValidEmail(email)) {
            showAlert("Email invalide", "Merci de saisir un email valide.", Alert.AlertType.ERROR);
            return;
        }
        if (password.length() < 6) {
            showAlert("Mot de passe trop court", "Le mot de passe doit contenir au moins 6 caractères.", Alert.AlertType.WARNING);
            return;
        }
        if (!password.equals(confirm)) {
            showAlert("Confirmation", "Les deux mots de passe ne correspondent pas.", Alert.AlertType.WARNING);
            return;
        }
        if (!areQuestionsValid(q1, q2, q3, a1, a2, a3)) {
            return;
        }
        if (userDAO.emailExists(email)) {
            showAlert("Email déjà utilisé", "Un compte existe déjà avec cet email.", Alert.AlertType.ERROR);
            return;
        }

        User user = new User(nom, email, telephone, PasswordUtils.hashPassword(password));
        if (userDAO.create(user)) {
            saveSecurityAnswers(user, q1, q2, q3, a1, a2, a3);
            ensureClientEntry(nom, email, telephone);
            clearRegisterForm();
            userDAO.authenticate(email, password)
                    .ifPresentOrElse(this::startTwoFactor,
                            () -> showAlert("Compte créé", "Connexion automatique impossible, merci d'essayer de vous connecter.", Alert.AlertType.INFORMATION));
        } else {
            showAlert("Erreur", "Impossible de créer le compte. Vérifiez la base de données.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSubmitLogin() {
        String email = safeValue(loginEmailField).toLowerCase();
        String password = loginPasswordField.getText();

        if (email.isBlank() || password == null || password.isBlank()) {
            showAlert("Champs manquants", "Merci de saisir vos identifiants.", Alert.AlertType.WARNING);
            return;
        }
        Optional<User> user = userDAO.authenticate(email, password);
        if (user.isPresent()) {
            // Démarre la 2FA si les identifiants sont corrects.
            startTwoFactor(user.get());
        } else {
            showAlert("Connexion refusée", "Email ou mot de passe incorrect.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdminShortcut() {
        handleShowLogin();
        loginEmailField.setText(ADMIN_EMAIL);
        loginPasswordField.setText("");
        loginPasswordField.setPromptText("Mot de passe admin");
    }

    @FXML
    private void handleForgotPassword() {
        handleShowReset();
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentStack.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            URL css = getClass().getResource("/views/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Système de Gestion de Billetterie");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface principale : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openStoreScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/store/store.fxml"));
            Parent root = loader.load();
            StoreController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) contentStack.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            URL css = getClass().getResource("/views/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Billetterie - Mes billets");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la boutique de billets.", Alert.AlertType.ERROR);
        }
    }

    private void showPane(VBox target) {
        VBox[] panes = {landingPane, loginPane, registerPane, resetPane, otpPane};
        for (VBox pane : panes) {
            boolean show = pane == target;
            pane.setManaged(show);
            pane.setVisible(show);
        }
    }

    private void clearRegisterForm() {
        registerNameField.clear();
        registerEmailField.clear();
        registerPhoneField.clear();
        registerPasswordField.clear();
        registerConfirmField.clear();
        question1Combo.getSelectionModel().clearSelection();
        question2Combo.getSelectionModel().clearSelection();
        question3Combo.getSelectionModel().clearSelection();
        answer1Field.clear();
        answer2Field.clear();
        answer3Field.clear();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$");
    }

    private String safeValue(TextField field) {
        return field != null && field.getText() != null ? field.getText().trim() : "";
    }

    private void populateSecurityCombos() {
        List<SecurityQuestion> questions = securityDAO.getQuestions();
        question1Combo.getItems().setAll(questions);
        question2Combo.getItems().setAll(questions);
        question3Combo.getItems().setAll(questions);
    }

    private boolean areQuestionsValid(SecurityQuestion q1, SecurityQuestion q2, SecurityQuestion q3,
                                      String a1, String a2, String a3) {
        if (q1 == null || q2 == null || q3 == null) {
            showAlert("Questions manquantes", "Choisissez 3 questions de sécurité.", Alert.AlertType.WARNING);
            return false;
        }
        if (q1.getId() == q2.getId() || q1.getId() == q3.getId() || q2.getId() == q3.getId()) {
            showAlert("Questions identiques", "Les 3 questions doivent être différentes.", Alert.AlertType.WARNING);
            return false;
        }
        if (a1.isBlank() || a2.isBlank() || a3.isBlank()) {
            showAlert("Réponses manquantes", "Merci de renseigner vos réponses de sécurité.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void saveSecurityAnswers(User user, SecurityQuestion q1, SecurityQuestion q2, SecurityQuestion q3,
                                     String a1, String a2, String a3) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(q1.getId(), PasswordUtils.hashPassword(a1));
        map.put(q2.getId(), PasswordUtils.hashPassword(a2));
        map.put(q3.getId(), PasswordUtils.hashPassword(a3));
        securityDAO.saveSecurityAnswers(user.getId(), map);
    }

    private void startTwoFactor(User user) {
        this.pendingUserForOtp = user;
        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);
        securityDAO.storeOtp(user.getId(), code, expiry);
        otpCodeField.clear();
        if (otpInfoLabel != null) {
            otpInfoLabel.setText("Un code vient de vous être envoyé par email.");
            if (!mailService.isEnabled()) {
                otpInfoLabel.setText("Un code aurait dû être envoyé. Configurez SMTP pour le recevoir.");
            }
        }
        if (mailService.isEnabled()) {
            mailService.sendEmail(user.getEmail(), "Code de vérification", "Votre code est : " + code);
        }
        showPane(otpPane);
    }

    @FXML
    private void handleValidateOtp() {
        if (pendingUserForOtp == null) {
            showAlert("Session expirée", "Recommencez la connexion.", Alert.AlertType.WARNING);
            showPane(loginPane);
            return;
        }
        String code = safeValue(otpCodeField);
        Optional<SecurityDAO.OtpToken> tokenOpt = securityDAO.getOtp(pendingUserForOtp.getId());
        if (tokenOpt.isEmpty()) {
            showAlert("Code expiré", "Demandez un nouveau code.", Alert.AlertType.WARNING);
            return;
        }
        SecurityDAO.OtpToken token = tokenOpt.get();
        if (token.expiresAt != null && token.expiresAt.isBefore(LocalDateTime.now())) {
            showAlert("Code expiré", "Demandez un nouveau code.", Alert.AlertType.WARNING);
            return;
        }
        if (!code.equals(token.code)) {
            showAlert("Code incorrect", "Le code saisi n'est pas valide.", Alert.AlertType.ERROR);
            return;
        }
        securityDAO.clearOtp(pendingUserForOtp.getId());
        User user = pendingUserForOtp;
        pendingUserForOtp = null;
        if (user.isAdmin()) {
            openMainScene();
        } else {
            openStoreScene(user);
        }
    }

    @FXML
    private void handleResendOtp() {
        if (pendingUserForOtp == null) {
            showAlert("Session expirée", "Recommencez la connexion.", Alert.AlertType.WARNING);
            showPane(loginPane);
            return;
        }
        startTwoFactor(pendingUserForOtp);
    }

    @FXML
    private void handleLoadResetQuestions() {
        String email = safeValue(resetEmailField).toLowerCase();
        if (email.isBlank()) {
            showAlert("Email requis", "Saisissez votre email.", Alert.AlertType.WARNING);
            return;
        }
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) {
            showAlert("Introuvable", "Aucun compte associé à cet email.", Alert.AlertType.ERROR);
            return;
        }
        resetTargetUser = userOpt.get();
        List<SecurityQuestion> qs = securityDAO.findUserQuestions(resetTargetUser.getId());
        if (qs.size() < 3) {
            showAlert("Questions manquantes", "Aucune question enregistrée pour ce compte.", Alert.AlertType.ERROR);
            return;
        }
        resetQuestion1Label.setText(qs.get(0).getLibelle());
        resetQuestion2Label.setText(qs.get(1).getLibelle());
        resetQuestion3Label.setText(qs.get(2).getLibelle());

        // Envoi OTP reset
        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);
        securityDAO.storeOtp(resetTargetUser.getId(), code, expiry);
        if (mailService.isEnabled()) {
            mailService.sendEmail(resetTargetUser.getEmail(), "Code de réinitialisation", "Votre code reset est : " + code);
            if (resetOtpInfoLabel != null) {
                resetOtpInfoLabel.setText("Code envoyé sur votre email.");
            }
        } else if (resetOtpInfoLabel != null) {
            resetOtpInfoLabel.setText("Configurez SMTP pour recevoir le code par email.");
        }
    }

    @FXML
    private void handleSubmitReset() {
        if (resetTargetUser == null) {
            showAlert("Chargement requis", "Chargez d'abord les questions pour votre email.", Alert.AlertType.WARNING);
            return;
        }
        String newPass = resetNewPasswordField.getText();
        String confirm = resetConfirmField.getText();
        if (newPass == null || newPass.isBlank() || confirm == null || confirm.isBlank()) {
            showAlert("Mot de passe requis", "Saisissez et confirmez votre nouveau mot de passe.", Alert.AlertType.WARNING);
            return;
        }
        if (!newPass.equals(confirm)) {
            showAlert("Confirmation", "Les deux mots de passe ne correspondent pas.", Alert.AlertType.WARNING);
            return;
        }
        String otp = safeValue(resetOtpField);
        Optional<SecurityDAO.OtpToken> otpToken = securityDAO.getOtp(resetTargetUser.getId());
        if (otpToken.isEmpty() || otpToken.get().expiresAt.isBefore(LocalDateTime.now()) || !otp.equals(otpToken.get().code)) {
            showAlert("OTP invalide", "Code expiré ou incorrect.", Alert.AlertType.ERROR);
            return;
        }
        HashMap<Integer, String> provided = new HashMap<>();
        List<SecurityQuestion> qs = securityDAO.findUserQuestions(resetTargetUser.getId());
        if (qs.size() >= 3) {
            provided.put(qs.get(0).getId(), PasswordUtils.hashPassword(safeValue(resetAnswer1Field)));
            provided.put(qs.get(1).getId(), PasswordUtils.hashPassword(safeValue(resetAnswer2Field)));
            provided.put(qs.get(2).getId(), PasswordUtils.hashPassword(safeValue(resetAnswer3Field)));
        }
        if (!securityDAO.validateAnswers(resetTargetUser.getId(), provided)) {
            showAlert("Réponses incorrectes", "Les réponses fournies ne correspondent pas.", Alert.AlertType.ERROR);
            return;
        }
        if (userDAO.updatePassword(resetTargetUser.getId(), PasswordUtils.hashPassword(newPass))) {
            showAlert("Succès", "Mot de passe réinitialisé. Connectez-vous avec le nouveau mot de passe.", Alert.AlertType.INFORMATION);
            clearResetForm();
            showPane(loginPane);
        } else {
            showAlert("Erreur", "Impossible de mettre à jour le mot de passe.", Alert.AlertType.ERROR);
        }
    }

    private void clearResetForm() {
        resetEmailField.clear();
        resetAnswer1Field.clear();
        resetAnswer2Field.clear();
        resetAnswer3Field.clear();
        resetNewPasswordField.clear();
        resetConfirmField.clear();
        resetQuestion1Label.setText("");
        resetQuestion2Label.setText("");
        resetQuestion3Label.setText("");
        resetOtpField.clear();
        if (resetOtpInfoLabel != null) {
            resetOtpInfoLabel.setText("Un code vous sera envoyé par email.");
        }
        resetTargetUser = null;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void ensureDefaultAdmin() {
        // Crée un compte admin par défaut si absent (utilisé pour la première connexion).
        if (!userDAO.emailExists(ADMIN_EMAIL)) {
            User admin = new User("Administrateur", ADMIN_EMAIL, "0000000000", PasswordUtils.hashPassword(ADMIN_PASSWORD));
            admin.setAdmin(true);
            admin.setSolde(0);
            userDAO.create(admin);
            ensureClientEntry(admin.getNomComplet(), ADMIN_EMAIL, admin.getTelephone());
        }
    }

    private void ensureClientEntry(String fullName, String email, String telephone) {
        if (clientDAO.findByEmail(email).isPresent()) {
            return;
        }
        String[] names = splitName(fullName);
        Client client = new Client();
        client.setPrenom(names[0]);
        client.setNom(names[1]);
        client.setEmail(email);
        client.setTelephone(telephone);
        client.setVille("Compte en ligne");
        clientDAO.create(client);
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Utilisateur", "Inconnu"};
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return parts;
    }
}
