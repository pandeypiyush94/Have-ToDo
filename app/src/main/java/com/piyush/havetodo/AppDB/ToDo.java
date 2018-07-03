package com.piyush.havetodo.AppDB;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 Created by Piyush on 2/23/2018.
 */
@Entity
public class ToDo implements Serializable,Parcelable {
    @PrimaryKey
    @NonNull
    public UUID toDoId;
    public int toDoColor;
    public Date toDoDate;
    public String toDoTxt;
    public boolean moveToArchive;
    public boolean hasToDoReminder;

    public ToDo(@NonNull UUID toDoId, int toDoColor, Date toDoDate, String toDoTxt, boolean moveToArchive, boolean hasToDoReminder) {
        this.toDoId = toDoId;
        this.toDoColor = toDoColor;
        this.toDoDate = toDoDate;
        this.toDoTxt = toDoTxt;
        this.moveToArchive = moveToArchive;
        this.hasToDoReminder = hasToDoReminder;
    }

    public ToDo(Parcel in) {
        toDoColor = in.readInt();
        toDoTxt = in.readString();
        toDoDate = new Date(in.readLong());
        toDoId = UUID.fromString(in.readString());
        hasToDoReminder = in.readByte() != 0;
    }
    public static final Creator<ToDo> CREATOR = new Creator<ToDo>() {
        @Override
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        @Override
        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(toDoColor);
        dest.writeString(toDoTxt);
        if (toDoDate != null)
            dest.writeLong(toDoDate.getTime());
        else
            dest.writeLong(new Date().getTime());
        dest.writeString(toDoId.toString());
        dest.writeByte((byte) (hasToDoReminder ? 1 : 0));
    }
}
