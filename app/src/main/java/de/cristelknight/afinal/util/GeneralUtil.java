package de.cristelknight.afinal.util;

import android.content.Context;
import android.widget.Toast;

import de.cristelknight.afinal.Control;

public class GeneralUtil {

    public static void msg(String s, Context context) { // Display a message
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static int getScaledValue(int progress, int min, int max){ // Scale the 0 - 100 progress to a min - max one
        return  (int) ((progress / 100.0) * (max - min) + min);
    }

    /**
     * @param delay defines the required difference between the last position, for sending a message to the arduino
     * @param min the scaled min value
     * @param max the scaled max value
     * @param delayed should the delay be used
     * @param type adds a prefix to the message, which tells the arduino what it should do with the message
     */
    public static int getProgress(int progress, int currentDelay, int delay, int min, int max, boolean delayed, String type, Control from){ // Handle the slide message sending
        if(!delayed || Math.abs(Math.subtractExact(currentDelay, progress)) >= delay){
            currentDelay = progress;
            from.sendSignalClosed(type + GeneralUtil.getScaledValue(progress, min, max));
        }
        return currentDelay;
    }

}
