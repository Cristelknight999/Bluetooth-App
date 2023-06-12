package de.cristelknight.afinal;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

import de.cristelknight.afinal.util.GeneralUtil;
import de.cristelknight.afinal.util.connecting.ConnectBT;

public class Control extends AppCompatActivity {

    Button btnDis, btn1, btn2, btn3, btn4, btn6, btn7, btn8, btn9;
    SeekBar seekBar, seekBar2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1;
    private int seekBarDelay = 0;
    private int seekBarDelay2 = 0;

    public String address = null;
    public BluetoothAdapter myBluetooth = null;
    public static BluetoothSocket btSocket = null;
    public static boolean isBtConnected = false;
    public static boolean isTryingToConnect = false;

    public static final UUID serialUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS);

        if(!isBtConnected && !isTryingToConnect){
            new ConnectBT(this).execute();
        }

        //Set-up View
        setContentView(R.layout.activity_second);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        btn9 = findViewById(R.id.button9);

        btnDis = findViewById(R.id.buttonD);
        seekBar = findViewById(R.id.seekBar);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar2.setProgress(50);
        switch1 = findViewById(R.id.switch1);

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendSignalClosed("w" + 1);
            } else {
                sendSignalClosed("w" + 0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarDelay = getProgressSpeed(progress, seekBarDelay, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarDelay = getProgressSpeed(seekBar.getProgress(), seekBarDelay, false);
            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    seekBarDelay2 = getProgressRotation(progress, seekBarDelay2, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(Math.abs(Math.subtractExact(50, progress)) <= 20){
                    seekBar.setProgress(50);
                }
                seekBarDelay2 = getProgressRotation(seekBar.getProgress(), seekBarDelay2, false);
            }
        });

        setOnTouchListener(btn1, 1);
        setOnTouchListener(btn2, 2);
        setOnTouchListener(btn3, 3);
        setOnTouchListener(btn4, 4);
        setOnTouchListener(btn6, 6);
        setOnTouchListener(btn7, 7);
        setOnTouchListener(btn8, 8);
        setOnTouchListener(btn9, 9);


        btnDis.setOnClickListener(v -> disconnect());
    }




    public int getProgressSpeed(int progress, int currentDelay, boolean delayed){
        return GeneralUtil.getProgress(progress, currentDelay, 20, 80, 255, delayed, "s", this);
    }


    public int getProgressRotation(int progress, int currentDelay, boolean delayed){
        return GeneralUtil.getProgress(progress, currentDelay, 15, -100, 100, delayed,"r", this);
    }



    private void setOnTouchListener(Button btn1, int message) {
        setOnTouchListener(btn1, "m" + message, "m" + 5);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener(Button button, String message, String release){
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Button pressed
                sendSignalClosed(message);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // Button released
                sendSignalClosed(release);
                return true;
            }
            return false;
        });
    }


    public void sendSignalClosed(String string) {
        sendSignal(string + "z");
    }

    private void sendSignal(String string) {
        if(btSocket != null) {
            try {
                btSocket.getOutputStream().write(string.getBytes());
            } catch (IOException e) {
                msg("Couldn't write to Socket.");
            }
        }
        else {
            msg("Socket is null.");
        }
    }

    private void disconnect() {
        isBtConnected = false;
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Couldn't close Socket.");
            }
        }
        finish();
    }

    public void msg(String s) {
        GeneralUtil.msg(s, this);
        //Log.d(TAG, s);
    }

}