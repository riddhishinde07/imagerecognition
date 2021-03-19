package com.example.captureimage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class dbhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "image.db";
    public static final String TABLE_NAME = "Registration_table";
    public static final String Name= "name";
    public static final String Email="email";
    public static final String Password = "password";



    public dbhelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (Name Text(30)not null,Email Text(20)not null,Password TEXT(20)not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
