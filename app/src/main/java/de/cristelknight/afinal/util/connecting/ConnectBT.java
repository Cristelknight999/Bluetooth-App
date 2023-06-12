package de.cristelknight.afinal.util.connecting;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.cristelknight.afinal.Control;

public class ConnectBT {
    private final Control control;
    private final ExecutorService executor;
    private Future<Void> connectTask;
    private final Handler handler;

    private ProgressDialog dialog;

    private final Runnable timeoutRunnable;

    public ConnectBT(Control control) {
        this.control = control;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler();

        //Just for safety
        timeoutRunnable = () -> {
            if(!connectTask.isDone()){
                cancel();
            }
        };
    }

    public void execute() {
        control.setRequestedOrientation(control.getResources().getConfiguration().orientation);
        Control.isTryingToConnect = true;
        control.runOnUiThread(() -> dialog = ProgressDialog.show(control, "Connecting...", "Please wait!", true));
        handler.postDelayed(timeoutRunnable, (ConnectBTCallable.MAX_ATTEMPTS + 1) * ConnectBTCallable.RETRY_INTERVAL);
        ConnectBTCallable connectCallable = new ConnectBTCallable(control, connectTask, dialog);
        connectTask = executor.submit(connectCallable);
    }


    public void cancel() {
        ConnectBTCallable.log("Couldn't connect 2");
        if (connectTask != null) {
            connectTask.cancel(true);
        }
        if (dialog != null) {
            dialog.cancel();
        }
        Control.isTryingToConnect = false;
        control.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        control.finish();
    }

}
