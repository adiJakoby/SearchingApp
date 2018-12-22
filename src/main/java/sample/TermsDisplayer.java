package sample;

import javafx.beans.property.SimpleStringProperty;

public class TermsDisplayer {

    private final SimpleStringProperty term;
    private final SimpleStringProperty value;

    public TermsDisplayer(String termName, int val){
        this.term = new SimpleStringProperty(termName);
        this.value = new SimpleStringProperty(val + "");
    }
}
