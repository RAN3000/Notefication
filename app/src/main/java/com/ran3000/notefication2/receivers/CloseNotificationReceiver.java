package com.ran3000.notefication2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ran3000.notefication2.AppExecutors;
import com.ran3000.notefication2.NoteficationForegroundService;
import com.ran3000.notefication2.NoteficationManager;
import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

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

                NoteficationManager manager = new NoteficationManager(context);
                manager.deleteNotification(note.getId());
                database.noteDao().delete(note);

                if (database.noteDao().getStickyNotesCount() == 0) {
                    Intent serviceIntent = new Intent(context, NoteficationForegroundService.class);
                    context.stopService(serviceIntent);
                }
            }

        });
    }

}
