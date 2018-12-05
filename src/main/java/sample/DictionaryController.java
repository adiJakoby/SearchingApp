package sample;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class DictionaryController {

    private List<String> myTerms = new ArrayList<>();
    private ListProperty<String> listProperty = new SimpleListProperty<>();
    @FXML
    public TableView table = new TableView();

    class MyComp implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            String s1l = s1.toLowerCase();
            String s2l = s2.toLowerCase();
            if (s1l.equals(s2l)) {
                return s1.compareTo(s2);
            }
            return s1l.compareTo(s2l);
        }
    }

    public void displayDictionary(){
        try{
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Dictionary");
            stage.setWidth(440);
            stage.setHeight(940);
            final Label lable = new Label("Dictionary");
            lable.setFont(new Font("Ariel" , 22));
            table.setEditable(false);

            TableColumn term = new TableColumn("Term");
            term.setMinWidth(200);
            term.setCellValueFactory(new PropertyValueFactory<TermsDisplayer,String>("term"));

            TableColumn value = new TableColumn("Number of Occurrence");
            value.setMinWidth(200);
            value.setCellValueFactory(new PropertyValueFactory<TermsDisplayer,String>("value"));

            table.setItems(getData());
            table.getColumns().addAll(term,value);
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


    private ObservableList<TermsDisplayer> getData() {
        ObservableList<TermsDisplayer> dict = FXCollections.observableArrayList();
        TreeMap<String, Integer[]> dictionary = new TreeMap<>(new MyComp());
        dictionary.putAll(Indexer.dictionary);
        for (Map.Entry<String,Integer[]> entry:dictionary.entrySet()) {
            String term = entry.getKey();
            int value = entry.getValue()[1];
            dict.add(new TermsDisplayer(term, value));
        }

        return dict;
    }
}
