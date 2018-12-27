package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {
        /**
         * fxml loading
         */
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/sample.fxml").openStream());
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Retrieval Engine");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(350);
        primaryStage.setMinWidth(879);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }

}
