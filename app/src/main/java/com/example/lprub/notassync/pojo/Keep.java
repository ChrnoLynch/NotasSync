package com.example.lprub.notassync.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.lprub.notassync.contentprovider.Contrato;

public class Keep implements Parcelable{

    //id,contenido, estado
    private long id;
    private String contenido;
    private int estado;
    private String login;

    public Keep() {
    }

    public Keep(String contenido,int estado,String login) {
        this.login = login;
        this.estado = estado;
        this.contenido = contenido;
    }


    public Keep(long id, String contenido, int estado, String login) {
        this.id = id;
        this.contenido = contenido;
        this.estado = estado;
        this.login = login;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keep keep = (Keep) o;

        return id == keep.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    protected Keep(Parcel in) {
        id = in.readLong();
        contenido = in.readString();
        estado = in.readInt();
        login = in.readString();
    }

    public static final Creator<Keep> CREATOR = new Creator<Keep>() {
        @Override
        public Keep createFromParcel(Parcel in) {
            return new Keep(in);
        }

        @Override
        public Keep[] newArray(int size) {
            return new Keep[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "Keep{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", estado=" + estado +
                ", login='" + login + '\'' +
                '}';
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(Contrato.TablaNota.CONTENIDO,this.contenido);
        cv.put(Contrato.TablaNota.ESTADO,this.estado);
        cv.put(Contrato.TablaNota.LOGIN,this.login);
        return cv;
    }

    public void rellenarNota(Cursor c){
        this.id = c.getInt(c.getColumnIndex(Contrato.TablaNota._ID));
        this.contenido= c.getString(c.getColumnIndex(Contrato.TablaNota.CONTENIDO));
        this.estado = c.getInt(c.getColumnIndex(Contrato.TablaNota.ESTADO));
        this.login=c.getString(c.getColumnIndex(Contrato.TablaNota.LOGIN));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(contenido);
        dest.writeInt(estado);
        dest.writeString(login);
    }
}
