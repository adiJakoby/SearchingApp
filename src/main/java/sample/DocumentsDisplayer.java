package sample;


import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DocumentsDisplayer {

    /**
     * required object for the query table view
     */

    private final SimpleStringProperty queyNmber;
    private final SimpleStringProperty Documents;
    private Button btn_getEntities;



    public DocumentsDisplayer(String queyNmber, String document) {
        this.queyNmber = new SimpleStringProperty(queyNmber);
        Documents = new SimpleStringProperty(document);
        btn_getEntities = new Button("Get entities");
        btn_getEntities.setVisible(true);
        btn_getEntities.setDisable(false);
        btn_getEntities.setOnAction(event -> {
            //getting all docs entities
            HashMap<String,Integer> docEntities = DocsInformation.entities.get(document);
            TreeMap<Integer,String> sortedEntities = new TreeMap<>();
            if(docEntities!=null) {
                for (String key : docEntities.keySet()) {
                    sortedEntities.put(docEntities.get(key), key);
                }
                String allDetails = "";
                for (Integer counter : sortedEntities.keySet()
                        ) {
                    allDetails += sortedEntities.get(counter) + " with the rate " + counter + "\n";///(Double.parseDouble((DocsInformation.allDocsInformation.get(document))[3])) + "\n";
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Entities Details");
                alert.setHeaderText(allDetails);
                alert.show();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Entities Details");
                alert.setHeaderText("Document not contains entities");
                alert.show();
            }
        });
    }

    public void setDocuments(String documents) {
        this.Documents.set(documents);
    }

    public Button getBtn_getEntities() {
        return btn_getEntities;
    }

    public void setBtn_getEntities(Button btn_getEntities) {
        this.btn_getEntities = btn_getEntities;
    }

    public SimpleStringProperty queyNmberProperty() {
        return queyNmber;
    }

    public void setQueyNmber(String queyNmber) {
        this.queyNmber.set(queyNmber);
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
