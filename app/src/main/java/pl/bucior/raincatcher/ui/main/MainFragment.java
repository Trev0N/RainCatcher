package pl.bucior.raincatcher.ui.main;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView textView,temperature;
    private final double kelwin = 273.15;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment, container, false);
        textView = root.findViewById(R.id.message1);
        temperature = root.findViewById(R.id.temperature);
        weatherService = WeatherApi.getClient().create(WeatherService.class);
        weatherService.getWeather(50.022570, 19.910180, "c5d033f0c9ff79e419c4ca2b43abc550","pl").enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    temperature.setText(String.valueOf(Math.round(weatherResponse.getCurrent().getTemp() - kelwin)));
                    textView.setText(String.valueOf(weatherResponse.getCurrent().getTemp()));
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