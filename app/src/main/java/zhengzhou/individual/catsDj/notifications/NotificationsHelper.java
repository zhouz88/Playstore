package zhengzhou.individual.catsDj.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import zhengzhou.individual.catsDj.MainActivity;
import zhengzhou.individual.catsDj.R;

public class NotificationsHelper {

    private static volatile NotificationsHelper instance;

    public static NotificationsHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationsHelper.class) {
                instance = new NotificationsHelper(context);
            }
        }
        return instance;
    }

    private Context context;

    public NotificationsHelper(Context context) {
        this.context = context;

    }

    public void createNotification() {
        createNotificationChannel();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_cat_round);
        Notification notification = new NotificationCompat
                .Builder(context, "NEWS_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //sound ≥
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(icon)
                .setContentInfo("You can succeed!")
                .setContentText("You can succeed! All information downloaded")
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon).bigLargeIcon(null))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        NotificationManagerCompat.from(context).notify(123, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String name = "NEWS_ID";
            String description = "News obtained";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(name, name, importance);
            channel.setDescription(description);

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }
}
