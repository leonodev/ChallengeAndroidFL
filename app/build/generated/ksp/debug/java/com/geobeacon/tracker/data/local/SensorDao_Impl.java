package com.geobeacon.tracker.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SensorDao_Impl implements SensorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SensorEntity> __insertionAdapterOfSensorEntity;

  public SensorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSensorEntity = new EntityInsertionAdapter<SensorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sensor_data` (`id`,`macAddress`,`rssi`,`latitude`,`longitude`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SensorEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMacAddress());
        statement.bindLong(3, entity.getRssi());
        statement.bindDouble(4, entity.getLatitude());
        statement.bindDouble(5, entity.getLongitude());
        statement.bindLong(6, entity.getTimestamp());
      }
    };
  }

  @Override
  public Object insert(final SensorEntity data, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSensorEntity.insert(data);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SensorEntity>> getLatestEvents() {
    final String _sql = "SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 10";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sensor_data"}, new Callable<List<SensorEntity>>() {
      @Override
      @NonNull
      public List<SensorEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "rssi");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<SensorEntity> _result = new ArrayList<SensorEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SensorEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final int _tmpRssi;
            _tmpRssi = _cursor.getInt(_cursorIndexOfRssi);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new SensorEntity(_tmpId,_tmpMacAddress,_tmpRssi,_tmpLatitude,_tmpLongitude,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
