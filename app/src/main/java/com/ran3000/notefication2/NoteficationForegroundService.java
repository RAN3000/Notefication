package com.ran3000.notefication2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

import java.util.List;

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

            for (Note note : database.noteDao().getAll()) {
                manager.createNotification(note.getId(), note);
            }
        });

        startForeground(ID, manager.createNotificationForService());
        return START_STICKY;
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
