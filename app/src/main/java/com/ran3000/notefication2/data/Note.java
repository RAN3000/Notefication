package com.ran3000.notefication2.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    private long orderId;

    @ColumnInfo(name = "sticky")
    private boolean sticky;

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

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", orderId=" + orderId +
                ", sticky=" + sticky +
                '}';
    }
}

