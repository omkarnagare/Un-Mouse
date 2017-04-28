package com.nagare.balkrishna.omkar.unmouse.GreenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.nagare.balkrishna.omkar.unmouse.Entity.BluetoothInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BLUETOOTH_INFO".
*/
public class BluetoothInfoDao extends AbstractDao<BluetoothInfo, Long> {

    public static final String TABLENAME = "BLUETOOTH_INFO";

    /**
     * Properties of entity BluetoothInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property BluetoothInfoId = new Property(0, Long.class, "bluetoothInfoId", true, "_id");
        public final static Property BluetoothName = new Property(1, String.class, "bluetoothName", false, "BLUETOOTH_NAME");
        public final static Property BluetoothAddress = new Property(2, String.class, "bluetoothAddress", false, "BLUETOOTH_ADDRESS");
    }


    public BluetoothInfoDao(DaoConfig config) {
        super(config);
    }
    
    public BluetoothInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BLUETOOTH_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: bluetoothInfoId
                "\"BLUETOOTH_NAME\" TEXT," + // 1: bluetoothName
                "\"BLUETOOTH_ADDRESS\" TEXT);"); // 2: bluetoothAddress
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_BLUETOOTH_INFO_BLUETOOTH_ADDRESS ON BLUETOOTH_INFO" +
                " (\"BLUETOOTH_ADDRESS\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BLUETOOTH_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BluetoothInfo entity) {
        stmt.clearBindings();
 
        Long bluetoothInfoId = entity.getBluetoothInfoId();
        if (bluetoothInfoId != null) {
            stmt.bindLong(1, bluetoothInfoId);
        }
 
        String bluetoothName = entity.getBluetoothName();
        if (bluetoothName != null) {
            stmt.bindString(2, bluetoothName);
        }
 
        String bluetoothAddress = entity.getBluetoothAddress();
        if (bluetoothAddress != null) {
            stmt.bindString(3, bluetoothAddress);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BluetoothInfo entity) {
        stmt.clearBindings();
 
        Long bluetoothInfoId = entity.getBluetoothInfoId();
        if (bluetoothInfoId != null) {
            stmt.bindLong(1, bluetoothInfoId);
        }
 
        String bluetoothName = entity.getBluetoothName();
        if (bluetoothName != null) {
            stmt.bindString(2, bluetoothName);
        }
 
        String bluetoothAddress = entity.getBluetoothAddress();
        if (bluetoothAddress != null) {
            stmt.bindString(3, bluetoothAddress);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BluetoothInfo readEntity(Cursor cursor, int offset) {
        BluetoothInfo entity = new BluetoothInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // bluetoothInfoId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // bluetoothName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // bluetoothAddress
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BluetoothInfo entity, int offset) {
        entity.setBluetoothInfoId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBluetoothName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setBluetoothAddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BluetoothInfo entity, long rowId) {
        entity.setBluetoothInfoId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BluetoothInfo entity) {
        if(entity != null) {
            return entity.getBluetoothInfoId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BluetoothInfo entity) {
        return entity.getBluetoothInfoId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}