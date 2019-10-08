package com.ran3000.notefication2.noteslist;

import androidx.annotation.NonNull;

import com.ran3000.notefication2.data.Note;

class NoteDiff {
    static final long DELETE_NOTE_FAKE_ID = -1;
    private final Note note;
    private final long toId;

    NoteDiff(@NonNull Note note, long toId) {
        this.note = note;
        this.toId = toId;
    }

    public Note getNote() {
        return note;
    }

    public long getToId() {
        return toId;
    }
}
