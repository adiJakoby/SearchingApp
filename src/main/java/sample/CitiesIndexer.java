
package sample;

import jdk.nashorn.internal.parser.JSONParser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class CitiesIndexer {

    static Map<String, City> citiesDetails = new HashMap<>();
    //contain for each doc where it was write
    static Map<String, City> cities = new HashMap<>();
    private org.json.simple.parser.JSONParser myParser;

    public void findCity(String docName, String cityName, int location) {


    }

    //connecting to the cities API and get all cities details into cities hashmap
    public void api_Connection() {
        //int maxlength=0;
        //String maxCity="";
        OkHttpClient myClient = new OkHttpClient();
        //HttpUrl.Builder urlBuilder = HttpUrl.parse("https://restcountries.eu/rest/v2/all?fielss=capital;name;pop;");
        String url = "https://restcountries.eu/rest/v2/all?fielss=capital;name;population;currency";
        Request req = new Request.Builder().url(url).build();
        Response res = null;
        try {
            res = myClient.newCall(req).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Object object = null;
        myParser = new org.json.simple.parser.JSONParser();
        try {
            try {
                object = myParser.parse(res.body().string());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object != null) {
            String capital = "", country = "", coin = "";
            Long population = 0L;
            Object[] parsed_json = ((JSONArray) object).toArray();
            for (Object O : parsed_json) {
                capital = (String) ((JSONObject) O).get("capital");
                country = (String) ((JSONObject) O).get("name");
                JSONArray theArray = (JSONArray) (((JSONObject) O).get("currencies"));
                for (Object obj : theArray) {
                    coin = (String) ((JSONObject) obj).get("code");
                }
                /*
                if(maxlength<capital.length()){
                    maxlength=capital.length();
                    maxCity=capital;
                }*/
                population = (Long) ((JSONObject) O).get("population");
                String pop = divideNumbers(population);
                citiesDetails.put(capital.toUpperCase(), new City(capital.toUpperCase(), country, coin, pop));

            }
            //System.out.println(maxlength);
            //System.out.println(maxCity);
        }
    }

    //check if the city name is one word or to and add to the city dictionary the doc that contain the city and it location
    public void addCityToCorpusMap(String capital, String doc) {
        String[] allCapitalParts = capital.split(" ");
        if (allCapitalParts.length > 1) {
            if(allCapitalParts.length == 3) {
                if (citiesDetails.containsKey(allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2].toUpperCase())) {
                    cities.put(doc, citiesDetails.get(allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2].toUpperCase()));
                }
            }
            else if(allCapitalParts.length == 2) {
                if (citiesDetails.containsKey(allCapitalParts[0] + " " + allCapitalParts[1].toUpperCase())) {
                    cities.put(doc, citiesDetails.get(allCapitalParts[0] + " " + allCapitalParts[1].toUpperCase()));
                }
            }
        }
        else{
            cities.put(doc, citiesDetails.get(allCapitalParts[0].toUpperCase()));
        }
    }


    /**
     * @param population
     * @return the population divide by 1000000,or more..
     */
    private String divideNumbers(Long population) {
        //double d = ourParseToDouble(toDivide);
        if (population >= 1000000000) {
            population = population / 1000000000;
            String toWrite;
            if (isNaturalNumber(population)) {
                int di = (int) (long) population;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(population);
            }
            return toWrite + "B";
        } else if (population >= 1000000) {
            population = population / 1000000;
            String toWrite;
            if (isNaturalNumber(population)) {
                int di = (int) (long) population;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(population);
            }
            return toWrite + "M";
        } else if (population >= 1000) {
            population = population / 1000;
            String toWrite;
            if (isNaturalNumber(population)) {
                int di = (int) (long) population;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(population);
            }
            return toWrite + "K";
        } else {
            String toWrite;
            if (isNaturalNumber(population)) {
                int di = (int) (long) population;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(population);
            }
            return toWrite;
        }
    }
    //checking if natural number
    private boolean isNaturalNumber(double number) {
        int x = (int) number;
        double y = number - x;
        if (y > 0) {
            return false;
        }
        return true;
    }

    //http://getcitydetails.geobytes.com/GetCityDetails?fqcn=USNYNYOR', true);

    public static void writeDocPostingFile() {
        try {
            FileWriter fw = new FileWriter("C:\\Users\\tzalach\\IdeaProjects\\SearchingApp\\src\\main\\java\\CitiesPosting.txt");
            BufferedWriter WriteFileBuffer = new BufferedWriter(fw);
            StringBuilder toWrite = new StringBuilder();
            for(Map.Entry<String, City> entry : cities.entrySet()) {
                String key = entry.getKey();
                City value = entry.getValue();
                if(value!=null) {
                    toWrite.append(key + " " + value.getName() + "\n");
                }
            }
            WriteFileBuffer.write(toWrite.toString());
            WriteFileBuffer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

