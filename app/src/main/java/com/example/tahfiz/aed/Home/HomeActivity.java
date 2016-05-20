package com.example.tahfiz.aed.Home;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tahfiz.aed.AlarmIntentReceiver;
import com.example.tahfiz.aed.Contacts.ContactData;
import com.example.tahfiz.aed.Contacts.ContactRepo;
import com.example.tahfiz.aed.Drawer.BaseActivity;
import com.example.tahfiz.aed.Nearby.NearbyActivity;
import com.example.tahfiz.aed.Nearby.NearbyRepo;
import com.example.tahfiz.aed.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private ContactRepo contactRepo;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private BluetoothLeService mBluetoothLeService;
    private TextView connectionState;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;

    private ArrayList<BluetoothDevice> mLeDevices;
    private int deviceIndex = 0;
    private BluetoothDevice mDevices = null;
    private NearbyRepo nearbyRepo;
    private TextView dataVal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load
        // titles
        // from
        // strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from
        // strings.xml

        set(navMenuTitles, navMenuIcons);

        contactRepo = new ContactRepo(this);
        nearbyRepo = new NearbyRepo(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mLeDevices = new ArrayList<>();

        connectionState = (TextView) findViewById(R.id.connection_state);
        dataVal = (TextView) findViewById(R.id.data_value);

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }

        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            if (mDevices != null){
                menu.findItem(R.id.menu_connect).setVisible(true);
            }else {
                menu.findItem(R.id.menu_connect).setVisible(false);
            }
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDevices.getAddress());
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                NavDrawerToggle();
                break;

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            //initialize();
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(mDevices.getAddress());
                Log.d(TAG, "Connect request result=" + result);
            }
        }
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            finish();
            return;
        }
        //initialize();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);

        //mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevices != null){
            unregisterReceiver(mGattUpdateReceiver);
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }

    @SuppressWarnings("deprecation")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            showBTDialog();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!mLeDevices.contains(device)) {
                                mLeDevices.add(device);
                            }
                        }
                    });
                }
            };

    private void showBTDialog(){
        if (mLeDevices.size() > 0){
            int deviceCount = mLeDevices.size();
            if (deviceIndex < deviceCount){
                mDevices = mLeDevices.get(deviceIndex);
            }
            String[] deviceNames = new String[deviceCount];
            int i = 0;
            for (BluetoothDevice device : mLeDevices){
                deviceNames[i++] = device.getName();
            }
            SelectDeviceDialogFragment deviceDialog =
                    SelectDeviceDialogFragment.newInstance(deviceNames,deviceIndex);
            deviceDialog.show(getFragmentManager(), "deviceDialog");
        }
    }

    /**
     * Dialog to display a list of bonded Bluetooth devices for user to select from.  This is
     * needed only for channel connection initiated from the application.
     */
    public static class SelectDeviceDialogFragment extends DialogFragment {
        public static SelectDeviceDialogFragment newInstance(String[] names, int position) {
            SelectDeviceDialogFragment frag = new SelectDeviceDialogFragment();
            Bundle args = new Bundle();
            args.putStringArray("names", names);
            args.putInt("position", position);
            frag.setArguments(args);
            return frag;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String[] deviceNames = getArguments().getStringArray("names");
            int position = getArguments().getInt("position", -1);
            if (position == -1) position = 0;
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Select Device")
                    .setCancelable(false)
                    .setSingleChoiceItems(deviceNames, position,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((HomeActivity) getActivity()).connectToDevice(which);
                                    dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

    private static final BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context,"SMS sent",Toast.LENGTH_SHORT).show();
                    break;

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context,"Generic Failure",Toast.LENGTH_SHORT).show();
                    break;

                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context,"No Service",Toast.LENGTH_SHORT).show();
                    break;

                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context,"Null PDU",Toast.LENGTH_SHORT).show();
                    break;

                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context,"Radio Off",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //For SMS Delivered
    private static final BroadcastReceiver smsDeliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context,"SMS delivered",Toast.LENGTH_SHORT).show();
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(context,"SMS not delivered",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //sending SMS
    public void respond(){
        ArrayList<ContactData> receipient = new ArrayList<ContactData>();
        receipient = contactRepo.getContactList();
        Location pointLocation = retrieveLocationFromPrefrences();

        String message = "This is an emergency. My last known location:"
                + "http://maps.google.com/?q="
                + pointLocation.getLatitude() + ","
                + pointLocation.getLongitude();


        for (ContactData data :receipient){
            sendSMS(data.getPhoneNum(),message);
        }
    }

    private void sendSMS(String phoneNum, String message) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this,0,
                new Intent(SENT),0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this,0,
                new Intent(DELIVERED),0);

        //when the SMS has been sent
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));

        //When SMS has been delivered
        registerReceiver(smsDeliverReceiver, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum, null, message, sentPI, deliveredPI);
    }

    //Retrieve location using SharedPreferences
    private Location retrieveLocationFromPrefrences(){
        SharedPreferences prefs = this.getSharedPreferences(NearbyActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Location location = new Location("PONT_LOCATION");
        location.setLatitude(prefs.getFloat("latitude",0));
        location.setLongitude(prefs.getFloat("longitude", 0));
        return location;
    }

    //alarm notification
    private void fireAlarm() {
        Log.d(TAG, "Alarm on!");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Intent myIntent = new Intent(HomeActivity.this, AlarmIntentReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HomeActivity.this,0,myIntent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    //Connect Bluetooth
    public void connectToDevice(int position) {
        mDevices = mLeDevices.get(position);
        deviceIndex = position;
        initialize();
    }

    //Initialize Bind Service
    private void initialize(){
        Intent gattServiceIntent = new Intent(this,BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.e(TAG, "Bind Service");
    }


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            if (mDevices != null){
                mBluetoothLeService.connect(mDevices.getAddress());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            dataVal.setText(data);

            int hbData = Integer.parseInt(data);

            if (hbData > 10 && hbData < 120){
                mBluetoothLeService.disconnect();
                if (contactRepo.getContactsCount() > 0 && nearbyRepo.getNearbyCount() > 0){
                    respond();
                    fireAlarm();
                }
            }
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                if (UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                }
            }
        }
    }
}
