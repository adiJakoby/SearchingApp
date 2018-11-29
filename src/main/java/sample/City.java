package sample;

import java.util.HashMap;
import java.util.Map;

public class City {

    private String name;
    private String coin;
    private String population;
    private Map<String, String> citiesInDoc = new HashMap<>();

    public City(String capital, String country, String code, String pop) {
        this.name=country;
        this.coin=code;
        this.population=pop;
    }


    public String getCoin() {
        return coin;
    }

    public String getPopulation() {
        return population;
    }

    public String getName() {
        return name;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    //adding to the city map the docname that contain the city and it location per doc
    public void addToMap( String doc, String location){
        //case that its not the first time that the city appears in the doc
        if(citiesInDoc.containsKey(doc)){
            citiesInDoc.put(doc ,citiesInDoc.get(doc)+ " and " + location);
        }
        else {
            citiesInDoc.put(doc , location);
        }
    }
}
