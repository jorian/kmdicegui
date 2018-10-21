import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.KomodoRPC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Program extends Application {
    ExecutorService threadPool = Executors.newWorkStealingPool();



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("KMDice");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("css/style.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
