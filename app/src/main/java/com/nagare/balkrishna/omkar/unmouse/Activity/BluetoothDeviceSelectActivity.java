package com.nagare.balkrishna.omkar.unmouse.Activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nagare.balkrishna.omkar.unmouse.App.UnMouseApp;
import com.nagare.balkrishna.omkar.unmouse.Entity.CommunicationType;
import com.nagare.balkrishna.omkar.unmouse.GreenDao.BluetoothInfoDao;
import com.nagare.balkrishna.omkar.unmouse.GreenDao.DaoSession;
import com.nagare.balkrishna.omkar.unmouse.R;
import com.nagare.balkrishna.omkar.unmouse.View.BluetoothDeviceListAdapter;
import com.nagare.balkrishna.omkar.unmouse.View.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceSelectActivity
        extends AppCompatActivity {
    private static final String TAG = "BluetoothDeviceSelect";
    private static final String DEVICE_ADDRESS = "bluetoothDeviceAddress";
    private static final String COMMUNICATION_TYPE = "communicationType";

    private DaoSession mDaoSession = null;
    private BluetoothInfoDao mBluetoothInfoDao = null;

    private BluetoothAdapter mBluetoothAdapter = null;

    private ListView mDeviceListView = null;
    private BluetoothDeviceListAdapter mDeviceListAdapter = null;
    private ProgressDialog mProgressDlg = null;
    private AlertDialog.Builder mAlertDialogBuilder = null;
    private View guideline = null;

    private List<BluetoothDeviceModel> mSavedDeviceList = null;
    private List<BluetoothDeviceModel> mDeviceList = null;

//    private int mHeight = 0;
//    private ConstraintLayout activityLayout = null;
//    private int listViewHeight = 0;
    private Window mWindow = null;

    TextView mHeaderTextView = null;
    private Typeface custom_font_header = null;
    private ImageView mBackGroundView = null;
    private TextView mRescanTextView = null;
    private InterstitialAd mInterstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mWindow = this.getWindow();

        mWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_select);

        setUpDB();

        setUpUIComponents();

        setUpBluetooth();
    }

    private void setUpBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {

            if (mBluetoothAdapter.isEnabled()) {

                mBluetoothAdapter.startDiscovery();
                mBackGroundView.setVisibility(View.VISIBLE);
                mProgressDlg.show();

            } else {

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1000);

            }

        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

    }

    private void showUnsupported() {

        Toast.makeText(this, "Bluetooth is not supported in this device", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int result_enable_bt,
                                    Intent data) {
        super.onActivityResult(requestCode,
                result_enable_bt,
                data);

        if (result_enable_bt == RESULT_OK) {
            Toast.makeText(this, "Turned On", Toast.LENGTH_SHORT).show();
        } else if (result_enable_bt == RESULT_CANCELED) {
            Toast.makeText(this, "UnMouse requires Bluetooth to be turned ON to function", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    protected void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    mBluetoothAdapter.startDiscovery();

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mBackGroundView.setVisibility(View.VISIBLE);
                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();
                mBackGroundView.setVisibility(View.GONE);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG,
                        "onReceive: name = " + device.getName() + ", address = " + device.getAddress());
                if (mDeviceList != null) {

                    BluetoothDeviceModel bluetoothDeviceModel = new BluetoothDeviceModel();
                    bluetoothDeviceModel.setBluetoothDeviceAddress(device.getAddress());
                    bluetoothDeviceModel.setBluetoothDeviceName(device.getName());

                    if(!mDeviceList.contains(bluetoothDeviceModel)) {
                        mDeviceList.add(bluetoothDeviceModel);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mDeviceListAdapter != null) {
                                    mDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        }
    };

    private void setUpUIComponents() {

        setUpMobileAdds();

        mHeaderTextView = (TextView) findViewById(R.id.header_bluetooth_device_select_activity);
        custom_font_header = Typeface.createFromAsset(getAssets(), "fonts/broadway.ttf");
        mHeaderTextView.setTypeface(custom_font_header);

        mBackGroundView = (ImageView) findViewById(R.id.background_view);

//        activityLayout = (ConstraintLayout) findViewById(R.id.device_select_activity_layout);

        guideline = (View) findViewById(R.id.top_hgl);
        mProgressDlg = new ProgressDialog(this);

        mProgressDlg.setTitle("Scanning in progress!");
        mProgressDlg.setMessage("Searching for nearby bluetooth devices...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBackGroundView.setVisibility(View.GONE);
                mProgressDlg.dismiss();
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });

        mDeviceList = new ArrayList<>();

        mDeviceListView = (ListView) findViewById(R.id.devices_list);
        mDeviceListAdapter = new BluetoothDeviceListAdapter(mDeviceList, this, R.layout.bluetooth_device, this);
        mDeviceListView.setAdapter(mDeviceListAdapter);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                BluetoothDeviceModel dataModel = mDeviceList.get(position);
//                Toast.makeText(BluetoothDeviceSelectActivity.this, dataModel.getBluetoothDeviceName(),Toast.LENGTH_SHORT).show();
                showAlert(dataModel);

            }
        });

        mRescanTextView = (TextView) findViewById(R.id.rescan_tv);
        mRescanTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mProgressDlg.isShowing()){
                    mProgressDlg.show();
                    if (mBluetoothAdapter != null) {
                        mBluetoothAdapter.startDiscovery();
                    }
                }

            }
        });

    }

    private void setUpMobileAdds() {

        AdView mAdView = (AdView) findViewById(R.id.mobile_add_device_select_bluetooth);
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
//                .addTestDevice("BAEFA564A6DF839A4CFA254BBC93ACAC")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showAlert(final BluetoothDeviceModel bluetoothDeviceModel) {

        boolean isAlreadyPaired = checkIfPairedAlready(bluetoothDeviceModel);

        if (!isAlreadyPaired) {
            new AlertDialog.Builder(this)
                    .setTitle("Unknown device selected !")
                    .setMessage("Pair the device from bluetooth settings first. Redirect to Settings page?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            launchBluetoothSettingsActivity();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {

            if (mAlertDialogBuilder != null) {
                mAlertDialogBuilder = null;
            }

            mAlertDialogBuilder = new AlertDialog.Builder(this);
            mAlertDialogBuilder.setTitle("Confirm selection");
            mAlertDialogBuilder.setMessage("Do you wish to connect to the device : \n" + bluetoothDeviceModel.getBluetoothDeviceName());
            mAlertDialogBuilder.setCancelable(false);

            mAlertDialogBuilder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startBluetoothTCPActivity(bluetoothDeviceModel);
                            dialog.cancel();
                        }
                    });

            mAlertDialogBuilder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            mAlertDialogBuilder.create().show();
        }

    }

    private void launchBluetoothSettingsActivity() {

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings",
                "com.android.settings.bluetooth.BluetoothSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        finish();
    }

    private boolean checkIfPairedAlready(BluetoothDeviceModel bluetoothDeviceModel) {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                if (device.getAddress().equals(bluetoothDeviceModel.getBluetoothDeviceAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startBluetoothTCPActivity(BluetoothDeviceModel bluetoothDeviceModel) {

        Intent intent = new Intent(BluetoothDeviceSelectActivity.this, TCPActivity.class);
        intent.putExtra(DEVICE_ADDRESS, bluetoothDeviceModel.getBluetoothDeviceAddress());
        intent.putExtra(COMMUNICATION_TYPE, CommunicationType.BLUETOOTH.getVal());
        startActivity(intent);
        finish();

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        rearrangeListViewPositions();
//    }
//
//    private boolean rearrangeListViewPositions() {
//
//        mHeight = activityLayout.getHeight();
//
//        int headerHeight = mHeaderTextView.getHeight();
//
//        int currentlistViewHeight = savedDeivceLayout.getHeight();
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
//        params.guidePercent = ( currentlistViewHeight + headerHeight )/(mHeight*1.0f);
//        guideline.setLayoutParams(params);
//        if(currentlistViewHeight != listViewHeight){
//            listViewHeight = currentlistViewHeight;
//            return true;
//        }else{
//            return false;
//        }
//
//    }

    private void setUpDB() {
        mDaoSession = ((UnMouseApp) getApplication()).getDaoSession();
        mBluetoothInfoDao = mDaoSession.getBluetoothInfoDao();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);

        mProgressDlg.dismiss();

        if (mDaoSession != null) {
            mDaoSession.clear();
        }
        super.onDestroy();
    }
}
