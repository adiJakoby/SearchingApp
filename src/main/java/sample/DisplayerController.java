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
        try{
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Relevant Documents");
            stage.setWidth(440);
            stage.setHeight(940);
            final Label lable = new Label("Relevant Documents");
            lable.setFont(new Font("Ariel" , 22));
            table.setEditable(false);

            TableColumn queyNmber = new TableColumn("Query number");
            queyNmber.setMinWidth(200);
            queyNmber.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer,String>("queyNmber"));

            TableColumn Documents = new TableColumn("Documents");
            Documents.setMinWidth(200);
            Documents.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer,String>("Documents"));

            TableColumn entities = new TableColumn("Entities");
            entities.setMinWidth(100);
            entities.setCellValueFactory(new PropertyValueFactory<DocumentsDisplayer, Button>("Get entities"));

            table.setItems(getData(allDocs));
            table.getColumns().addAll(queyNmber,Documents,entities);
            table.setMinHeight(800);

            final VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(20,0,0,10));
            vbox.getChildren().addAll(lable,table);

            ((Group)scene.getRoot()).getChildren().addAll(vbox);
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
