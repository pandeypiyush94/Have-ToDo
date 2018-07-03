package com.piyush.havetodo.AppDB;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ToDoDao_Impl implements ToDoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfToDo;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfToDo;

  private final SharedSQLiteStatement __preparedStmtOfUpdateArchive;

  private final SharedSQLiteStatement __preparedStmtOfUpdateToDoDate;

  private final SharedSQLiteStatement __preparedStmtOfDeleteToDo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ToDoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfToDo = new EntityInsertionAdapter<ToDo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `ToDo`(`toDoId`,`toDoColor`,`toDoDate`,`toDoTxt`,`moveToArchive`,`hasToDoReminder`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ToDo value) {
        final String _tmp;
        _tmp = ToDoDao.Converters.uuidToString(value.toDoId);
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, _tmp);
        }
        stmt.bindLong(2, value.toDoColor);
        final Long _tmp_1;
        _tmp_1 = ToDoDao.Converters.dateToLong(value.toDoDate);
        if (_tmp_1 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp_1);
        }
        if (value.toDoTxt == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.toDoTxt);
        }
        final int _tmp_2;
        _tmp_2 = value.moveToArchive ? 1 : 0;
        stmt.bindLong(5, _tmp_2);
        final int _tmp_3;
        _tmp_3 = value.hasToDoReminder ? 1 : 0;
        stmt.bindLong(6, _tmp_3);
      }
    };
    this.__updateAdapterOfToDo = new EntityDeletionOrUpdateAdapter<ToDo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `ToDo` SET `toDoId` = ?,`toDoColor` = ?,`toDoDate` = ?,`toDoTxt` = ?,`moveToArchive` = ?,`hasToDoReminder` = ? WHERE `toDoId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ToDo value) {
        final String _tmp;
        _tmp = ToDoDao.Converters.uuidToString(value.toDoId);
        if (_tmp == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, _tmp);
        }
        stmt.bindLong(2, value.toDoColor);
        final Long _tmp_1;
        _tmp_1 = ToDoDao.Converters.dateToLong(value.toDoDate);
        if (_tmp_1 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp_1);
        }
        if (value.toDoTxt == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.toDoTxt);
        }
        final int _tmp_2;
        _tmp_2 = value.moveToArchive ? 1 : 0;
        stmt.bindLong(5, _tmp_2);
        final int _tmp_3;
        _tmp_3 = value.hasToDoReminder ? 1 : 0;
        stmt.bindLong(6, _tmp_3);
        final String _tmp_4;
        _tmp_4 = ToDoDao.Converters.uuidToString(value.toDoId);
        if (_tmp_4 == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, _tmp_4);
        }
      }
    };
    this.__preparedStmtOfUpdateArchive = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update ToDo set moveToArchive = ? where toDoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateToDoDate = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update ToDo set toDoDate = ?, hasToDoReminder = ? where toDoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteToDo = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "delete from ToDo where toDoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "delete from ToDo where toDoDate >= ?";
        return _query;
      }
    };
  }

  @Override
  public long insertToDo(ToDo model) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfToDo.insertAndReturnId(model);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int updateTodo(ToDo model) {
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__updateAdapterOfToDo.handle(model);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateArchive(UUID id, boolean val) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateArchive.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      final int _tmp;
      _tmp = val ? 1 : 0;
      _stmt.bindLong(_argIndex, _tmp);
      _argIndex = 2;
      final String _tmp_1;
      _tmp_1 = ToDoDao.Converters.uuidToString(id);
      if (_tmp_1 == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _tmp_1);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateArchive.release(_stmt);
    }
  }

  @Override
  public void updateToDoDate(UUID id, boolean val, Date date) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateToDoDate.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      final Long _tmp;
      _tmp = ToDoDao.Converters.dateToLong(date);
      if (_tmp == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindLong(_argIndex, _tmp);
      }
      _argIndex = 2;
      final int _tmp_1;
      _tmp_1 = val ? 1 : 0;
      _stmt.bindLong(_argIndex, _tmp_1);
      _argIndex = 3;
      final String _tmp_2;
      _tmp_2 = ToDoDao.Converters.uuidToString(id);
      if (_tmp_2 == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _tmp_2);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateToDoDate.release(_stmt);
    }
  }

  @Override
  public void deleteToDo(UUID id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteToDo.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      final String _tmp;
      _tmp = ToDoDao.Converters.uuidToString(id);
      if (_tmp == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _tmp);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteToDo.release(_stmt);
    }
  }

  @Override
  public void deleteAll(long currentTime) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindLong(_argIndex, currentTime);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<ToDo> fetchAllToDo(boolean val) {
    final String _sql = "select * from ToDo where moveToArchive = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp;
    _tmp = val ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfToDoId = _cursor.getColumnIndexOrThrow("toDoId");
      final int _cursorIndexOfToDoColor = _cursor.getColumnIndexOrThrow("toDoColor");
      final int _cursorIndexOfToDoDate = _cursor.getColumnIndexOrThrow("toDoDate");
      final int _cursorIndexOfToDoTxt = _cursor.getColumnIndexOrThrow("toDoTxt");
      final int _cursorIndexOfMoveToArchive = _cursor.getColumnIndexOrThrow("moveToArchive");
      final int _cursorIndexOfHasToDoReminder = _cursor.getColumnIndexOrThrow("hasToDoReminder");
      final List<ToDo> _result = new ArrayList<ToDo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ToDo _item;
        final UUID _tmpToDoId;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfToDoId);
        _tmpToDoId = ToDoDao.Converters.stringToUUID(_tmp_1);
        final int _tmpToDoColor;
        _tmpToDoColor = _cursor.getInt(_cursorIndexOfToDoColor);
        final Date _tmpToDoDate;
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfToDoDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfToDoDate);
        }
        _tmpToDoDate = ToDoDao.Converters.longToDate(_tmp_2);
        final String _tmpToDoTxt;
        _tmpToDoTxt = _cursor.getString(_cursorIndexOfToDoTxt);
        final boolean _tmpMoveToArchive;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfMoveToArchive);
        _tmpMoveToArchive = _tmp_3 != 0;
        final boolean _tmpHasToDoReminder;
        final int _tmp_4;
        _tmp_4 = _cursor.getInt(_cursorIndexOfHasToDoReminder);
        _tmpHasToDoReminder = _tmp_4 != 0;
        _item = new ToDo(_tmpToDoId,_tmpToDoColor,_tmpToDoDate,_tmpToDoTxt,_tmpMoveToArchive,_tmpHasToDoReminder);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ToDo> fetchPastToDo(boolean val) {
    final String _sql = "select * from ToDo where moveToArchive = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp;
    _tmp = val ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfToDoId = _cursor.getColumnIndexOrThrow("toDoId");
      final int _cursorIndexOfToDoColor = _cursor.getColumnIndexOrThrow("toDoColor");
      final int _cursorIndexOfToDoDate = _cursor.getColumnIndexOrThrow("toDoDate");
      final int _cursorIndexOfToDoTxt = _cursor.getColumnIndexOrThrow("toDoTxt");
      final int _cursorIndexOfMoveToArchive = _cursor.getColumnIndexOrThrow("moveToArchive");
      final int _cursorIndexOfHasToDoReminder = _cursor.getColumnIndexOrThrow("hasToDoReminder");
      final List<ToDo> _result = new ArrayList<ToDo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ToDo _item;
        final UUID _tmpToDoId;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfToDoId);
        _tmpToDoId = ToDoDao.Converters.stringToUUID(_tmp_1);
        final int _tmpToDoColor;
        _tmpToDoColor = _cursor.getInt(_cursorIndexOfToDoColor);
        final Date _tmpToDoDate;
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfToDoDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfToDoDate);
        }
        _tmpToDoDate = ToDoDao.Converters.longToDate(_tmp_2);
        final String _tmpToDoTxt;
        _tmpToDoTxt = _cursor.getString(_cursorIndexOfToDoTxt);
        final boolean _tmpMoveToArchive;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfMoveToArchive);
        _tmpMoveToArchive = _tmp_3 != 0;
        final boolean _tmpHasToDoReminder;
        final int _tmp_4;
        _tmp_4 = _cursor.getInt(_cursorIndexOfHasToDoReminder);
        _tmpHasToDoReminder = _tmp_4 != 0;
        _item = new ToDo(_tmpToDoId,_tmpToDoColor,_tmpToDoDate,_tmpToDoTxt,_tmpMoveToArchive,_tmpHasToDoReminder);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ToDo fetchToDo(UUID id) {
    final String _sql = "select * from ToDo where toDoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp;
    _tmp = ToDoDao.Converters.uuidToString(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfToDoId = _cursor.getColumnIndexOrThrow("toDoId");
      final int _cursorIndexOfToDoColor = _cursor.getColumnIndexOrThrow("toDoColor");
      final int _cursorIndexOfToDoDate = _cursor.getColumnIndexOrThrow("toDoDate");
      final int _cursorIndexOfToDoTxt = _cursor.getColumnIndexOrThrow("toDoTxt");
      final int _cursorIndexOfMoveToArchive = _cursor.getColumnIndexOrThrow("moveToArchive");
      final int _cursorIndexOfHasToDoReminder = _cursor.getColumnIndexOrThrow("hasToDoReminder");
      final ToDo _result;
      if(_cursor.moveToFirst()) {
        final UUID _tmpToDoId;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfToDoId);
        _tmpToDoId = ToDoDao.Converters.stringToUUID(_tmp_1);
        final int _tmpToDoColor;
        _tmpToDoColor = _cursor.getInt(_cursorIndexOfToDoColor);
        final Date _tmpToDoDate;
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfToDoDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfToDoDate);
        }
        _tmpToDoDate = ToDoDao.Converters.longToDate(_tmp_2);
        final String _tmpToDoTxt;
        _tmpToDoTxt = _cursor.getString(_cursorIndexOfToDoTxt);
        final boolean _tmpMoveToArchive;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfMoveToArchive);
        _tmpMoveToArchive = _tmp_3 != 0;
        final boolean _tmpHasToDoReminder;
        final int _tmp_4;
        _tmp_4 = _cursor.getInt(_cursorIndexOfHasToDoReminder);
        _tmpHasToDoReminder = _tmp_4 != 0;
        _result = new ToDo(_tmpToDoId,_tmpToDoColor,_tmpToDoDate,_tmpToDoTxt,_tmpMoveToArchive,_tmpHasToDoReminder);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
