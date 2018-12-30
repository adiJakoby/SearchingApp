package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class CitiesDisplayer {

    private  final SimpleStringProperty cityName;
    private final CheckBox cityChooser;

    public CitiesDisplayer(String city) {
        cityName = new SimpleStringProperty(city);
        cityChooser = new CheckBox();
    }

    public String getCityName() {
        return cityName.get();
    }

    public SimpleStringProperty cityNameProperty() {
        return cityName;
    }

    public CheckBox getCityChooser() {
        return cityChooser;
    }

    public void setCityName(String cityName) {
        this.cityName.set(cityName);
    }
}
