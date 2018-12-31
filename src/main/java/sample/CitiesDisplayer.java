package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class CitiesDisplayer {

    private  final SimpleStringProperty cityName;
    private final CheckBox cityChooser;

    public CitiesDisplayer(String city) {
        cityName = new SimpleStringProperty(city);
        cityChooser = new CheckBox();
        cityChooser.setOnAction(event -> {
            if(cityChooser.isSelected()){
                CitiesController.allCitiesSelected.add(city);
            }else{
                if(CitiesController.allCitiesSelected.contains(city)){
                    CitiesController.allCitiesSelected.remove(city);
                }
            }
        });
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
