package com.piyush.havetodo.AppDB;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

public class Db_Impl extends Db {
  private volatile ToDoDao _toDoDao;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ToDo` (`toDoId` TEXT NOT NULL, `toDoColor` INTEGER NOT NULL, `toDoDate` INTEGER, `toDoTxt` TEXT, `moveToArchive` INTEGER NOT NULL, `hasToDoReminder` INTEGER NOT NULL, PRIMARY KEY(`toDoId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"f3d35217126a3e690eba44d35446b714\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `ToDo`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsToDo = new HashMap<String, TableInfo.Column>(6);
        _columnsToDo.put("toDoId", new TableInfo.Column("toDoId", "TEXT", true, 1));
        _columnsToDo.put("toDoColor", new TableInfo.Column("toDoColor", "INTEGER", true, 0));
        _columnsToDo.put("toDoDate", new TableInfo.Column("toDoDate", "INTEGER", false, 0));
        _columnsToDo.put("toDoTxt", new TableInfo.Column("toDoTxt", "TEXT", false, 0));
        _columnsToDo.put("moveToArchive", new TableInfo.Column("moveToArchive", "INTEGER", true, 0));
        _columnsToDo.put("hasToDoReminder", new TableInfo.Column("hasToDoReminder", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysToDo = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesToDo = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoToDo = new TableInfo("ToDo", _columnsToDo, _foreignKeysToDo, _indicesToDo);
        final TableInfo _existingToDo = TableInfo.read(_db, "ToDo");
        if (! _infoToDo.equals(_existingToDo)) {
          throw new IllegalStateException("Migration didn't properly handle ToDo(com.piyush.havetodo.AppDB.ToDo).\n"
                  + " Expected:\n" + _infoToDo + "\n"
                  + " Found:\n" + _existingToDo);
        }
      }
    }, "f3d35217126a3e690eba44d35446b714");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "ToDo");
  }

  @Override
  public ToDoDao toDoDao() {
    if (_toDoDao != null) {
      return _toDoDao;
    } else {
      synchronized(this) {
        if(_toDoDao == null) {
          _toDoDao = new ToDoDao_Impl(this);
        }
        return _toDoDao;
      }
    }
  }
}
