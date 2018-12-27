package sample;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class DisplayerController {

    @FXML
    public TableView table = new TableView();


    public void displayrelevantDocs (LinkedHashMap <String,List<String>> allDocs){
        try {
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Relevant Documents");
            stage.setWidth(800);
            stage.setHeight(600);
            final Label lable = new Label("Relevant Documents");
            lable.setFont(new Font("Ariel", 22));
            table.setEditable(false);

            TableColumn queyNmber = new TableColumn("Query number");
            queyNmber.setMinWidth(200);
            queyNmber.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer, String>("queyNmber"));

            TableColumn Documents = new TableColumn("Documents");
            Documents.setMinWidth(200);
            Documents.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer, String>("Documents"));

            TableColumn entities = new TableColumn("Entities");
            entities.setMinWidth(150);
            entities.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer, Button>("btn_getEntities"));

            table.setItems(getData(allDocs));
            table.getColumns().addAll(queyNmber, Documents, entities);
            table.setMinHeight(800);
            table.setMinHeight(800);
            ObservableList<DocumentsDisplayer> dict = table.getItems();
            final VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(20, 10, 0, 20));
            vbox.getChildren().addAll(lable, table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);
            table.setStyle("-fx-selection-bar: #b3e0ff; -fx-selection-bar-non-focused: #b3e0ff;  -fx-background-color: #1aa3ff;");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ObservableList<DocumentsDisplayer> getData(LinkedHashMap <String,List<String>>  allDocs) {
        ObservableList<DocumentsDisplayer> dict = FXCollections.observableArrayList();
        for (String querynum:allDocs.keySet()
             ) {
            List<String> Docs = allDocs.get(querynum);
            for (String doc:Docs
                 ) {
                dict.add(new DocumentsDisplayer(querynum , doc));
            }
        }
            return dict;
    }
}
