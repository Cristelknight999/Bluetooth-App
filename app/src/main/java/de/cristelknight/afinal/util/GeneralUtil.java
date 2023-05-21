package de.cristelknight.afinal.util;

import android.content.Context;
import android.widget.Toast;

public class GeneralUtil {


    public static void msg(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static int getScaledValue(int progress, int max, int min){
        return  (int) ((progress / 100.0) * (max - min) + min);
    }

}
