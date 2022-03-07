package org.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Weatherforecast";

    private TemperatureController temperatureController;

    private Button getWeatherBtn;
    private EditText cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureController = new TemperatureController();

        getWeatherBtn = findViewById(R.id.getWeatherBtn);
        getWeatherBtn.setOnClickListener(new GetWeatherBtnListener());

        cityInput = findViewById(R.id.cityInput);
    }

    private void shortToast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    private void longToast(String message) {
        toast(message, Toast.LENGTH_LONG);
    }

    private void toast(String message, Integer length) {
        Log.i(TAG, "Toasted: " + message);
        Toast.makeText(MainActivity.this, message, length).show();
    }

    private class GetWeatherBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String cityName = cityInput.getText().toString().trim();
            Log.i(TAG, "Asked temperature in '" + cityName + "'");
            GetTemperatureTask getTemperatureTask = new GetTemperatureTask();
            getTemperatureTask.execute(cityName);
        }
    }

    private class GetTemperatureTask extends AsyncTask<String, Void, Double> {
        @Override
        protected Double doInBackground(String... strings) {
            if (strings.length > 1 || strings.length == 0) {
                Log.i(TAG, "Wrong amount of cities");
            }

            WeatherGetter weatherGetter = new WeatherGetter();
            String cityName = strings[0];
            Double temperature = null;
            try {
                temperature = weatherGetter.getTemperatureIn(cityName);
            } catch (Exception e) {
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }

            return temperature;
        }

        @Override
        protected void onPostExecute(Double temperature) {
            super.onPostExecute(temperature);
            temperatureController.setTemperature(temperature);
        }
    }

    private class TemperatureController {
        private final Integer MAXIMUM_FRACTION_DIGIT = 2;
        private final Double DEFAULT_TEMPERATURE = 0.0;
        private final Double KELVIN_ABS_DIFFERENCE = 273.15;

        private TextView temperatureView;

        public TemperatureController() {
            temperatureView = findViewById(R.id.temperatureView);
            setTemperature(DEFAULT_TEMPERATURE);
        }

        public void setTemperature(Double temperature) {
            DecimalFormat formatter = new DecimalFormat();
            formatter.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGIT);
            temperatureView.setText(formatter.format(temperature - KELVIN_ABS_DIFFERENCE)
                    + " °C");
        }
    }
}