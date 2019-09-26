package com.ran3000.notefication2.noteslist;

import androidx.annotation.NonNull;

import com.ran3000.notefication2.data.Note;

class NoteDiff {
    private final Note note;
    private final long toId;

    NoteDiff(@NonNull Note note, @NonNull long toId) {
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
