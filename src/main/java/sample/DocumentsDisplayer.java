package sample;


import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DocumentsDisplayer {


    private final SimpleStringProperty queyNmber;
    private final SimpleStringProperty Documents;
    private Button getEntities;


    public DocumentsDisplayer(String queyNmber, String document) {
        this.queyNmber = new SimpleStringProperty(queyNmber);
        Documents = new SimpleStringProperty(document);
        getEntities = new Button("Get entities");
        getEntities.setVisible(true);
        getEntities.setDisable(false);
        getEntities.setOnAction(event -> {
            //getting all docs entities
            HashMap<String,Integer> docEntities = DocsInformation.entities.get(document);
            TreeMap<Integer,String> sortedEntities = new TreeMap<>();
            for (String key :docEntities.keySet()) {
                sortedEntities.put(docEntities.get(key),key);
            }
            String allDetails = "";
            for (Integer counter:sortedEntities.keySet()
                    ) {
                allDetails += sortedEntities.get(counter) + " with the rate " + counter/Integer.parseInt((DocsInformation.allDocsInformation.get(document))[3]) + "\n";
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entities Details");
            alert.setHeaderText(allDetails);
        });
        HashMap<String,Integer> docEntities = DocsInformation.entities.get(document);
        TreeMap<Integer,String> sortedEntities = new TreeMap<>();
        if(docEntities!= null) {
            for (String key : docEntities.keySet()) {
                sortedEntities.put(docEntities.get(key), key);
            }
            String allDetails = "";
            for (Integer counter : sortedEntities.keySet()
                    ) {
                allDetails += sortedEntities.get(counter) + " with the rate " + counter / Integer.parseInt((DocsInformation.allDocsInformation.get(document))[3]) + "\n";
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entities Details");
            alert.setHeaderText(allDetails);
            System.out.println(allDetails);
        }
    }


    public String getQueyNmber() {
        return queyNmber.get();
    }

    public SimpleStringProperty queyNmberPropertsy() {
        return queyNmber;
    }

    public String getDocuments() {
        return Documents.get();
    }

    public SimpleStringProperty documentsProperty() {
        return Documents;
    }
}
