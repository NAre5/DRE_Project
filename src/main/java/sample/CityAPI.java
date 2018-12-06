package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class responsible to get data about city from API https://restcountries.eu
 * We use json to do it easly and fase.
 */
public class CityAPI {
    JsonElement json;

    public CityAPI() {
        JsonParser parser = new JsonParser();
        try {
            json = parser.parse(new FileReader(new File(ClassLoader.getSystemResource("json.json").getPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getCityInfo(String city)
    {
        String cityAsLower = city.toLowerCase();
        cityAsLower  = Character.toUpperCase(cityAsLower.charAt(0)) + cityAsLower.substring(1);

        StringBuilder sb = new StringBuilder("[");//
        JsonArray infoAsArray = json.getAsJsonArray();
        for (int i = 0; i < infoAsArray.size(); i++) {
            JsonObject matchLine = (JsonObject)infoAsArray.get(i);
            String capital = matchLine.get("capital").getAsString();
            if(capital.equals(cityAsLower))
            {
                sb.append(matchLine.get("name").getAsString()).append(",");
                JsonArray currencyData = matchLine.getAsJsonArray("currencies");
                for (JsonElement currencyDatum : currencyData) {
                    JsonObject j = (JsonObject)currencyDatum;
                    JsonElement e = j.get("code");
                    sb.append(e.getAsString()).append(",");
                }
                sb.append(Parse.parseNumber(matchLine.get("population").getAsString())+"]");
            }
        }
        //if the api not contain the city
        if(sb.toString().equals("["))
            sb.append("*,*,*]");
        return sb.toString();

    }

}
