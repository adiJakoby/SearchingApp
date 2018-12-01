package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.*;

public class DictionaryController {
    @FXML
    public ListView<String> listView;

    @FXML
    private void initialize() {
        TreeMap<String, Integer> dictionary = new TreeMap<>();
        dictionary.putAll(Indexer.dictionary);
        List<String> l = new LinkedList<>();
        for (Map.Entry<String, Integer> entry:dictionary.entrySet()
             ) {
            l.add(dictionary.firstKey() + " " + dictionary.pollFirstEntry().getValue());
        }

        listView.getItems().addAll(l);
    }



}
