package com.ran3000.notefication2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.SystemClock;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

import java.util.List;

import timber.log.Timber;

/* useful links:
 *  - https://androidwave.com/foreground-service-android-example/
 *  - https://developer.android.com/guide/components/services.html#Foreground
 */
public class NoteficationForegroundService extends Service {

    public static final int ID = 3389;

    private AppExecutors executors;
    private NoteficationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        executors = new AppExecutors();
        manager = new NoteficationManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executors.diskIO().execute(() -> {
            NoteDatabase database = NoteDatabase.getAppDatabase(this);

            manager.deleteAllNotifications();
            for (Note note : database.noteDao().getAllSticky()) {
                manager.createNotification(note.getId(), note);
            }
        });

        startForeground(ID, manager.createNotificationForService());
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Following code from https://stackoverflow.com/a/20677827
        // also see https://stackoverflow.com/a/42120277 for what you need to do in some devices in
        // order for the system to allow restart services (Huawei, Xiaomi,...).
        Timber.e("FLAGX : %s", ServiceInfo.FLAG_STOP_WITH_TASK);
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
