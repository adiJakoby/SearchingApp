package sample;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

import java.util.List;

public class DocumentsDisplayer {


    private final SimpleStringProperty queyNmber;
    private final SimpleStringProperty Documents;
    private Button getEntities;


    public DocumentsDisplayer(String queyNmber, String document) {
        this.queyNmber = new SimpleStringProperty(queyNmber);
        Documents = new SimpleStringProperty(document);
        getEntities = new Button("Get entities");
        getEntities.setOnAction(event -> {
            ;
            Entities.getDocEntities(document);
        });
    }


    public String getQueyNmber() {
        return queyNmber.get();
    }

    public SimpleStringProperty queyNmberProperty() {
        return queyNmber;
    }

    public String getDocuments() {
        return Documents.get();
    }

    public SimpleStringProperty documentsProperty() {
        return Documents;
    }
}