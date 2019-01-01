package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class CitiesController {
    @FXML
    public TableView table = new TableView();
    static List<String> allCitiesSelected = new LinkedList<>();


    public void displayCities() {
        try {
            allCitiesSelected = new LinkedList<>();
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Cities Chooser");
            stage.setWidth(800);
            stage.setHeight(800);
            final Label lable = new Label("Please choose Cities for retrieve by ");
            lable.setFont(new Font("Ariel", 22));
            table.setEditable(false);

            TableColumn cityName = new TableColumn("City name");
            cityName.setMinWidth(200);
            cityName.setCellValueFactory(new PropertyValueFactory<CitiesDisplayer, String>("cityName"));

            TableColumn cityCheckBox = new TableColumn("");
            cityCheckBox.setMinWidth(200);
            cityCheckBox.setCellValueFactory(new PropertyValueFactory<CitiesDisplayer, CheckBox>("cityChooser"));

            table.setItems(getData());
            table.getColumns().addAll(cityName, cityCheckBox);
            table.setMinHeight(500);
            table.setMinWidth(600);
            //ObservableList<CitiesDisplayer> dict = table.getItems();
            final VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(20, 10, 0, 20));
            vbox.getChildren().addAll(lable, table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);
            table.setStyle("-fx-selection-bar: #b3e0ff; -fx-selection-bar-non-focused: #b3e0ff;  -fx-background-color: #1aa3ff;");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<CitiesDisplayer> getData() {
        ObservableList<CitiesDisplayer> dict = FXCollections.observableArrayList();
        TreeMap<String, City> sortedCities = new TreeMap<>();
        sortedCities.putAll(CitiesIndexer.allCitiesInCorpus);
        for (String city : sortedCities.keySet()
                ) {
            dict.add(new CitiesDisplayer(city));
        }
        return dict;
    }

    public List<String> getCitiesSelected() {
        return allCitiesSelected;
    }
}
