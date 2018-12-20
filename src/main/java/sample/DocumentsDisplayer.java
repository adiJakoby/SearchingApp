package sample;

import javafx.beans.property.SimpleStringProperty;

public class DocumentsDisplayer {


    private final SimpleStringProperty queyNmber;
    private final SimpleStringProperty Documents;
    private final SimpleStringProperty entities;


    public DocumentsDisplayer(SimpleStringProperty queyNmber, SimpleStringProperty documents, SimpleStringProperty entities) {
        this.queyNmber = queyNmber;
        Documents = documents;
        this.entities = entities;
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

    public String getEntities() {
        return entities.get();
    }

    public SimpleStringProperty entitiesProperty() {
        return entities;
    }
}
