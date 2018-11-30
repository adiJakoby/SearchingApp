package sample;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class City {

    private String name;
    private String country;
    private String coin;
    private String population;
    private Map<String, String> docsContainMe = new HashMap<>();
    private HashSet<String> allDocsWrittenHere=new HashSet<>();

    public City(String capital, String country, String code, String pop) {
        this.name=capital;
        this.country=country;
        this.coin=code;
        this.population=pop;
    }

    //adding to the city map the docname that contain the city and it location per doc
    public void addDocToMap( String doc, String location){
        //case that its not the first time that the city appears in the doc
        if(docsContainMe.containsKey(doc)){
            docsContainMe.put(doc , docsContainMe.get(doc)+ " and " + location);
        }
        else {
            docsContainMe.put(doc , location);
        }
    }

    public String getName() {
        return name;
    }

    //add the doc to the set of documents that was written in this city
    public void addDocToCity(String docName){
        allDocsWrittenHere.add(docName);
    }
}
