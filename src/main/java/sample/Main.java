package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {
        /**
         * fxml loading
         */
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/sample.fxml").openStream());
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Retrieval Engine");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(465);
        primaryStage.setMinWidth(580);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }

}
