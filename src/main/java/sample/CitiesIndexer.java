
package sample;

import jdk.nashorn.internal.parser.JSONParser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class CitiesIndexer {

    static Map<String, City> citeis = new HashMap<>();
    private org.json.simple.parser.JSONParser myParser;

    public void indexing(TreeMap<String, Integer> stems, String docNo, String cityName) {
        //case when the city not exist in the dictionary
        if (!citeis.containsKey(cityName)) {
            String[] Details = getCityDetails(cityName);

        }
    }

    public void findCity(String cityName) {
        if (!citeis.containsKey(cityName)) {


        }

    }

    public void api_Connection() {
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
        Object object=null;
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
        if(object!=null){
            String capital = "",country="",code="";
            Long population=0L;
            Object[] parsed_json = ((JSONArray) object).toArray();
            for (Object O: parsed_json) {
                capital=(String)((JSONObject)O).get("capital");
                country=(String)((JSONObject)O).get("name");
                JSONArray theArray = (JSONArray)(((JSONObject)O).get("currencies"));
                for (Object obj:theArray) {
                    code=(String)((JSONObject)obj).get("code");
                }
                population=(Long)((JSONObject)O).get("population");
                citeis.put(capital,new City(capital,country,code,population));

            }
        }
    }


    private String[] getCityDetails(String cityName) {
        String[] output = new String[3];
        return output;
    }
}

