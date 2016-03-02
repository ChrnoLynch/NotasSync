package com.example.lprub.notassync.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contrato {

    public Contrato(){
    }

    public static abstract class TablaNota implements BaseColumns {

        public static final String TABLA = "notas";
        public static final String LOGIN = "login";
        public static final String CONTENIDO = "contenido";
        public static final String ESTADO = "estado";

        //La autoridad es la cadena q identifica a qué contentprovider se llama
        public final static String AUTHORITY = "com.example.lprub.notassync";
        //Definir como llego a la tabla cliente (a q tabla estoy llegando)
        public final static Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLA);
        public final static Uri CAMBIARESTADO=
                Uri.parse("content://" + AUTHORITY + "/" + TABLA+"/"+3);
//                Uri.parse("content://" + AUTHORITY + "/" + TABLA+"/ESTADO");
        public final static String SINGLE_MIME =
                "vnd.android.cursor.item/vnd." + AUTHORITY + TABLA;
        public final static String MJLTIPLE_MIME =
                "vnd.android.cursor.dir/vnd." + AUTHORITY + TABLA;
    }
}
