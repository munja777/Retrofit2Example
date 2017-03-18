package com.example.artur.retrofit2example;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    private RetainedAppData mRetainedAppData;
    private ProgressBar mProgressBar;
    private EditText mInputCityName;
    private TextView mCityNameTextView;
    private TextView mCountryNameTextView;
    private TextView mCodTextView;
    private TextView mCoordsTextView;
    private TextView mTempTextView;
    private TextView mSunriseTextView;
    private TextView mSunsetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
        mInputCityName = (EditText) findViewById(R.id.input_city_id);

        if (savedInstanceState != null) {

            if (getLastCustomNonConfigurationInstance() != null) {

                mRetainedAppData = (RetainedAppData) getLastCustomNonConfigurationInstance();
                Log.d(TAG,"onCreate(): Reusing retained data set");
            }
        } else {

            mRetainedAppData = new RetainedAppData();
            Log.d(TAG, "onCreate(): Creating new  data set");
        }

        mRetainedAppData.setAppContext(this);

        if (mRetainedAppData.mData != null) {

            updateUXWithWeather(mRetainedAppData.mData);
        }

        if  (mRetainedAppData.isFetchInProgress()) {

            mProgressBar.setVisibility(View.VISIBLE);
        } else { mProgressBar.setVisibility(View.INVISIBLE); }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mRetainedAppData.setAppContext(null);
        Log.d(TAG,"onDestroy()");
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() { return mRetainedAppData; }

    public void expandWeatherSync(View v) {

        hideKeyboard(MainActivity.this, mInputCityName.getWindowToken());
        String city = mInputCityName.getText().toString();

        if (city.isEmpty()) {

            Toast.makeText(getApplicationContext(),"No city specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mRetainedAppData.runRetrofitTestSync(city);
    }

    public void expandWeatherAsync(View v) {

        hideKeyboard(MainActivity.this, mInputCityName.getWindowToken());
        String city = mInputCityName.getText().toString();

        if (city.isEmpty()) {

            Toast.makeText(getApplicationContext(),"No city specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mRetainedAppData.runRetrofitTestAsync(city);
    }

    public static void hideKeyboard(Activity activity,

                                    IBinder windowToken) {

        InputMethodManager mgr = (InputMethodManager) activity.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    void updateUXWithWeather (final Weather data) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
                mInputCityName = (EditText) findViewById(R.id.input_city_id);
                mCityNameTextView = (TextView) findViewById(R.id.city_name_id);
                mCountryNameTextView = (TextView) findViewById(R.id.country_name_id);
                mCodTextView = (TextView) findViewById(R.id.cod_id);
                mCoordsTextView = (TextView) findViewById(R.id.coords_id);
                mTempTextView = (TextView) findViewById(R.id.temp_id);
                mSunriseTextView = (TextView) findViewById(R.id.sunrise_id);
                mSunsetTextView = (TextView) findViewById(R.id.sunset_id);

                if (mRetainedAppData.isFetchInProgress()) { mProgressBar.setVisibility(View.VISIBLE);
                } else { mProgressBar.setVisibility(View.INVISIBLE); }

                Resources res = getResources();
                String textToPrint = res.getString(R.string.city) + data.getName();
                mCityNameTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.country) + data.getCountry();
                mCountryNameTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.coordinates) +"(" + data.getLat() + "," + data.getLon() + ")";
                mCoordsTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.cod) + data.getCod();
                mCodTextView.setText(textToPrint);
                String tempF = String.format(Locale.UK,"Temperature: %.2f F", (data.getTemp() - 273.15) * 1.8 + 32.00);
                mTempTextView.setText(tempF);
                DateFormat dfLocalTz = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
                Date sunriseTime = new Date(data.getSunrise() * 1000);
                Date sunsetTime = new Date(data.getSunset() * 1000);
                textToPrint = res.getString(R.string.sunrise) + dfLocalTz.format(sunriseTime);
                mSunriseTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.sunset) + dfLocalTz.format(sunsetTime);
                mSunsetTextView.setText(textToPrint);
            }
        });
    }

    private static class RetainedAppData {

        private WeakReference<MainActivity> mActivityRef;
        protected final String TAG = "RTD";
        private Weather mData;
        private AtomicBoolean mInProgress = new AtomicBoolean(false);
        private WeatherRestAdapter mWeatherRestAdapter;
        private Callback<Weather> mWeatherCallback = new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {

                if (response.isSuccessful()) {

                    Weather data = response.body();
                    Log.d(TAG, "Async success: Weather: Name:" + data.getName() + ", cod:" + data.getCod()
                            + ",Coord:  (" + data.getLat() + "," + data.getLon()
                            + "), Temp:" + data.getTemp()
                            + "\nSunset:" + data.getSunset() + ", " + data.getSunrise()
                            + ", Country:" + data.getCountry());
                    mData = data;

                    if (mActivityRef.get() != null) {

                        mActivityRef.get().updateUXWithWeather(mData);
                        mActivityRef.get().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                        findViewById(R.id.progress_bar_id);
                                mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    mInProgress.set(false);

                } else {

                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"Error code:" + statusCode + ", Error:" + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {

                mInProgress.set(false);
                if (mActivityRef.get() != null) {

                    mActivityRef.get().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                    findViewById(R.id.progress_bar_id);
                            mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };

        public void runRetrofitTestAsync (final String city) {

            if ( (mActivityRef.get() != null) && (mInProgress.get())) {

                Toast.makeText(mActivityRef.get(),"Weather fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mWeatherRestAdapter == null)
                mWeatherRestAdapter = new WeatherRestAdapter();

            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            try {

                mInProgress.set(true);
                mWeatherRestAdapter.testWeatherApi(city, mWeatherCallback);
            } catch (Exception e) {

                Log.d(TAG, "Thread sleep error" + e);
            }
        }

        public void runRetrofitTestSync (final String city) {

            if ((mActivityRef.get() != null) && (mInProgress.get())) {

                Toast.makeText(mActivityRef.get(),"Weather fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            if (mWeatherRestAdapter == null)
                mWeatherRestAdapter = new WeatherRestAdapter();

            mInProgress.set(true);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        Thread.sleep(10000);
                        Weather data = mWeatherRestAdapter.testWeatherApiSync(city);

                        if (data != null) {

                            Log.d(TAG, "Sync: Data: cod:" + data.getName() + ", cod:" + data.getCod()
                                    + ",Coord: (" + data.getLat() + "," + data.getLon()
                                    + "), Temp:" + data.getTemp()
                                    + "\nSunset:" + data.getSunset() + ", " + data.getSunrise()
                                    + ", Country:" + data.getCountry());

                            mData = data;

                            if (mActivityRef.get() != null) {
                                mActivityRef.get().updateUXWithWeather(mData);
                            }
                        } else { Log.e(TAG, "Sync: no data fetched"); }

                    } catch (Exception ex) {

                        Log.e(TAG, "Sync: exception", ex);

                        if (mActivityRef.get() != null) {

                            mActivityRef.get().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                            findViewById(R.id.progress_bar_id);
                                    mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } finally { mInProgress.set(false); }
                }
            }).start();
        }

        void setAppContext (MainActivity ref) { mActivityRef = new WeakReference<>(ref); }

        boolean isFetchInProgress() { return mInProgress.get(); }
    }
}