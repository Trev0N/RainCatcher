package pl.bucior.raincatcher.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pl.bucior.raincatcher.R;
import pl.bucior.raincatcher.WeatherApi;
import pl.bucior.raincatcher.WeatherResponse;
import pl.bucior.raincatcher.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private MainViewModel mViewModel;
    private WeatherService weatherService;
    private ImageView weatherImage;
    private final static int PERMISSION_ID = 44;
    private TextView temperature, temperatureFeeling, pressure, humidity, windSpeed, rain, rainText, rainSum;
    private AnyChartView rainChart;
    private final double kelwin = 273.15;
    private List<DataEntry> data = new ArrayList<>();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        checkAndRequestPermissions();
        final View root = inflater.inflate(R.layout.main_fragment, container, false);
        temperature = root.findViewById(R.id.temperature);
        temperatureFeeling = root.findViewById(R.id.temperatureFeeling);
        pressure = root.findViewById(R.id.pressure);
        humidity = root.findViewById(R.id.humidity);
        windSpeed = root.findViewById(R.id.windSpeed);
        rain = root.findViewById(R.id.rain);
        rainText = root.findViewById(R.id.rainText);
        rainSum = root.findViewById(R.id.rainSum);
        weatherImage = root.findViewById(R.id.weatherImage);
        rainChart = root.findViewById(R.id.rainChart);
        weatherService = WeatherApi.getClient().create(WeatherService.class);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        weatherService.getWeather(location.getLatitude(), location.getLongitude(), "c5d033f0c9ff79e419c4ca2b43abc550", "pl").enqueue(new Callback<WeatherResponse>() {

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                                WeatherResponse weatherResponse = response.body();
                                if (weatherResponse != null) {
                                    temperature.setText(String.format("%s°C", Math.round((weatherResponse.getCurrent().getTemp() - kelwin) * 10.0) / 10.0));
                                    temperatureFeeling.setText(String.format("%s°C", Math.round((weatherResponse.getCurrent().getFeels_like() - kelwin) * 10.0) / 10.0));
                                    pressure.setText(String.format("%shPa", weatherResponse.getCurrent().getPressure()));
                                    humidity.setText(String.format("%s%%", weatherResponse.getCurrent().getHumidity()));
                                    windSpeed.setText(String.format("%sm/s", weatherResponse.getCurrent().getWind_speed()));
                                    rainSum.setText(String.valueOf(weatherResponse.getHourly().stream()
                                            .filter(weatherDetail -> weatherDetail.getRain() != null)
                                            .map(WeatherResponse.WeatherDetail::getRain)
                                            .mapToDouble(WeatherResponse.Rain::getH).sum()));
                                    configureChartData(weatherResponse.getHourly());
                                    if (weatherResponse.getCurrent().getRain() != null) {
                                        rain.setText(String.format("%sl/m2", weatherResponse.getCurrent().getRain().getH()));
                                    } else {
                                        rainText.setVisibility(View.GONE);
                                        rain.setVisibility(View.GONE);
                                    }
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
                                        case "02d":
                                        case "02n":
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
                            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureChartData(List<WeatherResponse.WeatherDetail> weatherDetails) {
        int i = 0, j = 0;
        Double rainSum = 0d;
        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "0-6");
        titleMap.put(1, "6-12");
        titleMap.put(2, "12-18");
        titleMap.put(3, "18-24");
        titleMap.put(4, "24-30");
        titleMap.put(5, "30-36");
        titleMap.put(6, "36-42");
        titleMap.put(7, "42-48");
        for (WeatherResponse.WeatherDetail weatherDetail : weatherDetails) {
            if (weatherDetail.getRain() != null && weatherDetail.getRain().getH() != null) {
                rainSum += weatherDetail.getRain().getH();
            }
            if (i == 5) {
                data.add(new ValueDataEntry(titleMap.get(j), rainSum));
                j++;
                i = 0;
                rainSum = 0d;
            } else {
                i++;
            }
        }
        Cartesian cartesian = AnyChart.area();
        if (data.size() == 0) {
            rainChart.setVisibility(View.GONE);
            return;
        } else {
            rainChart.setVisibility(View.VISIBLE);
        }
        cartesian.area(data).tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .format("{%Value}mm/m²");

        cartesian.animation(true);
        cartesian.title("Wykres opadów w ciągu najbliższych 48 godzin");

        cartesian.yAxis(0d).labels().adjustFontSize(true, true);
        cartesian.yAxis(0d).labels().format("{%Value}{groupsSeparator: }");

        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0d);
        cartesian.xAxis(0d).title("Czas [h]");
        cartesian.yAxis(0d).title("Opady [mm/m²]");
        rainChart.setChart(cartesian);

    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        int acl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int afl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int internet = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int cfl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (cfl != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        if (acl != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (afl != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionsNeeded.size() > 0) {
            requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_ID);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}