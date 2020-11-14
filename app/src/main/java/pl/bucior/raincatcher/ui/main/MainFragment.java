package pl.bucior.raincatcher.ui.main;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
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

import java.util.Objects;
import java.util.Optional;

import pl.bucior.raincatcher.MyLocationService;
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
    private MyLocationService myLocationService;
    private ImageView weatherImage;
    private TextView temperature, temperatureFeeling, pressure, humidity, windSpeed;
    private final double kelwin = 273.15;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

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
        weatherImage = root.findViewById(R.id.weatherImage);
        weatherService = WeatherApi.getClient().create(WeatherService.class);
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        LocationProvider locationProvider = locationManager.getProvider(Objects.requireNonNull(locationManager.getBestProvider(createCoarseCriteria(), false)));
        myLocationService = new MyLocationService(weatherImage, temperature, temperatureFeeling, pressure, humidity, windSpeed, weatherService);
        if (locationProvider != null) {
            locationManager.requestLocationUpdates(locationProvider.getName(), 500, 1000, myLocationService);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}