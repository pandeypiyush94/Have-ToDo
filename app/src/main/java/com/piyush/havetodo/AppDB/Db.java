package com.piyush.havetodo.AppDB;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 Created by Piyush on 2/23/2018.
 */

@Database(entities = {ToDo.class}, version = 1)
@TypeConverters({ToDoDao.Converters.class})
public abstract class Db extends RoomDatabase {

    private static Db INSTANCE;

    public static Db getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),Db.class,"app_db").build();
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE = null;
    }

    public abstract ToDoDao toDoDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
