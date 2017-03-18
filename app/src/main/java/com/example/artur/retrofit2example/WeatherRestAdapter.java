package com.example.artur.retrofit2example;


import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRestAdapter {

    protected final String TAG = getClass().getSimpleName();
    protected Retrofit mRestAdapter;
    protected WeatherApi mApi;
    static final String WEATHER_URL="http://api.openweathermap.org/";
    static final String OPEN_WEATHER_API = "6cb31f00b7e3fd1a17ac5564b24f9729";


    public WeatherRestAdapter() {

        mRestAdapter = new Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRestAdapter.create(WeatherApi.class);
        Log.d(TAG, "WeatherRestAdapter -- created");
    }


    public void testWeatherApi(String city, Callback<Weather> callback){

        Log.d(TAG, "testWeatherApi: for city:" + city);
        mApi.getWeatherFromApi(city, OPEN_WEATHER_API).enqueue(callback);
    }


    public Weather testWeatherApiSync(String city) {

        Log.d(TAG, "testWeatherApi: for city:" + city);
        Call<Weather> call = mApi.getWeatherFromApi(city, OPEN_WEATHER_API);
        Weather result = null;

        try {
            result = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}