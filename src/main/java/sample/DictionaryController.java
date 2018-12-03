package sample;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class DictionaryController {
    @FXML
    public ListView<String> listView=new ListView<>();
    private List<String> myTerms = new ArrayList<>();
    private ListProperty<String> listProperty = new SimpleListProperty<>();


    @FXML
    private void initialize() {
        TreeMap<String, Integer[]> dictionary = new TreeMap<>(new MyComp());
        dictionary.putAll(Indexer.dictionary);
        while (dictionary.size()>0)
        {
            myTerms.add(dictionary.firstKey() + " " + dictionary.pollFirstEntry().getValue()[0] + " " + dictionary.pollFirstEntry().getValue()[1]);
        }
        listView.itemsProperty().bind(listProperty);
        listProperty.set(FXCollections.observableArrayList(myTerms));
    }

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
}
