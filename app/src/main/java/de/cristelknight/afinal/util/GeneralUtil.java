package de.cristelknight.afinal.util;

import android.content.Context;
import android.widget.Toast;

import de.cristelknight.afinal.Control;

public class GeneralUtil {


    public static void msg(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static int getScaledValue(int progress, int min, int max){
        return  (int) ((progress / 100.0) * (max - min) + min);
    }

    public static int getProgress(int progress, int currentDelay, int delay, int min, int max, boolean delayed, String type, Control from){
        if(!delayed || Math.abs(Math.subtractExact(currentDelay, progress)) >= delay){
            currentDelay = progress;
            from.sendSignalClosed(type + GeneralUtil.getScaledValue(progress, min, max));
        }
        return currentDelay;
    }

}
