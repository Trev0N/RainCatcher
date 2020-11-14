package pl.bucior.raincatcher;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLocationService implements LocationListener {

    private final ImageView weatherImage;
    private final TextView temperature;
    private final TextView temperatureFeeling;
    private final TextView pressure;
    private final TextView humidity;
    private final TextView windSpeed;
    private final WeatherService weatherService;
    private final double kelwin = 273.15;


    public MyLocationService(ImageView weatherImage, TextView temperature, TextView temperatureFeeling, TextView pressure, TextView humidity, TextView windSpeed, WeatherService weatherService) {
        this.weatherImage = weatherImage;
        this.temperature = temperature;
        this.temperatureFeeling = temperatureFeeling;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherService = weatherService;
    }

    @Override
    public void onLocationChanged(Location loc) {
        weatherService.getWeather(loc.getLatitude(), loc.getLongitude(), "c5d033f0c9ff79e419c4ca2b43abc550", "pl").enqueue(new Callback<WeatherResponse>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    temperature.setText(String.format("%s°C", Math.round((weatherResponse.getCurrent().getTemp() - kelwin) * 10.0) / 10.0));
                    temperatureFeeling.setText(String.format("%s°C", Math.round((weatherResponse.getCurrent().getFeels_like() - kelwin) * 10.0) / 10.0));
                    pressure.setText(String.format("%shPa", weatherResponse.getCurrent().getPressure()));
                    humidity.setText(String.format("%s%%", weatherResponse.getCurrent().getHumidity()));
                    windSpeed.setText(String.format("%sm/s", weatherResponse.getCurrent().getWind_speed()));
                    Optional<WeatherResponse.WeatherDescription> weatherDetail = weatherResponse.getCurrent().getWeather().stream().findAny();
                    if (!weatherDetail.isPresent()) {
                        return;
                    }
                    switch (weatherDetail.get().getIcon()) {
                        case "11d":
                        case "11n":
                            weatherImage.setImageResource(R.drawable.gce7ajgcd);
                            break;
                        case "03d":
                        case "03n":
                        case "04d":
                        case "04n":
                        case "50d":
                        case "50n":
                            weatherImage.setImageResource(R.drawable.ptqkdryac);
                            break;
                        case "01d":
                        case "01n":
                            weatherImage.setImageResource(R.drawable.pi5dxrki9);
                            break;
                        case "2d":
                        case "2n":
                            weatherImage.setImageResource(R.drawable._tyog4nyc);
                            break;
                        case "09d":
                        case "09n":
                        case "10n":
                        case "10d":
                            weatherImage.setImageResource(R.drawable.ptq8ya6bc);
                            break;
                        case "13d":
                        case "13n":
                            weatherImage.setImageResource(R.drawable._cro6jeck);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

}