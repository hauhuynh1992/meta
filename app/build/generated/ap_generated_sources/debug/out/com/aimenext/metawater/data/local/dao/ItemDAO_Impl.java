package com.aimenext.metawater.data.local.dao;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aimenext.metawater.data.local.entity.Item;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ItemDAO_Impl implements ItemDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteJob;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ItemDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItem = new EntityInsertionAdapter<Item>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `items`(`id`,`type`,`code`,`image`,`unique`,`date`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Item value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getId());
        }
        if (value.getType() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getType());
        }
        if (value.getCode() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCode());
        }
        if (value.getImage() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getImage());
        }
        if (value.getUnique() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUnique());
        }
        if (value.getDate() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, value.getDate());
        }
      }
    };
    this.__preparedStmtOfDeleteJob = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM items WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM items";
        return _query;
      }
    };
  }

  @Override
  public void insert(List<Item> items) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfItem.insert(items);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insert(Item item) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfItem.insert(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteJob(Long id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteJob.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindLong(_argIndex, id);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteJob.release(_stmt);
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<Item> getItems() {
    final String _sql = "SELECT * FROM items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Item _item;
        _item = __entityCursorConverter_comAimenextMetawaterDataLocalEntityItem(_cursor);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Item getItemById(Long id) {
    final String _sql = "SELECT * FROM items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final Item _result;
      if(_cursor.moveToFirst()) {
        _result = __entityCursorConverter_comAimenextMetawaterDataLocalEntityItem(_cursor);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  private Item __entityCursorConverter_comAimenextMetawaterDataLocalEntityItem(Cursor cursor) {
    final Item _entity;
    final int _cursorIndexOfId = cursor.getColumnIndex("id");
    final int _cursorIndexOfType = cursor.getColumnIndex("type");
    final int _cursorIndexOfCode = cursor.getColumnIndex("code");
    final int _cursorIndexOfImage = cursor.getColumnIndex("image");
    final int _cursorIndexOfUnique = cursor.getColumnIndex("unique");
    final int _cursorIndexOfDate = cursor.getColumnIndex("date");
    final String _tmpType;
    if (_cursorIndexOfType == -1) {
      _tmpType = null;
    } else {
      _tmpType = cursor.getString(_cursorIndexOfType);
    }
    final String _tmpCode;
    if (_cursorIndexOfCode == -1) {
      _tmpCode = null;
    } else {
      _tmpCode = cursor.getString(_cursorIndexOfCode);
    }
    final String _tmpImage;
    if (_cursorIndexOfImage == -1) {
      _tmpImage = null;
    } else {
      _tmpImage = cursor.getString(_cursorIndexOfImage);
    }
    final String _tmpUnique;
    if (_cursorIndexOfUnique == -1) {
      _tmpUnique = null;
    } else {
      _tmpUnique = cursor.getString(_cursorIndexOfUnique);
    }
    final Long _tmpDate;
    if (_cursorIndexOfDate == -1) {
      _tmpDate = null;
    } else {
      if (cursor.isNull(_cursorIndexOfDate)) {
        _tmpDate = null;
      } else {
        _tmpDate = cursor.getLong(_cursorIndexOfDate);
      }
    }
    _entity = new Item(_tmpType,_tmpCode,_tmpImage,_tmpUnique,_tmpDate);
    if (_cursorIndexOfId != -1) {
      final Long _tmpId;
      if (cursor.isNull(_cursorIndexOfId)) {
        _tmpId = null;
      } else {
        _tmpId = cursor.getLong(_cursorIndexOfId);
      }
      _entity.setId(_tmpId);
    }
    return _entity;
  }
}
