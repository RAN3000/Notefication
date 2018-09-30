package com.ran3000.notefication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ran3000.notefication.AppExecutors;
import com.ran3000.notefication.NoteficationManager;
import com.ran3000.notefication.data.Note;
import com.ran3000.notefication.data.NoteDatabase;

import timber.log.Timber;

public class BootCompletedReceiver extends BroadcastReceiver {

    private AppExecutors executors = new AppExecutors();

    @Override
    public void onReceive(Context context, Intent intent) {

        NoteficationManager manager = new NoteficationManager(context);

        Timber.d("Boot completed.");

        executors.diskIO().execute(() -> {
            NoteDatabase database = NoteDatabase.getAppDatabase(context);

            for (Note note : database.noteDao().getAll()) {
                manager.createNotification(note.getId(), note);
            }

        });

    }

}
