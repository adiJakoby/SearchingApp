package sample;

import javafx.beans.property.SimpleStringProperty;

public class TermsDisplayer {

    private final SimpleStringProperty term;
    private final SimpleStringProperty value;

    public TermsDisplayer(String termName, int val){
        this.term = new SimpleStringProperty(termName);
        this.value = new SimpleStringProperty(Integer.toString(val) + "");
    }


    public String getTerm() {
        return term.get();
    }

    public SimpleStringProperty termProperty() {
        return term;
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }
}
