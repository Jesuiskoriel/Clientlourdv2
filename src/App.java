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
            // 🔎 Vérification explicite du chemin du FXML
            URL fxmlUrl = getClass().getResource("/views/main.fxml");

            if (fxmlUrl == null) {
                throw new RuntimeException("Impossible de trouver /views/main.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);

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
