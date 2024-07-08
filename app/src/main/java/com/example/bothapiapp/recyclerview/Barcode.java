package com.example.bothapiapp.recyclerview;

import java.sql.Timestamp;

public class Barcode {
    int barcode_id;
    String product_code, barcode;
    Timestamp create_at, update_at;

    public int getBarcode_id() {
        return barcode_id;
    }

    public void setBarcode_id(int barcode_id) {
        this.barcode_id = barcode_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Timestamp getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Timestamp create_at) {
        this.create_at = create_at;
    }

    public Timestamp getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Timestamp update_at) {
        this.update_at = update_at;
    }
}

/* String queryTableBarcodes = "CREATE TABLE " + BARCODE_TABLE + " ("
     + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
     + BARCODE_ID + " INTEGER, "
     + PRODUCT_CODE + " TEXT, "
     + BARCODE + " TEXT, "
     + BARCODE_CREATE_AT + " DATETIME, "
     + BARCODE_UPDATE_AT + " DATETIME "
     + ");"; */