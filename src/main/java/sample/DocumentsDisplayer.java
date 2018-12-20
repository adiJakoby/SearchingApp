package sample;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

import java.util.List;

public class DocumentsDisplayer {


    private final SimpleStringProperty queyNmber;
    private final SimpleStringProperty Documents;
    private Button getEntities;


    public DocumentsDisplayer(int queyNmber, String document) {
        this.queyNmber = new SimpleStringProperty(queyNmber+"");
        Documents = new SimpleStringProperty(document);
        getEntities = new Button("Get entities");
        getEntities.setOnAction(event -> {
            ;
            Entities.getDocEntities(document);
        });
    }


}
