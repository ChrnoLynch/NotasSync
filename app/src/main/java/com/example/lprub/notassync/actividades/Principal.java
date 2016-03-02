package com.example.lprub.notassync.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.lprub.notassync.R;
import com.example.lprub.notassync.adapter.ClaseAdaptador;
import com.example.lprub.notassync.contentprovider.Contrato;
import com.example.lprub.notassync.gestion.GestionKeep;
import com.example.lprub.notassync.pojo.Keep;
import com.example.lprub.notassync.pojo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {


    private Usuario user;
    private List<Keep> listaNotas;
    private GestionKeep servidor = new GestionKeep(this);
    private ClaseAdaptador cl;
    private ListView lv;
    private List<Keep> listaNotasBD;
    private static final int AÑADIR=1, EDITAR=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        lv = (ListView) findViewById(R.id.listView);
        //Recogemos el usuario con el que hemos logueado de la actividad de login.
        user = getIntent().getParcelableExtra("usuario");
         setTitle("Notas de "+user.getEmail());
        //Creamos un ArrayList y consultamos nuestro Content Provider para rellenarlo con todas las notas del usuario que ha logueado.
        listaNotas=new ArrayList<Keep>();
        Cursor c = getContentResolver().query(Contrato.TablaNota.CONTENT_URI, null, "login='"+user.getEmail()+"'", null, null);
        if (c != null) {
        //Si el cursor no esta vacio, lo recorremos y añadimos a un arraylist todas las notas para posteriormente usarlas en nuestro adaptador.
            while (c.moveToNext()) {
                Keep k = new Keep();
                k.rellenarNota(c);
                listaNotas.add(k);
            }
        }

        //Creamos el adaptador
        cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
            lv.setAdapter(cl);

        //Seteamos el escuchador para borrar la nota si mantenemos pulsada una nota
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                          @Override
                                          public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                              AlertDialog.Builder b = new AlertDialog.Builder(Principal.this);
                                              b.setMessage("¿Borrar nota?")
                                                      .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              final Keep k = listaNotas.get(position);
                                                              //Ejecutamos un hilo para marcar la nota como borrado en nuestro servidor y eliminarla de nuestra
                                                              //base de datos
                                                              Runnable r = new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      servidor.eliminarNota(k);
                                                                  }
                                                              };
                                                              Thread t = new Thread(r);
                                                              t.start();
                                                              String[] argumentos = {k.getId() + ""};
                                                              getContentResolver().delete(Contrato.TablaNota.CAMBIARESTADO, null, argumentos);
                                                              listaNotas.remove(position);
                                                              notifyDataChanged();
                                                          }
                                                      })
                                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {}
                                                      }).show();
                                              return false;
                                          }
                                      }
        );

        //Seteamos el escuchador para llamar al metodo editar, el cual nos abrira la actividad para editar la nota seleccionada.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                editar(position);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle guardaEstado) {
        super.onSaveInstanceState(guardaEstado);
        guardaEstado.putParcelableArrayList("salvado", (ArrayList<? extends Parcelable>) listaNotas);
    }

    @Override
    protected void onRestoreInstanceState(Bundle recuperaEstado) {
        super.onRestoreInstanceState(recuperaEstado);
        listaNotas=recuperaEstado.getParcelableArrayList("salvado");
        cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
        lv.setAdapter(cl);
    }

    public void añadirNota() {
        Keep k = null;
        Intent i = new Intent(this, Nota.class);
        Bundle b = new Bundle();
        b.putParcelable("nota", k);
        i.putExtras(b);
        startActivityForResult(i, AÑADIR);
    }

    public void editar(int position){
        Keep k = listaNotas.get(position);
        Intent i=new Intent(this,Nota.class);
        Bundle b=new Bundle();
        b.putParcelable("nota",k);
        b.putInt("pos", position);
        i.putExtras(b);
        startActivityForResult(i, EDITAR);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Bundle b;
            Keep k;
            switch (requestCode){
                case AÑADIR:
                    b =data.getExtras();
                    k=b.getParcelable("nota");
                    listaNotas.add(k);
                    notifyDataChanged();
                    Uri urinsercion = getContentResolver().insert(Contrato.TablaNota.CONTENT_URI, k.getContentValues());
                    long idandroid = Long.parseLong(urinsercion.getLastPathSegment());
                    k.setId(idandroid);
                    if (internetEnabled()) {
                        HiloSubidaNotas a = new HiloSubidaNotas();
                        a.execute();
                    }
                    break;
                case EDITAR:
                    b=data.getExtras();
                    k=b.getParcelable("nota");
                    int pos=b.getInt("pos");
                    listaNotas.set(pos,k);
                    String[] argumentos = {k.getId() + ""};
                    notifyDataChanged();
                    if (internetEnabled()) {
                        ActualizaAsync a = new ActualizaAsync(k);
                        a.execute();
                    }
                    getContentResolver().update(Contrato.TablaNota.CAMBIARESTADO, k.getContentValues(), null, argumentos);
                    break;
            }
        }
    }

    private void sincronizar(){
        HiloSincronizacion sincronizacion= new HiloSincronizacion();
        sincronizacion.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addy) {
            añadirNota();
            return true;
        }else if (id== R.id.action_search){
            sincronizar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyDataChanged() {cl.notifyDataSetChanged();
    }

    private class HiloSubidaNotas extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            listaNotas = servidor.subirNotas(listaNotas, user);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataChanged();
            cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
            lv.setAdapter(cl);
        }
    }
    private class ActualizaAsync extends AsyncTask<Void, Void, Void> {
        Keep k;

        public ActualizaAsync(Keep k) {
            this.k = k;
        }

        @Override
        protected Void doInBackground(Void... params) {
            servidor.actualizar(k);
            String[] argumentos = {k.getId() + ""};
            getContentResolver().update(Contrato.TablaNota.CAMBIARESTADO, k.getContentValues(), null, argumentos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataChanged();
            cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
            lv.setAdapter(cl);
        }
    }

    private class HiloSincronizacion extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<List> notasServer = servidor.consultarNotas(user);
            listaNotasBD=notasServer.get(0);
            List<Keep> notasServerBorrar = notasServer.get(1);
            List<Keep> fusionNotas= new ArrayList<>();
            fusionNotas.addAll(listaNotasBD);
            List<Keep> actualizaID=new ArrayList<>();

            for (Keep k: notasServerBorrar){
                String[] argumentos = {k.getId() + ""};
                getContentResolver().delete(Contrato.TablaNota.CAMBIARESTADO, null, argumentos);
            }
            for (Keep k:listaNotas){
                if(!listaNotasBD.contains(k) && k.getEstado() == 1) {
                        String[] argumentos = {k.getId() + ""};
                        getContentResolver().delete(Contrato.TablaNota.CAMBIARESTADO, null, argumentos);
                }
            }
            for(Keep k: listaNotasBD){
                if(k.getId()==-1){
                    Uri uri=getContentResolver().insert(Contrato.TablaNota.CONTENT_URI,k.getContentValues());
                    long id= Long.parseLong(uri.getLastPathSegment());
                    k.setId(id);
                    actualizaID.add(k);
                }else{
                    if(!listaNotas.contains(k)){
                        Uri uri=getContentResolver().insert(Contrato.TablaNota.CONTENT_URI,k.getContentValues());
                    }
                }
            }
            if(actualizaID.size()!=0){
                servidor.actualizarIdAndroid(actualizaID, user);
            }

            for(Keep k: listaNotas){
                if(k.getEstado()==0){
                    fusionNotas.add(k);
                }
            }
            listaNotas = fusionNotas;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataChanged();
            HiloSubidaNotas ak= new HiloSubidaNotas();
            ak.execute();
        }
    }

    public boolean internetEnabled() {
        ConnectivityManager m = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean is3g = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWiFi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (is3g || isWiFi) {
            return true;
        }else {
            return false;
        }
    }


}
