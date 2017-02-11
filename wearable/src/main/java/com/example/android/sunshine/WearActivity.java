package com.example.android.sunshine;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class WearActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView mHighTempTextView, mLowTempTextView;
    private ImageView mWeatherIcon;
    private int mHiTemp, mLowTemp, mCondition;

    private static final String HITEMP_KEY = "com.example.android.sunshine.key.hitemp";
    private static final String LOWTEMP_KEY = "com.example.android.sunshine.key.lowtemp";
    private static final String CONDITION_KEY = "com.example.android.sunshine.key.condition";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHighTempTextView = (TextView) stub.findViewById(R.id.high_temperature);
                mLowTempTextView = (TextView) stub.findViewById(R.id.low_temperature);
                mWeatherIcon = (ImageView) stub.findViewById(R.id.weather_icon);

            }
        });

        if(savedInstanceState == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        else {
            mHiTemp = savedInstanceState.getInt("hiTemp");
            mLowTemp = savedInstanceState.getInt("lowTemp");
            mCondition = savedInstanceState.getInt("condition");
            mHighTempTextView.setText(getString(R.string.high_temp) + " " + mHiTemp + (char) 0x00B0);
            mLowTempTextView.setText(getString(R.string.low_temp) + " " + mLowTemp + (char) 0x00B0);
            mWeatherIcon.setImageResource(getSmallArtResourceIdForWeatherCondition(mCondition));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/weather") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    int hiTemp = dataMap.getInt(HITEMP_KEY);
                    int lowTemp = dataMap.getInt(LOWTEMP_KEY);
                    int condition = dataMap.getInt(CONDITION_KEY);
                    mHighTempTextView.setText(getString(R.string.high_temp) + " " + hiTemp + (char) 0x00B0);
                    mLowTempTextView.setText(getString(R.string.low_temp) + " " + lowTemp + (char) 0x00B0);
                    mWeatherIcon.setImageResource(getSmallArtResourceIdForWeatherCondition(condition));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.e("Data Item Deleted", "Data Item Deleted");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("hiTemp", mHiTemp);
        savedInstanceState.putInt("lowTemp", mLowTemp);
        savedInstanceState.putInt("condition", mCondition);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("Connection Failed", "Connection Failed");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e("Connection Suspended", "Connection Suspended");
    }

    public static int getSmallArtResourceIdForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        } else if (weatherId >= 900 && weatherId <= 906) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 958 && weatherId <= 962) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 951 && weatherId <= 957) {
            return R.drawable.ic_clear;
        }
        return R.drawable.ic_storm;
    }
}