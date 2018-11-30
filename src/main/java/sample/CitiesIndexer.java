
package sample;

import com.wirefreethought.geodb.client.GeoDbApi;
import com.wirefreethought.geodb.client.model.*;
import com.wirefreethought.geodb.client.net.GeoDbApiClient;
import com.wirefreethought.geodb.client.request.FindCitiesRequest;
import com.wirefreethought.geodb.client.request.FindCurrenciesRequest;
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
import java.util.*;


public class CitiesIndexer {

    static Map<String, City> citiesDetails = new HashMap<>();
    //contain for each doc where it was write
    static Map<String, City> cities = new HashMap<>();
    private org.json.simple.parser.JSONParser myParser;
    static String workingDir = System.getProperty("user.dir");


    //connecting to the cities API and get all cities details into cities hashmap
    public void api_Connection() {
        OkHttpClient myClient = new OkHttpClient();
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
        //TODO take care in cases that our all cities dictionary does not contain the city
        char[] mychars = {'(', ')'};
        if (capital.contains("(")) {
            capital = OurReplace(capital, mychars, "");
        }
        String[] allCapitalParts = mySplit(capital," ");
        if (allCapitalParts.length > 1) {
            if (allCapitalParts.length == 3) {
                if (citiesDetails.containsKey((allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2]).toUpperCase()))
                {
                    citiesDetails.get((allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2]).toUpperCase()).addDocToCity(doc);
                    cities.put(doc, citiesDetails.get((allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2]).toUpperCase()));
                    //System.out.println(doc);
                } else {
                    //getCityDetails((allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2]).toUpperCase(), doc);
                    //System.out.println(doc);
                }
            } else if (allCapitalParts.length == 2) {
                if (citiesDetails.containsKey((allCapitalParts[0] + " " + allCapitalParts[1]).toUpperCase())) {
                    citiesDetails.get((allCapitalParts[0] + " " + allCapitalParts[1]).toUpperCase()).addDocToCity(doc);
                    cities.put(doc, citiesDetails.get((allCapitalParts[0] + " " + allCapitalParts[1]).toUpperCase()));
                    //System.out.println(doc);
                } else {
                    //getCityDetails((allCapitalParts[0] + " " + allCapitalParts[1]).toUpperCase(), doc);
                   // System.out.println(doc);
                }
            }
        } else if (citiesDetails.containsKey(allCapitalParts[0].toUpperCase())) {
            citiesDetails.get((allCapitalParts[0]).toUpperCase()).addDocToCity(doc);
            cities.put(doc, citiesDetails.get(allCapitalParts[0].toUpperCase()));
            //System.out.println(doc);
        } else {
            //getCityDetails(allCapitalParts[0].toUpperCase(), doc);
            //System.out.println(doc);
        }
    }

    //in case that the city it not a capital
    private void getCityDetails(String city, String doc) {
        /*GeoDbApiClient apiClient = new GeoDbApiClient(GeoDbInstanceType.FREE);
        GeoDbApi geoDbApi = new GeoDbApi(apiClient);

// Execute service calls. (See below for examples.)
        CitiesResponse citiesResponse = geoDbApi.findCities(
                FindCitiesRequest.builder()
                        .namePrefix("New York")
                        .build()
        );

        Metadata m = citiesResponse.getMetadata();
        CitySummary c = citiesResponse.getData().get(0);

        //citiesResponse.setData();
        System.out.println(citiesResponse.getData());
        System.out.println(geoDbApi.findCurrencies(FindCurrenciesRequest.builder().countryId(citiesResponse.getData().get(0).getCountryCode()).build()));
        System.out.println();
        */
        OkHttpClient myClient = new OkHttpClient();
        String url = "http://getcitydetails.geobytes.com/GetCityDetails?fqcn=" + city;
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
            String population = "";
            capital = (String) ((JSONObject) object).get("geobytescity");
            country = (String) ((JSONObject) object).get("geobytescountry");
            population = (String) ((JSONObject) object).get("geobytespopulation");
            coin = (String) ((JSONObject) object).get("geobytescurrencycode");
            if (!population.equals("")) {
                population = divideNumbers(Long.parseLong(population));
                citiesDetails.put(capital.toUpperCase(), new City(capital.toUpperCase(), country, coin, population));
                cities.put(doc, citiesDetails.get(city));
            }
        }
    }

    /**
     * @param pop
     * @return the population divide by 1000000,or more..
     */
    private String divideNumbers(Long pop) {
        int population=(int)(long)pop;
        int rest=0;
        String restbyString="";
        if (population >= 1000000000) {
            rest=population%1000000000;
            population = population / 1000000000;
            if(Long.toString(pop).charAt(Integer.toString(population).length())=='0'){
                restbyString=Integer.toString(population)+".0"+(Integer.toString(rest)).substring(0,1);
            }
            else {
                restbyString = Integer.toString(population) + "." + (Integer.toString(rest)).substring(0, 2);
            }
            return restbyString + "B";
        } else if (population >= 1000000) {
            rest=population%1000000;
            population = population / 1000000;
            if(Long.toString(pop).charAt(Integer.toString(population).length())=='0'){
                restbyString=Integer.toString(population)+".0"+(Integer.toString(rest)).substring(0,1);
            }
            else {
                restbyString = Integer.toString(population) + "." + (Integer.toString(rest)).substring(0, 2);
            }
            return restbyString + "M";
        } else if (population >= 1000) {
            rest = population % 1000;
            population = population / 1000;
            if(Long.toString(pop).charAt(Integer.toString(population).length())=='0'){
                if(Integer.toString(rest).length()>=2) {
                    restbyString = Integer.toString(population) + ".0" + (Integer.toString(rest)).substring(0, 1);
                }
                else if(Integer.toString(rest).length()==1){
                    restbyString = Integer.toString(population) + ".0" + (Integer.toString(rest)).substring(0, 1);
                }
            }
            else {
                if(Integer.toString(rest).length()>=2) {
                    restbyString = Integer.toString(population) + "." + (Integer.toString(rest)).substring(0, 2);
                }
                else if(Integer.toString(rest).length()==1){
                    restbyString = Integer.toString(population) + "." + (Integer.toString(rest)).substring(0, 1);
                }
            }
            return restbyString + "K";
        } else {
            restbyString=pop.toString();
            return restbyString;
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


    public static void writeDocPostingFile() {
        try {
            FileWriter fw = new FileWriter(workingDir + "\\src\\main\\java\\CitiesPosting.txt");
            BufferedWriter WriteFileBuffer = new BufferedWriter(fw);
            StringBuilder toWrite = new StringBuilder();
            for (Map.Entry<String, City> entry : cities.entrySet()) {
                String key = entry.getKey();
                City value = entry.getValue();
                if (value != null) {
                    toWrite.append(key + " " + value.getName() + "\n");
                }
            }
            WriteFileBuffer.write(toWrite.toString());
            WriteFileBuffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String OurReplace(String s, char[] targets, String replacement) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char check = s.charAt(i);
            boolean contain = false;
            for (int j = 0; !contain && j < targets.length; j++) {
                if (check == targets[j]) {
                    contain = true;
                }
            }
            if (contain) {
                sb.append(" ");
            } else {
                sb.append(check);
            }
        }
        return sb.toString();
    }

    private static String[] mySplit(String str, String regex) {
        Vector<String> result = new Vector<String>();
        int start = 0;
        int pos = str.indexOf(regex);
        while (pos >= start) {
            if (pos > start) {
                result.add(str.substring(start, pos));
            }
            start = pos + regex.length();
            //result.add(regex);
            pos = str.indexOf(regex, start);
        }
        if (start < str.length()) {
            result.add(str.substring(start));
        }
        String[] array = result.toArray(new String[0]);
        return array;
    }
}

