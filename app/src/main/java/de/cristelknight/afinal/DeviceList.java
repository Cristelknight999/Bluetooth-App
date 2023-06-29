package de.cristelknight.afinal;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.cristelknight.afinal.util.GeneralUtil;

public class DeviceList extends AppCompatActivity {

    // Declare UI elements
    Chip btnPaired;
    ListView devicelist;

    // Declare variables
    private BluetoothAdapter myBluetooth = null;
    public static String EXTRA_ADDRESS = "device_address";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the Android version is supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            msg("Android version to new!");
            finish();
        }

        // Set up the view and initialize UI elements
        setContentView(R.layout.activity_main);
        btnPaired = findViewById(R.id.chip4);
        devicelist = findViewById(R.id.recyclerView);
        addListView();

        // Check if Bluetooth is available on the device
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            msg("Bluetooth device not available");
            finish();
        } else if (!myBluetooth.isEnabled()) {
            // Request user to enable Bluetooth if it's not enabled
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        // Clear and add paired devices to the device list
        clearAndAddDevices();
        btnPaired.setOnClickListener(v -> clearAndAddDevices());
    }


    private ArrayAdapter<Pair<String, String>> mAdapter;

    private List<Pair<String, String>> mDeviceList = new ArrayList<>();

    private void addListView(){
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDeviceList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View rowView = convertView;
                if (rowView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    rowView = inflater.inflate(R.layout.list_layout, null);
                }
                Pair<String, String> peripheral = getItem(position);
                TextView nameTextView = rowView.findViewById(R.id.device_name);
                nameTextView.setText(peripheral.first);
                TextView addressTextView = rowView.findViewById(R.id.device_address);
                addressTextView.setText(peripheral.second);
                return rowView;
            }
        };
        devicelist.addHeaderView(getLayoutInflater().inflate(R.layout.header_layout, null));
        devicelist.setAdapter(mAdapter);
        devicelist.setOnItemClickListener(myListClickListener);
    }

    private final AdapterView.OnItemClickListener myListClickListener = (parent, view, position, id) -> {
        if(position == 0) return;
        String address = mDeviceList.get(position - 1).second;
        Intent i = new Intent(DeviceList.this, Control.class);
        i.putExtra(EXTRA_ADDRESS, address);
        startActivity(i);
    };

    @SuppressLint("MissingPermission")
    public void clearAndAddDevices(){
        mDeviceList.clear();
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice bt : pairedDevices) {
                mDeviceList.add(new Pair<>(bt.getName(), bt.getAddress()));
            }
        } else {
            msg("No Paired Bluetooth Devices Found.");
        }
    }

    public void msg(String s) {
        GeneralUtil.msg(s, this);
        //Log.d(TAG, s);
    }
}