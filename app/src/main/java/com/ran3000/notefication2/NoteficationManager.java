package com.ran3000.notefication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.RemoteViews;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.receivers.CloseNotificationReceiver;

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

    public void createNotification(long id, @NonNull Note note) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra("note_id", id);
        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent closeIntent = new Intent(context, CloseNotificationReceiver.class);
        closeIntent.putExtra("id", id);
        PendingIntent closePendingIntent =
                PendingIntent.getBroadcast(context, (int) id, closeIntent, 0);

        paint.setColor(ContextCompat.getColor(context, note.getColor()));
        canvas.drawCircle(55, 55, 55, paint);

        remoteViews.setTextViewText(R.id.notification_text, note.getText());
        remoteViews.setInt(R.id.notification_layout, "setBackgroundResource", note.getColor());
        remoteViews.setOnClickPendingIntent(R.id.notification_close_button, closePendingIntent);
        if (note.getText().startsWith("_")) {
            remoteViews.setViewVisibility(R.id.notification_thread_indicator, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.notification_thread_indicator, View.GONE);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(context, note.getColor()))
                .setContentTitle(note.getText())
                .setContentText("Expand for more.")
                .setContentIntent(contentPendingIntent)
                .setCustomBigContentView(remoteViews)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int) id, mBuilder.build());
    }

    public Notification createNotificationForService() {
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Notefication")
                .setContentText("Running in the background to show your notes.")
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return mBuilder.build();
    }

    public void deleteNotification(long id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel((int) id);
    }

    public void deleteAllNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
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
