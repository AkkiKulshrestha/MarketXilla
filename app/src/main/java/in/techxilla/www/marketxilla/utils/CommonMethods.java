package in.techxilla.www.marketxilla.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;
import in.techxilla.www.marketxilla.fragment.ProgressDialogFragment;

public class CommonMethods {

    public static final String OTP_DELIMITER = ":";
    public static final String SMS_ORIGIN = "abkms";
    public static final String APP_NAME = "ABKMS";
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMERIC_STRING = "0123456789";
    public static String EXCEL_FILE_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + "/" + APP_NAME + "/";

    @ColorInt
    public static int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");

    @ColorInt
    public static int ERROR_COLOR = Color.parseColor("#D50000");

    @ColorInt
    public static int INFO_COLOR = Color.parseColor("#3F51B5");

    @ColorInt
    public static int SUCCESS_COLOR = Color.parseColor("#388E3C");

    @ColorInt
    public static int WARNING_COLOR = Color.parseColor("#FFA900");

    @ColorInt
    public static int NORMAL_COLOR = Color.parseColor("#353A3E");

    public static void DisplayToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void DisplayToastWarning(Context context, String message) {
        Toasty.warning(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void DisplayToastSuccess(Context context, String message) {
        Toasty.success(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void DisplayToastError(Context context, String message) {
        Toasty.error(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void DisplayToastNormal(Context context, String message) {
        Toasty.normal(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void DisplayToastInfo(Context context, String message) {
        Toasty.info(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void DisplaySnackBar(View view, String message, String type) {
        if (type != null && !type.equalsIgnoreCase("")) {
            if (type.equalsIgnoreCase("INFO")) {
                Snacky.builder()
                        .setView(view)
                        .setText(message)
                        .centerText().setTextColor(Color.WHITE)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .info()
                        .show();

            } else if (type.equalsIgnoreCase("SUCCESS")) {
                Snacky.builder()
                        .setView(view)
                        .setText(message)
                        .centerText().setTextColor(Color.WHITE)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .success()
                        .show();

            } else if (type.equalsIgnoreCase("WARNING")) {
                Snacky.builder()
                        .setView(view)
                        .setText(message)
                        .centerText().setTextColor(Color.WHITE)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .warning()
                        .show();

            } else if (type.equalsIgnoreCase("ERROR")) {
                Snacky.builder()
                        .setView(view)
                        .setText(message)
                        .centerText().setTextColor(Color.WHITE)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error()
                        .show();
            } else {
                Snacky.builder()
                        .setView(view)
                        .setText(message)
                        .centerText().setTextColor(Color.WHITE)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .build()
                        .show();
            }
        } else {
            Snacky.builder()
                    .setView(view)
                    .setText(message)
                    .centerText().setTextColor(Color.WHITE)
                    .setDuration(Snacky.LENGTH_SHORT)
                    .build()
                    .show();
        }
    }

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(s.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (byte b : a) {
                sb.append(Character.forDigit((b & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(b & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAge(int DOByear, int DOBmonth, int DOBday) {
        int age;
        final Calendar calenderToday = Calendar.getInstance();
        final int currentYear = calenderToday.get(Calendar.YEAR);
        final int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        final int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
        age = currentYear - DOByear;
        return age;
    }

    public static void hiddenKeyboard(final View v, final Context con) {
        InputMethodManager keyboard = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String randomNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String GetSystemTime() {
        String SystemTime = "";
        Calendar cal = Calendar.getInstance();
        final int millisecond = cal.get(Calendar.MILLISECOND);
        final int second = cal.get(Calendar.SECOND);
        final int minute = cal.get(Calendar.MINUTE);
        //12 hour format
        final int hour = cal.get(Calendar.HOUR);
        //24 hour format
        final int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        SystemTime = hour + ":" + minute;
        return SystemTime;
    }

    public static String ReplaceNewLineCodeFromString(String OriginalString) {
        String NewString = "";
        if (OriginalString != null && !OriginalString.equalsIgnoreCase("")) {
            if (OriginalString.contains("\n")) {

                NewString = OriginalString.replace("\n", "");
                OriginalString = NewString;
                if (OriginalString.contains("&amp;")) {

                    NewString = OriginalString.replace("&amp;", " & ");
                }
            } else if (OriginalString.contains("&amp;")) {

                NewString = OriginalString.replace("&amp;", " & ");
            } else {
                NewString = OriginalString;
            }
        }
        return NewString;
    }

    public static String UrlFormatString(String OriginalString) {
        String NewString = "";
        if (OriginalString != null && !OriginalString.equalsIgnoreCase("")) {
            if (OriginalString.contains(" ")) {

                NewString = OriginalString.replace(" ", "+");

            } else {
                NewString = OriginalString;
            }
        }
        return NewString;
    }

    public static int GetSystemHours() {
        int Systemhours = 0;
        Calendar cal = Calendar.getInstance();

        final int millisecond = cal.get(Calendar.MILLISECOND);
        final int second = cal.get(Calendar.SECOND);
        final int minute = cal.get(Calendar.MINUTE);
        //12 hour format
        final int hour = cal.get(Calendar.HOUR);
        //24 hour format
        final int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        Systemhours = hourofday;
        return Systemhours;
    }

    public static int GetSystemMinutes() {
        int Systemminutes = 0;
        Calendar cal = Calendar.getInstance();

        final int millisecond = cal.get(Calendar.MILLISECOND);
        final int second = cal.get(Calendar.SECOND);
        final int minute = cal.get(Calendar.MINUTE);
        //12 hour format
        final int hour = cal.get(Calendar.HOUR);
        //24 hour format
        final int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        Systemminutes = minute;
        return Systemminutes;
    }

    @SuppressLint("DefaultLocale")
    public static String afterTextChanged(Editable view) {
        String s = null;
        try {
            // The comma in the format specifier does the trick
            s = String.format("%,d", Long.parseLong(view.toString()));
        } catch (NumberFormatException ignored) {
        }
        return s;
    }

    public static void LogD(String Tag, String msg) {
        Log.d(Tag, msg);
    }

    public static boolean InsertLogs(Context context, String rdate, String UserId, String FileName, String Message) {
        boolean result = false;
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("marketxilla.db", Context.MODE_PRIVATE, null);
            String query = "INSERT INTO TBL_LOG(LogRegisteredDate,UserId,FileName,Message) VALUES('" + rdate + "', '" + UserId + "','" + FileName + "','" + Message + "');";
            database.execSQL(query);
            database.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String NumberDisplayFormatting(String NumberToDisplay) {
        String FormattedDisplayString = "";
        if (NumberToDisplay != null && !NumberToDisplay.equalsIgnoreCase("")) {
            double number_dis = Double.parseDouble(NumberToDisplay);
            if (number_dis < 10000000 && number_dis > 10000) {
                Double new_num = number_dis / 100000;
                FormattedDisplayString = String.format("%.2f", new_num) + " Lacs";
            } else if (number_dis > 10000000) {
                Double new_num = number_dis / 10000000;
                FormattedDisplayString = String.format("%.2f", new_num) + " Crs";
            } else if (number_dis > 0 && number_dis <= 10000) {
                try {
                    String originalString = NumberToDisplay;
                    long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,##,##,##,###");
                    FormattedDisplayString = formatter.format(longval);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            } else if (NumberToDisplay.equalsIgnoreCase("") || number_dis == 0) {
                FormattedDisplayString = "0 Lacs";
            }
        }
        return FormattedDisplayString;
    }

    @SuppressLint("DefaultLocale")
    public static String NumberDisplayFormattingForCompare(String NumberToDisplay) {
        String FormattedDisplayString = "";
        if (NumberToDisplay != null && !NumberToDisplay.equalsIgnoreCase("")) {
            double number_dis = Double.parseDouble(NumberToDisplay);
            if (number_dis >= 10000000) {
                Double new_num = number_dis / 10000000;
                FormattedDisplayString = String.format("%.2f", new_num) + " Crs";
            } else if (number_dis < 10000000 && number_dis >= 100000) {
                Double new_num = number_dis / 100000;
                FormattedDisplayString = String.format("%.2f", new_num) + " L";
            } else if (number_dis >= 1000 && number_dis < 100000) {
                Double new_num = number_dis / 1000;
                FormattedDisplayString = String.format("%.2f", new_num) + " K";

            } else if (number_dis > 0 && number_dis < 1000) {
                try {
                    String originalString = NumberToDisplay;
                    long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,##,##,##,###");
                    FormattedDisplayString = formatter.format(longval);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            } else if (NumberToDisplay.equalsIgnoreCase("") || number_dis <= 0) {
                FormattedDisplayString = "0";
            }
        }
        return FormattedDisplayString;
    }

    @SuppressLint("DefaultLocale")
    public static String NumberDisplayFormattingForCompareWithNegativeValues(String NumberToDisplay) {
        String FormattedDisplayString = "";
        if (NumberToDisplay != null && !NumberToDisplay.equalsIgnoreCase("")) {
            NumberToDisplay = NumberToDisplay.replace(",", "");
            double number_dis = Double.parseDouble(NumberToDisplay);
            if (number_dis >= 10000000) {
                Double new_num = number_dis / 10000000;
                FormattedDisplayString = String.format("%.2f", new_num) + " Crs";
            } else if (number_dis < 10000000 && number_dis >= 100000) {
                Double new_num = number_dis / 100000;
                FormattedDisplayString = String.format("%.2f", new_num) + " L";
            } else if (number_dis >= 1000 && number_dis < 100000) {
                Double new_num = number_dis / 1000;
                FormattedDisplayString = String.format("%.2f", new_num) + " K";

            } else if (number_dis > 0 && number_dis < 1000) {
                try {
                    String originalString = NumberToDisplay;
                    long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,##,##,##,###");
                    FormattedDisplayString = formatter.format(longval);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            } else if (NumberToDisplay.equalsIgnoreCase("") || number_dis == 0) {
                FormattedDisplayString = "0";
            } else if (number_dis < 0) {
                double abs_value = Math.abs(number_dis);
                String NewStringFormatted = NumberDisplayFormattingForCompare(String.valueOf(abs_value));
                FormattedDisplayString = "-" + NewStringFormatted;
            }
        }
        return FormattedDisplayString;
    }

    public static String NumberDisplayFormattingWithComma(String NumberToDisplay) {
        String FormattedDisplayString = "";
        String DecimalTrimmedValue = null;
        if (NumberToDisplay != null && !NumberToDisplay.equalsIgnoreCase("")) {
            try {
                String originalString = NumberToDisplay;
                long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,##,##,##,###");
                FormattedDisplayString = formatter.format(longval);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (NumberToDisplay.equalsIgnoreCase("")) {
            FormattedDisplayString = "0";
        }
        return FormattedDisplayString;
    }

    public static String DecimalNumberDisplayFormattingWithComma(String NumberToDisplay) {
        String FormattedDisplayString = "";
        String DecimalTrimmedValue = "";
        if (NumberToDisplay != null && !NumberToDisplay.equalsIgnoreCase("")) {
            String originalString = NumberToDisplay;
            try {
                double longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Double.parseDouble(originalString);
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,##,##,##,###.00");
                FormattedDisplayString = formatter.format(longval);

                if (FormattedDisplayString.equalsIgnoreCase(".00")) {
                    FormattedDisplayString = "0.00";
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                FormattedDisplayString = originalString;
            }
        } else if (NumberToDisplay.equalsIgnoreCase("")) {
            FormattedDisplayString = "0";
        }

        return FormattedDisplayString;
    }

    public static String JotzDisplayDateFormat(String YMDFormatDate) {
        String FormattedString = "";
        final String yrs, mnts, days;
        final int new_year, new_month, new_day;

        if (!YMDFormatDate.equalsIgnoreCase("")) {
            String[] parts = YMDFormatDate.split("-");
            yrs = parts[0];
            mnts = parts[1];
            days = parts[2];
            new_year = Integer.parseInt(yrs);
            new_month = Integer.parseInt(mnts);
            new_day = Integer.parseInt(days);

            String Month_initials = GetMonthInitials(new_month);
            FormattedString = Month_initials.toUpperCase() + " " + yrs;
        }
        return FormattedString;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDate(Context context, long milliSeconds) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((int) milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String SavePreference(Context context, String key, String value) {
        final SharedPreferences preferences = context.getSharedPreferences("UserDetails", 0);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        return key;
    }

    public static String LoadPreference(Context context, String key) {
        final SharedPreferences preferences = context.getSharedPreferences("UserDetails", 0);
        return preferences.getString(key, "");
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false;
            }
        }
        return ret;
    }

    public static boolean isSDPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void setTextViewFont(Context context, TextView textView) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "font/roboto_light.ttf");
            textView.setTypeface(tf);
        } catch (Exception ignored) {
        }
    }

    public static void setButtonFont(Context context, Button button) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "font/roboto_bold.ttf");
            button.setTypeface(tf);
        } catch (Exception ignored) {
        }
    }

    public static int CalculateMonth(String StartDate, String EndDate) {
        int total_months = 0;
        final int sd_dd, sd_mm, sd_yy, ed_dd, ed_mm, ed_yy;
        String[] parts = StartDate.split("-");
        String yrsSD_Lumpsum = parts[0];
        String mntsSD_Lumpsum = parts[1];
        String daysSD_Lumpsum = parts[2];
        sd_yy = Integer.parseInt(yrsSD_Lumpsum);
        sd_mm = Integer.parseInt(mntsSD_Lumpsum) - 1;
        sd_dd = Integer.parseInt(daysSD_Lumpsum);

        String[] partsss = EndDate.split("-");
        String yrsED_Lumpsum = partsss[0];
        String mntsED_Lumpsum = partsss[1];
        String daysED_Lumpsum = partsss[2];
        ed_yy = Integer.parseInt(yrsED_Lumpsum);
        ed_mm = Integer.parseInt(mntsED_Lumpsum) - 1;
        ed_dd = Integer.parseInt(daysED_Lumpsum);

        total_months = ((ed_yy - sd_yy) * 12 + (ed_mm - sd_mm)) + 1;
        return total_months;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DateDisplayedFormat(String DateIn_YMD) {
        String finalDate = null;
        try {
            finalDate = new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(DateIn_YMD));
        } catch (ParseException e) {
            Log.d("Exception", e.toString());
        }

        return finalDate;
    }

    public static String GetMonthInitials(int MonthCode) {
        if (MonthCode == 1) {
            return "Jan";
        } else if (MonthCode == 2) {
            return "Feb";
        } else if (MonthCode == 3) {
            return "Mar";
        } else if (MonthCode == 4) {
            return "Apr";
        } else if (MonthCode == 5) {
            return "May";
        } else if (MonthCode == 6) {
            return "Jun";
        } else if (MonthCode == 7) {
            return "Jul";
        } else if (MonthCode == 8) {
            return "Aug";
        } else if (MonthCode == 9) {
            return "Sep";
        } else if (MonthCode == 10) {
            return "Oct";
        } else if (MonthCode == 11) {
            return "Nov";
        } else if (MonthCode == 12) {
            return "Dec";
        } else {
            return "month";
        }
    }

    public static String GetMonth(int MonthCode) {
        if (MonthCode == 1) {
            return "January";
        } else if (MonthCode == 2) {
            return "February";
        } else if (MonthCode == 3) {
            return "March";
        } else if (MonthCode == 4) {
            return "April";
        } else if (MonthCode == 5) {
            return "May";
        } else if (MonthCode == 6) {
            return "June";
        } else if (MonthCode == 7) {
            return "July";
        } else if (MonthCode == 8) {
            return "August";
        } else if (MonthCode == 9) {
            return "September";
        } else if (MonthCode == 10) {
            return "October";
        } else if (MonthCode == 11) {
            return "November";
        } else if (MonthCode == 12) {
            return "December";
        } else {
            return "month";
        }
    }

    public static Integer GetMonthCode(String Month) {
        int month_code = 0;
        if (Month.equalsIgnoreCase("January")) {
            month_code = 1;
        } else if (Month.equalsIgnoreCase("February")) {
            month_code = 2;
        } else if (Month.equalsIgnoreCase("March")) {
            month_code = 3;
        } else if (Month.equalsIgnoreCase("April")) {
            month_code = 4;
        } else if (Month.equalsIgnoreCase("May")) {
            month_code = 5;
        } else if (Month.equalsIgnoreCase("June")) {
            month_code = 6;
        } else if (Month.equalsIgnoreCase("July")) {
            month_code = 7;
        } else if (Month.equalsIgnoreCase("August")) {
            month_code = 8;
        } else if (Month.equalsIgnoreCase("September")) {
            month_code = 9;
        } else if (Month.equalsIgnoreCase("October")) {
            month_code = 10;
        } else if (Month.equalsIgnoreCase("November")) {
            month_code = 11;
        } else if (Month.equalsIgnoreCase("December")) {
            month_code = 12;
        }
        return month_code;
    }

    public static String ucFirst(String name) {
        String captilizedString = "";
        if (!name.trim().equals("")) {
            captilizedString = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return captilizedString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DisplayCurrentDate() {
        String CurrentDate = null;
        final SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        CurrentDate = formDate.format(new Date(System.currentTimeMillis()));
        return CurrentDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DisplayCurrentDate_inYMD() {
        String CurrentDate = null;
        final SimpleDateFormat formDate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = formDate.format(new Date(System.currentTimeMillis()));
        return CurrentDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isDateExpired(final String DMY_Date) {
        boolean result = true;
        if (DMY_Date != null && !DMY_Date.equalsIgnoreCase("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(DMY_Date);
                if (System.currentTimeMillis() <= strDate.getTime()) {
                    result = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return !result;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetOneDayLessDate(String YMD_Date) {
        String OneDayLessDate = null;
        String[] dateParts;
        SimpleDateFormat formDate = new SimpleDateFormat("yyyy-MM-dd");
        if (YMD_Date != null && !YMD_Date.equals("")) {
            dateParts = YMD_Date.split("-");
            String day = dateParts[2];
            String month = dateParts[1];
            String year = dateParts[0];
            if (!day.equalsIgnoreCase("01") || !day.equalsIgnoreCase("1")) {
                int OneDayless = Integer.parseInt(day) - 1;
                OneDayLessDate = dateParts[0] + "-" + dateParts[1] + "-" + String.valueOf(OneDayless);
            } else {
                if (!month.equalsIgnoreCase("01") || !month.equalsIgnoreCase("1")) {
                    int OneDayless = 30;
                    int OneMonthless = Integer.parseInt(month) - 1;
                    OneDayLessDate = dateParts[0] + "-" + String.valueOf(OneMonthless) + "-" + String.valueOf(OneDayless);
                } else {
                    int OneDayless = 30;
                    int OneMonthless = Integer.parseInt(month) - 1;
                    int OneYearless = Integer.parseInt(year);
                    OneDayLessDate = String.valueOf(OneYearless) + "-" + String.valueOf(OneMonthless) + "-" + String.valueOf(OneDayless);
                }
            }
        }
        return OneDayLessDate;
    }

    public static int GetCurrentMonth() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public static int GetCurrentYear() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public static String SanitizeString(String input_string) {
        String outputString = input_string;
        outputString = input_string.replaceAll("[^a-zA-Z0-9]", " ");
        return outputString;
    }

    public static String Base64_Encode(String input_string) {
        String encoded_string = "";
        byte[] data = null;
        data = input_string.getBytes(StandardCharsets.UTF_8);
        encoded_string = Base64.encodeToString(data, Base64.DEFAULT);
        return encoded_string;
    }

    public static String Base64_Decode(String base64) {
        String decoded_string = "";
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        decoded_string = new String(data, StandardCharsets.UTF_8);
        return decoded_string;
    }

    public static void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void showProgressDialog(FragmentManager fragmentManager) {
        ProgressDialogFragment.newInstance().show(fragmentManager, "");
    }

    public void TruncateTableLogs(Context context) {
        final SQLiteDatabase database = context.openOrCreateDatabase("marketxilla.db", Context.MODE_PRIVATE, null);
        final String query = "DELETE FROM  TBL_LOG;";
        database.execSQL(query);
        database.close();
    }
}