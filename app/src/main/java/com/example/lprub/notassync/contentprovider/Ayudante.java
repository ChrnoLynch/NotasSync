package com.example.lprub.notassync.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Ayudante extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "keeps.sqlite";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql;

        sql = "create table " + Contrato.TablaNota.TABLA +
                " (" + Contrato.TablaNota._ID +
                " integer primary key autoincrement, " +
                Contrato.TablaNota.CONTENIDO + " text, " +
                Contrato.TablaNota.ESTADO + " integer," +
                Contrato.TablaNota.LOGIN+ " text)";

        db.execSQL(sql);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "drop table if exists "
                + Contrato.TablaNota.TABLA;
        db.execSQL(sql);

        onCreate(db);

    }
}
