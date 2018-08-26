package com.example.android.store.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.store.data.StoreContract.StoreEntry;

public class StoreDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Store.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NAN = " NOT NULL";
    private static final String COMMA = ",";
    private static final String CREATE_TABLE =
            "CREATE TABLE " + StoreEntry.TABLE_NAME + " (" +
                    StoreEntry.COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA +
                    StoreEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + NAN + COMMA +
                    StoreEntry.COLUMN_PRODUCT_IMAGE + TEXT_TYPE + NAN + COMMA +
                    StoreEntry.COLUMN_PRODUCT_PRICE + INTEGER_TYPE + NAN + COMMA +
                    StoreEntry.COLUMN_PRODUCT_TYPE + INTEGER_TYPE + NAN + COMMA +
                    StoreEntry.COLUMN_PRODUCT_QUANTITY + INTEGER_TYPE + NAN + " DEFAULT 1" + COMMA +
                    StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME + TEXT_TYPE + NAN + COMMA +
                    StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO + TEXT_TYPE + NAN + " );";

    private static final String DELETE_PRODUCTS =
            "DROP TABLE IF EXISTS " + StoreEntry.TABLE_NAME;

    public StoreDBHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_PRODUCTS);
        onCreate(db);
    }
}
