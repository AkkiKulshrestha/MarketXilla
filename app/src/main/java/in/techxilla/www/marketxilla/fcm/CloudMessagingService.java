package in.techxilla.www.marketxilla.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

public class CloudMessagingService extends FirebaseMessagingService {
    public static final String NOTIFICATION_CHANNEL_ID = String.valueOf(System.currentTimeMillis());
    private static final String TAG = "CloudMessagingService";
    private final static String default_notification_channel_id = "default";
    private String mController = null;
    private String mUrl = null;
    private String contentText = "";
    private String title = "";

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            if (remoteMessage.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

                if (extras.getString("controller") != null)
                    mController = extras.getString("controller");

                if (extras.getString("url") != null)
                    mUrl = extras.getString("url");
            }

            if (remoteMessage.getNotification() != null) {
                contentText = remoteMessage.getNotification().getBody();
                title = remoteMessage.getNotification().getTitle();
            }

            if (remoteMessage.getData() != null) {
                sendNotification(mController, mUrl, title, contentText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    public String CreateNewToken() {
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        return refreshedToken;
    }

    /**
     * Persist token on third-party servers using your Retrofit APIs client.
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "token", token);
    }

    /**
     * Schedule async work using WorkManager mostly these are one type job.
     */
    private void sendNotification(String controller, String url, String title, String contentText) {
        PendingIntent resultPendingIntent;
        Intent intentApp = null;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        intentApp = new Intent(this, NewDashboard.class);
        stackBuilder.addNextIntent(intentApp);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, default_notification_channel_id)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(getApplicationContext().getResources().getColor(R.color.black))
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "CLOUD MESSAGING SERVICE",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}