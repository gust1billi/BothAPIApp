package com.example.bothapiapp.databasehandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bothapiapp.Utils;

import java.sql.Timestamp;
import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {
    private static final String PRODUCT_TABLE = "products";
    private static final String BARCODE_TABLE = "barcodes";
    private static final String TABLE_ID = "id";

    private static final String PRODUCT_CODE = "product_code";

    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_PRICE = "price";

    private static final String BARCODE_ID = "barcode_id";
    private static final String BARCODE = "barcode";
    private static final String BARCODE_CREATE_AT = "create_at";
    private static final String BARCODE_UPDATE_AT = "update_at";

    private final Context context;

    private static final String DATABASE_NAME = "TMI_TEST_PRODUCTS";
    private static final int DATABASE_VERSION = 1;

    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTableProducts = "CREATE TABLE " + PRODUCT_TABLE + " ("
                + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + PRODUCT_NAME + " TEXT, "
                + PRODUCT_CODE + " TEXT, "
                + PRODUCT_PRICE + " TEXT"
                + ");";

        String queryTableBarcodes = "CREATE TABLE " + BARCODE_TABLE + " ("
                + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + BARCODE_ID + " INTEGER, "
                + PRODUCT_CODE + " TEXT, "
                + BARCODE + " TEXT, "
                + BARCODE_CREATE_AT + " DATE DEFAULT CURRENT_DATE, "
                + BARCODE_UPDATE_AT + " DATE DEFAULT CURRENT_DATE"
                + ");";

        db.execSQL(queryTableProducts);
        db.execSQL(queryTableBarcodes);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE );
        db.execSQL("DROP TABLE IF EXISTS " + BARCODE_TABLE );
        onCreate(db);
    }

    public void addProduct(String name, String code, String price){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PRODUCT_NAME, name);
        values.put(PRODUCT_CODE, code);
        values.put(PRODUCT_PRICE, price);

        db.insert( PRODUCT_TABLE, null, values );
    }

    public void addBarcode(int bid, String code, String barcode,
                           String create_at, String update_at ){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( BARCODE_ID, bid);
        values.put( PRODUCT_CODE, code );
        values.put( BARCODE, barcode );
        values.put( BARCODE_CREATE_AT, String.valueOf( create_at ) );
        values.put( BARCODE_UPDATE_AT, String.valueOf( update_at ) );

        // long result =
                db.insert( BARCODE_TABLE, null, values );
//        String callback;
//
//        if ( result == -1 ){
//            callback = "Failure";
//        } else callback = "Success";

        // Log.e("Callback Barcode", callback);
        // Utils.showToast(context, callback);
    }

    public Cursor readAllProducts(){
        Cursor cursor = null;
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();

        if (db != null)
            cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE, null);

        return cursor;
    }

    public Cursor readAllBarcodes(){
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null)
            cursor = db.rawQuery("SELECT * FROM " + BARCODE_TABLE, null);

        return cursor;
    }

    public Cursor readProductBarcodes( String key ){
        String query =
                "SELECT " + BARCODE + " FROM " + BARCODE_TABLE
                        + " WHERE " + PRODUCT_CODE + " = \"" + key + "\"";
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();
        Cursor cursor = db.rawQuery( query, null);
        return cursor;
    }
}
