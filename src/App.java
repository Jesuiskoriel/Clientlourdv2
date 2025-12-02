import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {

        Parent root;

        try {
            
            root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
            Scene scene = new Scene(root, 800, 600); 

            primaryStage.setTitle("Système de Gestion de Billetterie");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
// import javafx.application.Application;
// import jjava java avafx.event.ActionEvent;
// import javafx.event.EventHandler;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;
 
// public class App extends Application {
//     @Override
//     public void start(Stage primaryStage) {
//         Button btn = new Button();
//         btn.setText("Say 'Hello World'");
//         btn.setOnAction(new EventHandler<ActionEvent>() {
 
//             @Override
//             public void handle(ActionEvent event) {
//                 System.out.println("Hello World!");
//             }
//         });
        
//         StackPane root = new StackPane();
//         root.getChildren().add(btn);
  
//   /*
//   Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
//   Scene scene = new Scene(root);
//   */
  
//   Scene scene = new Scene(root, 300, 250);
  
//   primaryStage.setTitle("Hello World!");
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }
 
//  public static void main(String[] args) {
//         launch(args);
//     }
// }