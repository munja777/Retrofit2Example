package com.example.artur.retrofit2example;


import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class WeatherRestAdapter {

    private final String TAG = getClass().getSimpleName();
    private WeatherApi mApi;
    private static final String WEATHER_URL="http://api.openweathermap.org/";
    private static final String OPEN_WEATHER_API = "6cb31f00b7e3fd1a17ac5564b24f9729";


    WeatherRestAdapter() {

        Retrofit mRestAdapter = new Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRestAdapter.create(WeatherApi.class);
        Log.d(TAG, "WeatherRestAdapter -- created");
    }


    void testWeatherApi(String city, Callback<Weather> callback){

        Log.d(TAG, "testWeatherApi: for city:" + city);
        mApi.getWeatherFromApi(city, OPEN_WEATHER_API).enqueue(callback);
    }


    Weather testWeatherApiSync(String city) {

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