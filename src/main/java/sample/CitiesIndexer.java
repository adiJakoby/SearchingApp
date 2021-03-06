
package sample;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;


public class CitiesIndexer {

    static Map<String, City> citiesDetails = new HashMap<>();
    private org.json.simple.parser.JSONParser myParser;
    static String workingDir = System.getProperty("user.dir");
    DocsInformation docsInformation = new DocsInformation();
    static HashMap<String, City> allCitiesInCorpus = new HashMap();


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
            String population = "";
            Object[] parsed_json = ((JSONArray) object).toArray();
            for (Object O : parsed_json) {
                capital = (String) ((JSONObject) O).get("capital");
                country = (String) ((JSONObject) O).get("name");
                JSONArray theArray = (JSONArray) (((JSONObject) O).get("currencies"));
                for (Object obj : theArray) {
                    coin = (String) ((JSONObject) obj).get("code");
                }
                population = ((JSONObject) O).get("population").toString();
                String pop = divideNumbers(Double.parseDouble(population));
                citiesDetails.put(capital.toUpperCase(), new City(capital.toUpperCase(), country, coin, pop));

            }
        }
    }

    //check if the city name is one word or to and add to the city dictionary the doc that contain the city and it location
    public void addCityToCorpusMap(String capital, String doc) {
        //TODO take care in cases that our all cities dictionary does not contain the city
        String cityName = "";
        if (!Pattern.compile("[0-9]").matcher(capital).find()) {
            char[] mychars = {'(', ')', '[', ']', '.', '"', '\'', '2'};
            if (capital.contains("(")
                    || capital.contains("[") || capital.contains("]") || capital.contains("-") || capital.contains(".")) {
                capital = OurReplace(capital, mychars, "");
            }
            String[] allCapitalParts = mySplit(capital, " ");
            allCapitalParts = mySplit(capital, "-");
            City city = null;
            if (allCapitalParts.length >= 1) {
                if (allCapitalParts.length == 3) {
                    cityName = (allCapitalParts[0] + " " + allCapitalParts[1] + " " + allCapitalParts[2]).toUpperCase();
                    if (citiesDetails.containsKey(cityName)) {
                        city = citiesDetails.get(cityName);
                        allCitiesInCorpus.put(cityName, city);
                    } else if(!allCitiesInCorpus.containsKey(cityName)){
                        city = new City(cityName, "", "", "");
                        allCitiesInCorpus.put(cityName, city);
                    }
                    else{
                        city = allCitiesInCorpus.get(cityName);
                    }
                } else if (allCapitalParts.length == 2) {
                    cityName = (allCapitalParts[0] + " " + allCapitalParts[1]).toUpperCase();
                    if (citiesDetails.containsKey(cityName)) {
                        city = citiesDetails.get(cityName);
                        allCitiesInCorpus.put(cityName, city);
                    } else if(!allCitiesInCorpus.containsKey(cityName)){
                        city = new City(cityName, "", "", "");
                        allCitiesInCorpus.put(cityName, city);
                    }
                    else{
                        city = allCitiesInCorpus.get(cityName);
                    }
                } else if (allCapitalParts.length == 1) {
                    if (citiesDetails.containsKey(allCapitalParts[0].toUpperCase())) {
                        cityName = allCapitalParts[0].toUpperCase();
                        city = citiesDetails.get(cityName);
                        allCitiesInCorpus.put(cityName, city);
                    } else if(!allCitiesInCorpus.containsKey(allCapitalParts[0].toUpperCase())){
                        cityName = allCapitalParts[0].toUpperCase();
                        city = new City(cityName, "", "", "");
                        allCitiesInCorpus.put(cityName, city);
                    }
                    else{
                        city = allCitiesInCorpus.get(allCapitalParts[0].toUpperCase());
                    }
                }
                docsInformation.addOriginCity(doc, cityName);
                city.addDocToCity(doc);
            }
        }
    }

    /**
     * @param pop
     * @return the population divide by 1000000,or more..
     */
    private String divideNumbers(Double pop) {
        if (pop >= 1000000000) {
            pop = pop / 1000000000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "B";
        } else if (pop >= 1000000) {
            pop = pop / 1000000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "M";
        } else if (pop >= 1000) {
            pop = pop / 1000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "K";
        } else {
            return Double.toString(pop);
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

    public static String[] mySplit(String str, String regex) {
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

    //write each city population, coin and country
    public void writeCitiesPosting(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path + "\\Cities Information.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allCitiesInCorpus);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //reading and setting the allCitiesInCorpus map
    public void setAllCitiesInCorpus(String path){
        try {
            FileInputStream fis = new FileInputStream(path + "\\Cities Information.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            allCitiesInCorpus = (HashMap<String, City>) ois.readObject();
            ois.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

