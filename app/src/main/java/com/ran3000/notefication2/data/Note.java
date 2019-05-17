package com.ran3000.notefication2.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.ran3000.notefication2.ColorManager;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "color")
    @ColorManager.NoteficationColor
    private int color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ColorManager.NoteficationColor
    public int getColor() {
        return color;
    }

    public void setColor(@ColorManager.NoteficationColor int color) {
        this.color = color;
    }
}

