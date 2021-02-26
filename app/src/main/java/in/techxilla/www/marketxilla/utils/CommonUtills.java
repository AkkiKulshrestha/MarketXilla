package in.techxilla.www.marketxilla.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class CommonUtills {
    public static float getScreenWidth(Context context) {
        float width = (float) 360.0;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels / displayMetrics.density;
        return width;
    }
}
