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
        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setTitle("Retrieval Engine");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(465);
        primaryStage.setMinWidth(580);
        //view.setSubController();
        //SetStageCloseEvent(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.show();
    }//

    /*
    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit");
                alert.setHeaderText("Exit confirmation");
                alert.setContentText("Are you sure you want to quit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    //model.stopServers();
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }*/


    public static void main(String[] args) throws IOException {
        launch(args);
        /*
        DocsInformation docsInformation = new DocsInformation();
        String workingDir = System.getProperty("user.dir");
        System.out.println(workingDir);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        ReadFile Myreader = new ReadFile();
        Myreader.ReadFile(workingDir + "\\src\\main\\java\\corpus");
        Indexer indexer = new Indexer();
        indexer.mergePostingFile();
        docsInformation.saveTheInformation(workingDir + "\\src\\main\\java\\");
        Date date1 = new Date();
        System.out.println(dateFormat.format(date1));*/
    }

}
