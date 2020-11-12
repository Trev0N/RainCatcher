package pl.bucior.raincatcher.ui.main;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Optional;

import pl.bucior.raincatcher.R;
import pl.bucior.raincatcher.WeatherApi;
import pl.bucior.raincatcher.WeatherResponse;
import pl.bucior.raincatcher.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private WeatherService weatherService;
    private ImageView weatherImage;
    private TextView temperature, temperatureFeeling, pressure, humidity, windSpeed;
    private final double kelwin = 273.15;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.main_fragment, container, false);
        temperature = root.findViewById(R.id.temperature);
        temperatureFeeling = root.findViewById(R.id.temperatureFeeling);
        pressure = root.findViewById(R.id.pressure);
        humidity = root.findViewById(R.id.humidity);
        windSpeed = root.findViewById(R.id.windSpeed);
        weatherImage = root.findViewById(R.id.weatherImage);
        weatherService = WeatherApi.getClient().create(WeatherService.class);
        weatherService.getWeather(50.022570, 19.910180, "c5d033f0c9ff79e419c4ca2b43abc550", "pl").enqueue(new Callback<WeatherResponse>() {

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
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}