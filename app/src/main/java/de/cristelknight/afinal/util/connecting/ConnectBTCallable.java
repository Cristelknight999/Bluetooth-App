package de.cristelknight.afinal.util.connecting;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.ActivityInfo;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import de.cristelknight.afinal.Control;

public class ConnectBTCallable implements Callable<Void> {
    public static final int MAX_ATTEMPTS = 5;  // Maximum number of connection attempts
    public static final long RETRY_INTERVAL = 2000;  // Interval between connection attempts in milliseconds
    private final Control control;
    private final Future<Void> connectTask;
    private final ProgressDialog dialog;

    public ConnectBTCallable(Control control, Future<Void> connectTask, ProgressDialog dialog) {
        this.control = control;
        this.connectTask = connectTask;
        this.dialog = dialog;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Void call() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            if (Thread.currentThread().isInterrupted()){
                log("Interrupted");
                cancel();
                return null;
            }

            try {
                if (Control.btSocket == null || !Control.isBtConnected) { // Check if bluetooth is not connected yet
                    //Handle connection
                    control.myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = control.myBluetooth.getRemoteDevice(control.address);
                    Control.btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(Control.serialUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Control.btSocket.connect();

                    Control.isBtConnected = true;
                    dismiss(true); // Dismiss the progress dialog and indicate successful connection
                    return null;
                }


            } catch (IOException ignored) {}
            if (attempt < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }

        // All connection attempts failed

        cancel(); // Cancel the connection attempt
        return null;
    }

    public void cancel() {
        log("canceled");
        dismiss(false);
        control.finish(); // Finish the control activity and return to the DeviceList
    }

    public void dismiss(boolean bl) { //true = successful, false = unsuccessful
        if (connectTask != null) {
            connectTask.cancel(true); // Cancel the connectTask
        }
        if (dialog != null) {
            dialog.cancel(); // stop showing the progress dialog
        }
        Control.isTryingToConnect = false;
        control.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // Restore the screen orientation
        if(bl) log("Connected!");
        else log("Couldn't connect 1");
    }

    public static void log(String s) {
        Log.d(TAG, s);
    }

}

