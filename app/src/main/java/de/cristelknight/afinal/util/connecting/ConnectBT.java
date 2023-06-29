package de.cristelknight.afinal.util.connecting;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.cristelknight.afinal.Control;

public class ConnectBT {
    private final Control control;  // Reference to the Control activity
    private final ExecutorService executor;  // Executor service for executing the Bluetooth connection task
    private Future<Void> connectTask;  // Represents the asynchronous Bluetooth connection task
    private final Handler handler;  // Handler for posting delayed runnables
    private ProgressDialog dialog;  // Progress dialog for displaying connection progress
    private final Runnable timeoutRunnable;  // Runnable executed if the connection task times out

    public ConnectBT(Control control) {
        this.control = control;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler();

        // Runnable for handling connection timeout (Just for safety)
        timeoutRunnable = () -> {
            if(!connectTask.isDone()){
                cancel();
            }
        };
    }

    public void execute() {
        control.setRequestedOrientation(control.getResources().getConfiguration().orientation); // Prevent the view rotation. Needed, because ProgressDialog.show() is deprecated and crashes the app on rotation
        Control.isTryingToConnect = true; // Set flag to indicate connection attempt
        control.runOnUiThread(() -> dialog = ProgressDialog.show(control, "Connecting...", "Please wait!", true)); // Display progress dialog
        handler.postDelayed(timeoutRunnable, (ConnectBTCallable.MAX_ATTEMPTS + 1) * ConnectBTCallable.RETRY_INTERVAL); // Schedule timeout runnable
        ConnectBTCallable connectCallable = new ConnectBTCallable(control, connectTask, dialog); // Create ConnectBTCallable object
        connectTask = executor.submit(connectCallable); // Submit the ConnectBTCallable task for execution
    }


    public void cancel() { // Return to the DeviceList
        ConnectBTCallable.log("Couldn't connect 2");
        if (connectTask != null) {
            connectTask.cancel(true);
        }
        if (dialog != null) {
            dialog.cancel();
        }
        Control.isTryingToConnect = false;
        control.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // Restore the screen orientation
        control.finish();
    }

}
