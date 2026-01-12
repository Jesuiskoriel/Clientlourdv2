import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            // Charge la première scène (écran d'authentification) depuis le FXML.
            URL fxmlUrl = getClass().getResource("/views/auth/auth.fxml");

            if (fxmlUrl == null) {
                throw new RuntimeException("Impossible de trouver /views/auth/auth.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            // Ajout de la feuille de style globale (thème).
            URL css = getClass().getResource("/views/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            primaryStage.setTitle("Système de Gestion de Billetterie");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("ERREUR AU CHARGEMENT DE L'APPLICATION");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
