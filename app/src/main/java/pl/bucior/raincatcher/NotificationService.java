package pl.bucior.raincatcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private final static String apiKey = "ObUbGJ1Ara4KVOI2mArOdADnOTjkXssK";
    private static final String TAG = "NotificationService";
    static final int NOTIFICATION_ID = 543;

    private boolean currentlyProcessingLocation = false;
    public static boolean isServiceRunning = false;
    private GoogleApiClient googleApiClient;
    private Location firstLocation;
    private WeatherResponse weatherResponse;
    private SharedPreferences sharedPreferences;
    private Date notificationDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Notification") && sharedPreferences.getBoolean("Notification", false)) {
            isServiceRunning = false;
            startServiceAndSendNotification("RainCatcher", "Aplikacja działa w tle", false);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void startServiceAndSendNotification(String textTitle, String text, boolean willRain) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String NOTIFICATION_CHANNEL_ID = "pl.bucior.raincatcher";
        String channelName = "LocationService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }
        manager.createNotificationChannel(chan);
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setContentTitle(textTitle)
                .setTicker(getResources().getString(R.string.app_name))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_EVENT)
                .build();
        if (!isServiceRunning) {
            notification.flags = notification.flags | Notification.DEFAULT_VIBRATE;
            startForeground(NOTIFICATION_ID, notification);
            isServiceRunning = true;
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
//            c.add(Calendar.HOUR,1); //TODO DO TESTÓW
            notificationDate = c.getTime();
        } else if (willRain) {
            manager.notify(NOTIFICATION_ID, notification);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
//                c.add(Calendar.HOUR,1); //TODO DO TESTÓW
            notificationDate = c.getTime();
        }
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void processLocation(Location location) {
        if (notificationDate.before(new Date())) {
            firstLocation = location;
            getNearestMeasurementByLocation(location.getLatitude(), location.getLongitude());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getNearestMeasurementByLocation(double latitude, double longitude) {
        WeatherService weatherService = WeatherApi.getClient().create(WeatherService.class);
        weatherService.getWeather(latitude, longitude, apiKey, "pl").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                weatherResponse = response.body();
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLocationChanged(Location location) {
        if (!sharedPreferences.getBoolean("Notification", false)) {
            isServiceRunning = false;
            stopLocationUpdates();
            stopSelf();
        }
        if (location != null) {
            if (firstLocation == null) {
                firstLocation = location;
                return;
            }
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            processLocation(location);
        }
    }


    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException se) {
            Log.e(TAG, "Go into settings and find RainCatcherApp and enable Location.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.");
    }
}