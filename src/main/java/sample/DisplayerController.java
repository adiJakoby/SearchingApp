package sample;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class DisplayerController {

    private List<String> myTerms = new ArrayList<>();
    private ListProperty<String> listProperty = new SimpleListProperty<>();
    @FXML
    public TableView table = new TableView();


    public void displayrelevantDocs (){
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
            queyNmber.setCellValueFactory(new PropertyValueFactory<TermsDisplayer,String>("queyNmber"));

            TableColumn Documents = new TableColumn("Documents");
            Documents.setMinWidth(200);
            Documents.setCellValueFactory(new PropertyValueFactory<TermsDisplayer,String>("Documents"));

            TableColumn entities = new TableColumn("Entities");
            entities.setMinWidth(200);
            entities.setCellValueFactory(new PropertyValueFactory<TermsDisplayer,String>("Entities"));

            //table.setItems(getData());
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

//    private ObservableList<TermsDisplayer> getData() {
//        ObservableList<TermsDisplayer> dict = FXCollections.observableArrayList();
//        Map <Integer,List<String>> allDocs = new HashMap<>();
//        //allDocs.putAll();
//        for (Map.Entry<String,Integer[]> entry:allDocs.entrySet()) {
//            String term = entry.getKey();
//            int value = entry.getValue()[1];
//            dict.add(new TermsDisplayer(term, value));
//        }
// return dict;
//
//    }
}
