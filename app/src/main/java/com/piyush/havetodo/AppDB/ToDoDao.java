package com.piyush.havetodo.AppDB;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 Created by Piyush on 2/23/2018.
 */

@Dao
public interface ToDoDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insertToDo(ToDo model);

    @Query("select * from ToDo where moveToArchive = :val")
    List<ToDo> fetchAllToDo(boolean val);

    @Query("select * from ToDo where moveToArchive = :val")
    List<ToDo> fetchPastToDo(boolean val);

    @Query("select * from ToDo where toDoId = :id")
    ToDo fetchToDo(UUID id);

    @Update
    int updateTodo(ToDo model);

    @Query("update ToDo set moveToArchive = :val where toDoId = :id")
    void updateArchive(UUID id, boolean val);

    @Query("update ToDo set toDoDate = :date, hasToDoReminder = :val where toDoId = :id")
    void updateToDoDate(UUID id, boolean val, Date date);

    @Query("delete from ToDo where toDoId = :id")
    void deleteToDo(UUID id);

    @Query("delete from ToDo where toDoDate >= :currentTime")
    void deleteAll(long currentTime);

    class Converters{
        @TypeConverter
        public static String uuidToString(UUID id){
            if (id != null)
                return id.toString();
            else return "";
        }
        @TypeConverter
        public static UUID stringToUUID(String txt){
            if (txt != null)
                return UUID.fromString(txt);
            else
                return UUID.randomUUID();}

        @TypeConverter
        public static Long dateToLong(Date obj){
            if (obj != null)
                return obj.getTime();
            else
                return null;
        }
        @TypeConverter
        public static Date longToDate(Long time){
            if (time != null)
               return new Date(time);
            else
                return null;
        }
    }
}
