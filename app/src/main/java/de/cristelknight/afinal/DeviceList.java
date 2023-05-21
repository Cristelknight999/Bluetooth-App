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
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    Chip btnPaired;
    ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    public static String EXTRA_ADDRESS = "device_address";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Toast.makeText(getApplicationContext(), "Android version to new!", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_main);
        btnPaired = findViewById(R.id.chip4);
        devicelist = findViewById(R.id.recyclerView);
        addListView();

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        } else if (!myBluetooth.isEnabled()) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

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
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }
    /*
    @SuppressLint("MissingPermission")
    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);
    }

     */


}