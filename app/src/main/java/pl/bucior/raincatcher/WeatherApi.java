package pl.bucior.raincatcher;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApi {

    private static Retrofit retrofit = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(new OkHttpClient
                            .Builder()
                            .build())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .create()))
                    .baseUrl("https://api.openweathermap.org/")
                    .build();
        }
        return retrofit;
    }
}
