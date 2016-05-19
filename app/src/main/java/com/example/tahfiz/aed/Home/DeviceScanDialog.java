package com.example.tahfiz.aed.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.tahfiz.aed.R;

/**
 * Created by tahfiz on 8/5/2016.
 */
public class DeviceScanDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private Button btnScan;
    private Button btnStop;
    private ListView lvDevices;
    private boolean mScanning;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    ConnectionListener actvConnect;

    public interface ConnectionListener{
        public void communicate(BluetoothDevice device);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            actvConnect = (ConnectionListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bt_list, null);

        mHandler = new Handler();

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(getActivity());

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        lvDevices = (ListView) view.findViewById(android.R.id.list);

        lvDevices.setAdapter(mLeDeviceListAdapter);
        lvDevices.setOnItemClickListener(this);
        scanLeDevice(true);

        final AlertDialog popDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("List of Available Device")
                .setPositiveButton("SCAN",null)
                .setNegativeButton("STOP",null)
                .create();

        popDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnScan = popDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnScan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                    }
                });

                Button btnStop = popDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btnStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scanLeDevice(false);
                    }
                });
            }

        });

        popDialog.create();

        return popDialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;

        /*final Intent intent = new Intent(getActivity(), DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());*/
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        dismiss();
        actvConnect.communicate(device);
        //startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
}
