package com.ran3000.notefication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.ran3000.notefication.data.Note;

public class NoteficationManager {

    private static final String CHANNEL_ID = "NoteficationChannel2";
    private static final String CHANNEL_NAME = "Notefication";
    private static final String CHANNEL_DESCRIPTION = "Sticky notes in notifications.";

    private RemoteViews remoteViews;

    private Bitmap bitmap = Bitmap.createBitmap(110, 110, Bitmap.Config.ARGB_8888);
    private Canvas canvas;
    Paint paint = new Paint();

    @NonNull
    private final Context context;

    public NoteficationManager(@NonNull Context context) {
        this.context = context;
        paint.setStyle(Paint.Style.FILL);
        canvas = new Canvas(bitmap);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_expanded_layout);
    }

    public void createNotification(@NonNull Note note) {
        paint.setColor(ContextCompat.getColor(context, note.getColor()));
        canvas.drawCircle(55, 55, 55, paint);

        remoteViews.setTextViewText(R.id.notification_text, note.getText());
        remoteViews.setInt(R.id.notification_layout, "setBackgroundResource", note.getColor());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(context, note.getColor()))
                .setContentTitle(note.getText())
                .setContentText("Expand for more.")
                .setCustomBigContentView(remoteViews)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int) note.getId(), mBuilder.build());
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
