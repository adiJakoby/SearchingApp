package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.TreeMap;

public class Entities {


    public static void getDocEntities(String documents) {
        //getting all docs entities
        HashMap<String,Integer> docEntities = DocsInformation.entities.get(documents);
        TreeMap<Integer,String> sortedEntities = new TreeMap<>();
        for (String key :docEntities.keySet()) {
            sortedEntities.put(docEntities.get(key),key);
        }
        String allDetails = "";
        for (Integer counter:sortedEntities.keySet()
             ) {
            allDetails += sortedEntities.get(counter) + " with the rate " + counter/Integer.parseInt((DocsInformation.allDocsInformation.get(documents))[3]) + "\n";
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Entities Details");
        alert.setHeaderText(allDetails);
    }
}
