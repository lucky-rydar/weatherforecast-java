package org.weatherforecast;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherGetter {
    private final String TAG = "WeatherGetter";
    private final String API_KEY = "320d08efa051c7f96886ef293c4f5cb4";
    private final String DEFAULT_LIMIT = "1";

    private OkHttpClient httpClient;

    public WeatherGetter() {
        httpClient = new OkHttpClient();
    }

    public Double getTemperatureIn(String city) throws Exception {
        Coordinate coordinate = getCoordinateByCity(city);

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + coordinate.lat + "&lon=" +
                coordinate.lon + "&appid=" + API_KEY;
        Request request = new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = httpClient.newCall(request).execute();
            return extractTemperatureFromJson(response.body().string());
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            throw e;
        }
    }

    private Coordinate getCoordinateByCity(String city) {
        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=" +
                DEFAULT_LIMIT + "&appid=" + API_KEY;

        Request request = new Request.Builder().url(url).get().build();

        Response response = null;

        try {
            response = httpClient.newCall(request).execute();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        Coordinate ret = null;
        try {
            ret = extractCoordinateFromJSON(response.body().string());
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        return ret;
    }

    private Coordinate extractCoordinateFromJSON(String json) throws Exception {
        try {
            return extractCoordinateFromJSON(new JSONArray(json));
        } catch (JSONException e) {
            Log.i(TAG, e.toString());
            throw e;
        }
    }

    private Coordinate extractCoordinateFromJSON(JSONArray json) throws Exception {
        JSONObject firstObj = null;
        try {
            firstObj = json.getJSONObject(0);
            return new Coordinate(firstObj.getDouble("lat"), firstObj.getDouble("lon"));
        } catch (JSONException e) {
            Log.i(TAG, e.toString());
            throw e;
        }
    }

    private Double extractTemperatureFromJson(String json) throws JSONException {
        try {
            return extractTemperatureFromJson(new JSONObject(json));
        } catch (JSONException e) {
            Log.i(TAG, "Cant extract temperature from json");
            throw e;
        }
    }

    private Double extractTemperatureFromJson(JSONObject json) throws JSONException {
        try {
            return json.getJSONObject("main").getDouble("temp");
        } catch (JSONException e) {
            Log.i(TAG, e.toString());
            throw e;
        }
    }

    class Coordinate {
        private Double lon;
        private Double lat;

        public Coordinate(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
