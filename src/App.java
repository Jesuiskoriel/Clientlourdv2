import javafx.application.Application;

// Point d'entrée neutre pour éviter la détection JavaFX prématurée de `java -jar`.
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        Application.launch(FxApp.class, args);
    }
}
