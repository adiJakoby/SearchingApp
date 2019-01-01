package sample;

import javafx.beans.property.SimpleStringProperty;

public class TermsDisplayer {
    public String getTerm() {
        return term.get();
    }

    public SimpleStringProperty termProperty() {
        return term;
    }

    public void setTerm(String term) {
        this.term.set(term);
    }

    private final SimpleStringProperty term;

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    private final SimpleStringProperty value;

    public TermsDisplayer(String termName, int val){
        this.term = new SimpleStringProperty(termName);
        this.value = new SimpleStringProperty(val + "");
    }
}
