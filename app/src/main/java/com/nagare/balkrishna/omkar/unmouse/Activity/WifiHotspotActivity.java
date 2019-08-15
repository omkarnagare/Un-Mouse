package com.nagare.balkrishna.omkar.unmouse.Activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nagare.balkrishna.omkar.unmouse.App.WifiApManager;
import com.nagare.balkrishna.omkar.unmouse.Entity.CommunicationType;
import com.nagare.balkrishna.omkar.unmouse.R;

public class WifiHotspotActivity extends AppCompatActivity {

    private static final String COMMUNICATION_TYPE = "communicationType";

    private TextView mSetUpHotspotTextView = null;
    private EditText hostpotssidEditText = null;
    private EditText hotspotPasswordEditText = null;
    private ProgressDialog progress = null;
    private View focusedView = null;
    private
    TextView mHeaderTextView = null;
    private Typeface custom_font_header = null;

    private WifiManager mWifiManager = null;

    private boolean mWasWifiOn = false;
    private boolean isWifiHotspotEnabled = false;
    private Thread checkHotspotEnabledthread = null;

    private Button mPeekButton = null;
    private int mPasswordCursorStart = 0;
    private int mPasswordCursorEnd = 0;
    private Window mWindow = null;
    private InterstitialAd mInterstitialAd = null;
    private boolean timeOutOccured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mWindow = this.getWindow();

        mWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_hotspot);

        setUpUI();
        switchOffWiFi();

    }

    private void setUpUI() {

        setUpMobileAdds();

        mHeaderTextView = (TextView) findViewById(R.id.header_wifi_hotspot_activity);
        custom_font_header = Typeface.createFromAsset(getAssets(), "fonts/broadway.ttf");
        mHeaderTextView.setTypeface(custom_font_header);
        mSetUpHotspotTextView = (TextView) findViewById(R.id.setup_hotspot_tv);
        hostpotssidEditText = (EditText) findViewById(R.id.wifi_ssid);
        hotspotPasswordEditText = (EditText) findViewById(R.id.wifi_hotspot_password);
        mPeekButton = (Button) findViewById(R.id.password_peek);

        hostpotssidEditText.setText(Build.MODEL + "-UnMouse");

        progress = new ProgressDialog(this);
        progress.setTitle("Wait!!");
        progress.setMessage("Hotspot is being created ...");
        progress.setCancelable(false);

        mSetUpHotspotTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });

        mPasswordCursorStart = hotspotPasswordEditText.getSelectionStart();
        mPeekButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v,
                                   MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        mPasswordCursorEnd = hotspotPasswordEditText.getSelectionEnd();
                        hotspotPasswordEditText.setTransformationMethod(null);
                        break;
                    case MotionEvent.ACTION_UP:
                        hotspotPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
                        hotspotPasswordEditText.setSelection(mPasswordCursorStart,
                                mPasswordCursorEnd);
                        break;
                }
                return true;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            findViewById(R.id.ssid_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.password_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.hl1).setVisibility(View.INVISIBLE);
            findViewById(R.id.hl2).setVisibility(View.INVISIBLE);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
        if (isWifiHotspotEnabled) {
            mSetUpHotspotTextView.setText("Proceed");
            findViewById(R.id.ssid_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.password_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.hl1).setVisibility(View.INVISIBLE);
            findViewById(R.id.hl2).setVisibility(View.INVISIBLE);
        }
    }

    private void setUpMobileAdds() {

        AdView mAdView = (AdView) findViewById(R.id.mobile_add_hotspot_activity);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("5F580625FEC4ACA3B6ACC70C67490CBB")
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7466893006911881/6583660462");

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
//                mInterstitialAd.show();
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
//                .addTestDevice("5F580625FEC4ACA3B6ACC70C67490CBB")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void checkFields() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
            if (!isWifiHotspotEnabled) {
                final AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle("Hotspot creation process");
                mAlertDialogBuilder.setMessage("Taking you to the settings page. Do you want to continue?");
                mAlertDialogBuilder.setCancelable(false);

                mAlertDialogBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                final ComponentName cn = new ComponentName(
                                        "com.android.settings",
                                        "com.android.settings.TetherSettings");
                                intent.setComponent(cn);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                dialog.cancel();
                            }
                        });

                mAlertDialogBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WifiHotspotActivity.this, "UnMouse requires wifi hotspot to be created to function", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.cancel();
                                finish();
                            }
                        });
                mAlertDialogBuilder.create().show();
            } else {
                showNotificationDialogue();
            }

        } else {

            isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
            if (!isWifiHotspotEnabled) {

                final String hotspotID = hostpotssidEditText.getText().toString();
                final String password = hotspotPasswordEditText.getText().toString();

                hostpotssidEditText.setError(null);
                hotspotPasswordEditText.setError(null);

                if (TextUtils.isEmpty(password)) {
                    hotspotPasswordEditText.setError("Password field cannot be blank");
                    focusedView = hotspotPasswordEditText;
                } else if (password.length() < 8) {
                    hotspotPasswordEditText.setError("Password field must have atleast 8 characters.");
                    focusedView = hotspotPasswordEditText;
                } else {

                    if (TextUtils.isEmpty(hotspotID)) {
                        hostpotssidEditText.setError("Hotspot name field cannot be blank");
                        focusedView = hostpotssidEditText;
                    } else {

                        focusedView = null;
                        progress.show();

                        if (checkHotspotEnabledthread != null) {
                            try {
                                checkHotspotEnabledthread.interrupt();
                                checkHotspotEnabledthread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            checkHotspotEnabledthread = null;
                        }

                        checkHotspotEnabledthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
                                if (!isWifiHotspotEnabled) {

                                    startHotspot(hotspotID, password);

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WifiHotspotActivity.this, "Hotspot was already on.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                Thread dismissProgressDialogueIfTimeOut = new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            Thread.sleep(60000);
                                            timeOutOccured = true;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (progress.isShowing()) {
                                                        progress.dismiss();
                                                    }
                                                    Toast.makeText(WifiHotspotActivity.this, "Could not create hotspot. Please try again.", Toast.LENGTH_LONG).show();
                                                    isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                dismissProgressDialogueIfTimeOut.start();
                                timeOutOccured = false;

                                while (true) {

                                    isWifiHotspotEnabled = WifiApManager.getApState(getApplicationContext());
                                    if (isWifiHotspotEnabled || timeOutOccured) {
                                        progress.dismiss();

                                        if (dismissProgressDialogueIfTimeOut != null) {

                                            try {
                                                dismissProgressDialogueIfTimeOut.interrupt();
                                                dismissProgressDialogueIfTimeOut.join();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            dismissProgressDialogueIfTimeOut = null;

                                        }

                                        break;
                                    } else {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }

                                if (!timeOutOccured) {
                                    showNotificationDialogue();
                                }

                            }
                        });

                        checkHotspotEnabledthread.start();

                    }
                }

                if (focusedView != null) {
                    focusedView.requestFocus();
                }

            } else {

                showNotificationDialogue();

            }
        }

    }

    private void showNotificationDialogue() {

        final AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Hotspot created successfully!");
        mAlertDialogBuilder.setMessage("Make sure your PC is connected to the hotspot just created and Press OK");
        mAlertDialogBuilder.setCancelable(false);

        mAlertDialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(WifiHotspotActivity.this, TCPActivity.class);
                        intent.putExtra(COMMUNICATION_TYPE, CommunicationType.WIFI.getVal());
                        startActivity(intent);
//                        finish();

                        dialog.cancel();
                    }
                });

        mAlertDialogBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                WifiApManager.configApState(getApplicationContext(), false);
//                                switchOnWiFiIfWasOnBefore();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WifiHotspotActivity.this, "Hotspot stopped.", Toast.LENGTH_SHORT).show();
                                        mSetUpHotspotTextView.setText("Setup Hotspot");

                                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
                                            findViewById(R.id.ssid_layout).setVisibility(View.VISIBLE);
                                            findViewById(R.id.password_layout).setVisibility(View.VISIBLE);
                                            findViewById(R.id.hl1).setVisibility(View.VISIBLE);
                                            findViewById(R.id.hl2).setVisibility(View.VISIBLE);
                                        }

                                    }
                                });
                            }
                        });
                        thread.start();

                        dialog.cancel();
                    }
                });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlertDialogBuilder.create().show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        progress.dismiss();

        if (checkHotspotEnabledthread != null) {
            try {
                checkHotspotEnabledthread.interrupt();
                checkHotspotEnabledthread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            checkHotspotEnabledthread = null;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WifiApManager.configApState(getApplicationContext(), false);
                switchOnWiFiIfWasOnBefore();
            }
        });
        thread.start();
        super.onDestroy();
    }

    private void startHotspot(String hotspotID,
                              String password) {
        WifiApManager.setSsid(hotspotID);
        WifiApManager.setPassword(password);
        WifiApManager.configApState(getApplicationContext(), true);
    }

    private void switchOffWiFi() {

        Thread switchOffWiFiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (mWifiManager.isWifiEnabled()) {
                    mWasWifiOn = true;
                    mWifiManager.setWifiEnabled(false);
                }
            }
        });
        switchOffWiFiThread.start();
    }

    private void switchOnWiFiIfWasOnBefore() {
        if (mWifiManager != null) {
            if (mWasWifiOn && !mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        }
    }
}
