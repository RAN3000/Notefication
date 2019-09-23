package com.ran3000.notefication2.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {Note.class}, version = 2)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase INSTANCE;

    public abstract NoteDao noteDao();

    public static NoteDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note-database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes "
                    + " ADD COLUMN orderId INTEGER NOT NULL DEFAULT 0");
        }
    };

}

