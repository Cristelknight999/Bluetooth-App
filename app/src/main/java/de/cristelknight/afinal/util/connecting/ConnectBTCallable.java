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

    private Control control;
    private Future<Void> connectTask;
    private ProgressDialog dialog;

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
                msg("Interrupted");
                cancel();
                return null;
            }


            try {
                if (Control.btSocket == null || !Control.isBtConnected) {
                    control.myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = control.myBluetooth.getRemoteDevice(control.address);
                    Control.btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(Control.serialUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Control.btSocket.connect();

                    Control.isBtConnected = true;
                    dismiss(true);
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

        cancel();
        return null;
    }

    public void cancel() {
        msg("canceled");
        dismiss(false);
        control.finish();
    }

    public void dismiss(boolean bl) {
        if (connectTask != null) {
            connectTask.cancel(true);
        }
        if (dialog != null) {
            dialog.cancel();
        }
        Control.isTryingToConnect = false;
        if(bl)msg("Connected!");
        else msg("Couldn't connect 1");
    }

    private void msg(String s) {
        Log.d(TAG, s);
    }

}

