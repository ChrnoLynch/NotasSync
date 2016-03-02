package com.example.lprub.notassync.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class Proveedor extends ContentProvider {

    public static final UriMatcher convierteUri;
    public static final int NOTAS = 1;
    public static final int NOTAS_ID = 2;
    public static final int ESTADO=3;

    private Ayudante abd;
    private static SQLiteDatabase db;
    static {
        convierteUri = new UriMatcher(UriMatcher.NO_MATCH);
        convierteUri.addURI(Contrato.TablaNota.AUTHORITY, Contrato.TablaNota.TABLA, NOTAS );
        convierteUri.addURI(Contrato.TablaNota.AUTHORITY, Contrato.TablaNota.TABLA + "/2", NOTAS_ID);
        convierteUri.addURI(Contrato.TablaNota.AUTHORITY, Contrato.TablaNota.TABLA + "/3", ESTADO);
    }

    @Override
    public boolean onCreate() {
        abd = new Ayudante((this.getContext()));
        db=abd.getReadableDatabase();
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (convierteUri.match(uri)) {
            case NOTAS:
                return Contrato.TablaNota.MJLTIPLE_MIME;
            case NOTAS_ID:
                return Contrato.TablaNota.SINGLE_MIME;
            case ESTADO:
                return Contrato.TablaNota.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de actividad desconocida " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId=0;
        ContentValues contentValues;
        if (values == null) {
            throw new IllegalArgumentException(" null ");
        }
        int conver=convierteUri.match(uri);
        if (conver != NOTAS ) {
            throw new IllegalArgumentException("URI desconocida : " + uri);//SI no es correcta la Uri
        }
        SQLiteDatabase db = abd.getWritableDatabase();
        rowId = db.insert(Contrato.TablaNota.TABLA, null, values);

        if (rowId > 0) {
            //Si se ha insertado el elemento correctamente, entonces devolvemos la uri del elemento que se acaba de insertar
            Uri uri_actividad = ContentUris.withAppendedId(Contrato.TablaNota.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri_actividad, null);
            return uri_actividad;
        }
        throw new SQLException("Error al insertar fila en : " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = abd.getWritableDatabase();// Vuelve a abrir la base de datos para conectar con ella en modo escritura
        long idActividad = ContentUris.parseId(uri);
        int match = convierteUri.match(uri);//Obtengo la uri
        int affected;
        switch (match) {
            case NOTAS:
                affected = db.delete(Contrato.TablaNota.TABLA, selection,selectionArgs);
                break;
            case NOTAS_ID:
                affected = db.delete(Contrato.TablaNota.TABLA, Contrato.TablaNota._ID + "= ?" , new String [] {idActividad + ""});
                break;
            case ESTADO:
                affected = db.delete(Contrato.TablaNota.TABLA, Contrato.TablaNota._ID + "= ?", selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Elemento actividad desconocido: " + uri);
        }
        // Notificar cambio asociado a la urigetContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return affected;//Devuelve el numero de filas borradas
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = abd.getWritableDatabase();
        int affected;
        switch (convierteUri.match(uri)) {
            case NOTAS:
                affected = db.update(Contrato.TablaNota.TABLA, values, selection, selectionArgs);
                break;
            case NOTAS_ID:
                String idActividad = uri.getPathSegments().get(1);
                affected = db.update(Contrato.TablaNota.TABLA, values,
                        Contrato.TablaNota._ID + "= ?", new String[]{idActividad});
                break;
            case ESTADO:
                affected = db.update(Contrato.TablaNota.TABLA, values,
                        Contrato.TablaNota._ID + "= ?", selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = abd.getReadableDatabase();
        int match = convierteUri.match(uri);
        Cursor c = db.query(Contrato.TablaNota.TABLA, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), Contrato.TablaNota.CONTENT_URI);
        return c;
    }


}




