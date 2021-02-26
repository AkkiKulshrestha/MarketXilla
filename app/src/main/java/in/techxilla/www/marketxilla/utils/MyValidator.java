package in.techxilla.www.marketxilla.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SIR.WilliamRamsay on 03-Dec-15.
 */
public class MyValidator {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
    private static final String REQUIRED_MSG = "Field required";
    Bitmap bitmap = null;

    public static boolean isValidPhone(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidEmail(final EditText editText) {
        final String email = editText.getText().toString().trim();
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            editText.setError("Enter valid Email");
            return false;
        } else if (email.length() == 0) {
            editText.setError("Enter valid Email");
            return false;
        }
        editText.setError(null);
        return true;
    }

    /**
     * @param edt_email_mobile as EditText
     */
    public static boolean isValidEmailMobile(final EditText edt_email_mobile) {
        boolean result = true;
        final String email_mob = edt_email_mobile.getText().toString().trim();
        if (TextUtils.isEmpty(email_mob)) {
            edt_email_mobile.setError("Enter valid Email Or Mobile No.");
        } else if (email_mob.length() == 10) {
            android.util.Patterns.PHONE.matcher(email_mob).matches();
        } else {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(email_mob);
            if (!matcher.matches()) {
                edt_email_mobile.setError("Enter valid Email Or Mobile No.");
                return false;
            } else if (email_mob.length() == 0) {
                edt_email_mobile.setError("Enter valid Email Or Mobile No.");
                return false;
            }
            edt_email_mobile.setError(null);
        }
        return result;

    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidPassword(final EditText editText) {
        String pass = editText.getText().toString().trim();
        if (pass != null && pass.length() < 5) {
            editText.setError(REQUIRED_MSG);
            return false;
        } else {
            //$specialCharacters = "-@%\\\\[\\\\}+'!/#$^?:;,\\\\(\\\"\\\\)~`.*=&\\\\{>\\\\]<_";
           /* Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@%!/#$^?*=&])(?=\\S+$).{6,20}$");

            Matcher matcher = pattern.matcher(pass);

            if (!matcher.matches()) {
                editText.setError("Please Enter Strong Password.");
                return false;
            }else{
                editText.setError(null);
                return  true;
            }*/
            editText.setError(null);
            return true;
        }
    }

    /**
     * @param imageStr as Image String
     */
    public static boolean isValidImageString(final String imageStr) {
        return imageStr != null && (imageStr.length() != 0 || !imageStr.equalsIgnoreCase(""));
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidField(final EditText editText) {
        final String txtValue = editText.getText().toString().trim();
        if (txtValue.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return true;
        }
        editText.setError(null);
        return false;
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidName(final EditText editText) {
        final String txtValue = editText.getText().toString().trim();
        if (txtValue.length() <= 3) {
            editText.setError(REQUIRED_MSG);
            return false;
        }
        editText.setError(null);
        return true;
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidAge(final EditText editText) {
        Integer retValue = 0;
        final String txtValue = editText.getText().toString().trim();
        if (txtValue.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        if (txtValue.length() > 0 && txtValue.length() > 2) {
            editText.setError("Age cannot be greater than 100 years");
            return false;
        }

        if (txtValue.length() > 0 && Integer.parseInt(txtValue) == 0) {
            editText.setError("Age cannot be 0 year");
            return false;
        }
        editText.setError(null);
        return true;
    }

    /**
     * @param spinner as Spinner
     */
    public static boolean isValidSpinner(final Spinner spinner) {
        final View view = spinner.getSelectedView();
        final TextView textView = (TextView) view;
        if (spinner.getSelectedItemPosition() == 0) {
            textView.setError("None selected");
            return false;
        }
        textView.setError(null);
        return true;
    }

    /**
     * @param spinner as SearchableSpinner
     */
    public static boolean isValidSearchableSpinner(final SearchableSpinner spinner) {
        final View view = spinner.getSelectedView();
        return spinner.getSelectedItemPosition() != 0;
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidMobile(final EditText editText) {
        final String mob = editText.getText().toString().trim();
        if (mob != null && mob.length() != 10) {
            editText.setError(REQUIRED_MSG + " Enter 10 digits");
            return false;
        } else {
            Pattern pattern = Pattern.compile("(([6-9]{1})([0-9]{9}))");
            Matcher matcher = pattern.matcher(mob);
            if (!matcher.matches()) {
                editText.setError("Invalid Mobile No. Enter Valid Mobile Number");
                return false;
            } else {
                editText.setError(null);
                return true;
            }
        }
    }

    /**
     * @param context as Context
     * @param mob     as String
     */
    public static boolean isValidMobileString(final Context context, final String mob) {
        if (mob != null && mob.length() != 10) {
            CommonMethods.DisplayToastWarning(context, "Invalid Mobile No. Enter Valid Mobile Number");
            return false;
        } else {
            Pattern pattern = Pattern.compile("(([6-9]{1})([0-9]{9}))");
            Matcher matcher = pattern.matcher(mob);
            if (!matcher.matches()) {
                CommonMethods.DisplayToastWarning(context, "Invalid Mobile No. Enter Valid Mobile Number");
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidPan(final EditText editText) {
        final String pan = editText.getText().toString().trim();
        if (pan != null && pan.length() != 10) {
            editText.setError("Enter Valid PAN Number");
            return false;
        } else if (pan != null && pan.length() > 1) {
            Pattern pattern = Pattern.compile("(([A-Z]{5})([0-9]{4})([a-zA-Z]))");
            Matcher matcher = pattern.matcher(pan);
            if (!matcher.matches()) {
                editText.setError("Invalid PAN. Enter Valid PAN Number");
                return false;
            } else {
                //([CPHFATBLJG])
                String char_4 = pan.substring(3, 4);
                Log.d("Char 4", char_4);
                Pattern pat = Pattern.compile("[CPHFATBLJG]");
                Matcher mat = pat.matcher(char_4);
                if (!mat.matches()) {
                    editText.setError("Invalid PAN. Enter Valid PAN Number");
                    return false;
                } else {
                    editText.setError(null);
                    return true;
                }
            }
        } else {
            editText.setError(null);
            return true;
        }
    }

    /**
     * @param editText as EditText
     */
    public static boolean isValidAadhaar(final EditText editText) {
        final String adhaar = editText.getText().toString().trim();
        if (adhaar != null && adhaar.length() == 12) {
            editText.setError(null);
            return true;
        }
        editText.setError(REQUIRED_MSG + " Enter 12 digits");
        return false;
    }

    /**
     * @param view as ImageView
     */
    public static boolean isValidImage(@NonNull ImageView view) {
        final Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }
        return hasImage;
    }

    /**
     * @param StartDT        as String
     * @param EndDT          as String
     * @param edt_SIPEndDate as EditText
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean CheckDates(final String StartDT, final String EndDT, final EditText edt_SIPEndDate) {
        final SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        boolean b = false;
        try {
            if (dfDate.parse(StartDT).before(dfDate.parse(EndDT))) {
                b = true;//If start date is before end date
                edt_SIPEndDate.setError("End Date Should Be Greater Than Start Date");
            } else if (dfDate.parse(StartDT).equals(dfDate.parse(EndDT))) {
                b = false;//If two dates are equal
                edt_SIPEndDate.setError("End Date Should Be Greater Than Start Date");
            } else {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }
}