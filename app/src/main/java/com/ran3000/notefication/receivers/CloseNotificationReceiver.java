package com.ran3000.notefication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.ran3000.notefication.AppExecutors;
import com.ran3000.notefication.data.Note;
import com.ran3000.notefication.data.NoteDatabase;

import timber.log.Timber;

public class CloseNotificationReceiver extends BroadcastReceiver {

    private AppExecutors executors = new AppExecutors();

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra("id", -1);

        executors.diskIO().execute(() -> {

            NoteDatabase database = NoteDatabase.getAppDatabase(context);

            if (id == -1) {
                executors.mainThread().execute(() -> {
                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                });
            } else {
                Note note = database.noteDao().getById(id);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel((int) note.getId());
                database.noteDao().delete(note);
            }

        });
    }

}
