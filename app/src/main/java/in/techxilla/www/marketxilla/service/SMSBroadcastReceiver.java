package in.techxilla.www.marketxilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private Matcher matcher;
    private OTPReceiveListener otpReceiveListener;

    public SMSBroadcastReceiver(OTPReceiveListener receiveListener) {
        otpReceiveListener = receiveListener;
    }

    public SMSBroadcastReceiver() {
    }

    public OTPReceiveListener getOtpReceiveListener() {
        return otpReceiveListener;
    }

    public void setOtpReceiveListener(OTPReceiveListener otpReceiveListener) {
        this.otpReceiveListener = otpReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            final Bundle extras = intent.getExtras();
            final Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            if (status != null) {
                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (message != null) {
                            Pattern p = Pattern.compile(".*?(\\d{6}).*?");
                            matcher = p.matcher(message);
                        }

                        if (matcher.find()) {
                            if (otpReceiveListener != null) {
                                otpReceiveListener.onSuccessOtp(matcher.group(1));
                            }
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        }
    }

    public interface OTPReceiveListener {
        void onSuccessOtp(String otp);
    }
}
