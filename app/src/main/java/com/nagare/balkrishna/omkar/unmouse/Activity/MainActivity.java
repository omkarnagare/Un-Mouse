package com.nagare.balkrishna.omkar.unmouse.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nagare.balkrishna.omkar.unmouse.R;

public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private
    TextView mWifiTextView = null;
    private
    TextView mBluetoothTextView = null;
    private
    TextView mHeaderTextView = null;
    private
    TextView mDescriptionTextView = null;
    private Window mWindow = null;

    private Typeface custom_font_description = null;
    private Typeface custom_font_header = null;

    InterstitialAd mInterstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindow = this.getWindow();

        mWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        custom_font_description = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        custom_font_header = Typeface.createFromAsset(getAssets(), "fonts/broadway.ttf");

        setUpUIComponents();

        setUpOnClickMethods();

    }

    private void setUpOnClickMethods() {
        mWifiTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,
                        WifiHotspotActivity.class);
                startActivity(intent);
//                finish();

            }
        });

        mBluetoothTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        BluetoothDeviceSelectActivity.class);
                startActivity(intent);
//                finish();

            }
        });
    }

    private void setUpMobileAdds() {

        AdView mAdView = (AdView) findViewById(R.id.mobile_add_main_activity);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("BAEFA564A6DF839A4CFA254BBC93ACAC")
                .build();
        mAdView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8786806562583765/7737367536");

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
//                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("BAEFA564A6DF839A4CFA254BBC93ACAC")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private void setUpUIComponents() {
        setUpMobileAdds();

        mWifiTextView = (TextView) findViewById(R.id.wifi_tv);
        mBluetoothTextView = (TextView) findViewById(R.id.bluetooth_tv);
        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mHeaderTextView = (TextView) findViewById(R.id.header_main_activity);

        mDescriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());

//        mWifiTextView.setTypeface(custom_font_description);
//        mBluetoothTextView.setTypeface(custom_font_description);
        mDescriptionTextView.setTypeface(custom_font_description);
        mHeaderTextView.setTypeface(custom_font_header);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
