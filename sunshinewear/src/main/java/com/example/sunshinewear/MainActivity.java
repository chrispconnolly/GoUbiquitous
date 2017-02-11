package com.example.sunshinewear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mHighTempTextView, mLowTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHighTempTextView = (TextView) stub.findViewById(R.id.high_temperature);
                mLowTempTextView = (TextView) stub.findViewById(R.id.low_temperature);
                mHighTempTextView.setText(getString(R.string.high_temp).replace("-", "11"));
                mLowTempTextView.setText(getString(R.string.low_temp).replace("-", "19"));
            }
        });
    }
}
