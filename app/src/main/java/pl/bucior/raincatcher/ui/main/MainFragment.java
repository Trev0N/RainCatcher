package pl.bucior.raincatcher.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.bucior.raincatcher.R;
import pl.bucior.raincatcher.WeatherApi;
import pl.bucior.raincatcher.WeatherResponse;
import pl.bucior.raincatcher.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private WeatherService weatherService;
    private ImageView weatherImage;
    private TextView temperature, temperatureFeeling, pressure, humidity, windSpeed, rain, rainText, rainSum;
    private final double kelwin = 273.15;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

//    @Override
//    public void onStart() {
//        LocationRequest mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(60000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationCallback mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        //TODO: UI updates.
//                    }
//                }
//            }
//        };
//        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
//        super.onStart();
//    }
//
    public void locationCheck(){
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i(TAG,"Found location:" + locationResult.toString());

            }
        };
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
        }
        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
        }
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
        weatherService = WeatherApi.getClient().create(WeatherService.class);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationCheck();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                weatherService.getWeather(location.getLatitude(), location.getLongitude(), "c5d033f0c9ff79e419c4ca2b43abc550", "pl").enqueue(new Callback<WeatherResponse>() {

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
                                            rainSum.setText(String.valueOf(weatherResponse.getHourly().stream()
                                                    .filter(weatherDetail -> weatherDetail.getRain()!=null)
                                                    .map(WeatherResponse.WeatherDetail::getRain)
                                                    .mapToDouble(WeatherResponse.Rain::getH).sum()));
                                            if(weatherResponse.getCurrent().getRain()!=null) {
                                                rain.setText(String.format("%sl/m2", weatherResponse.getCurrent().getRain().getH()));
                                            }else {
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