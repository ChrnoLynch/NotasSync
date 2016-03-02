package com.example.lprub.notassync.actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lprub.notassync.R;
import com.example.lprub.notassync.pojo.Keep;

public class Nota extends AppCompatActivity {
    private Keep k;
    private EditText et;
    private Intent i;
    private Bundle b;
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_keep);
        et=(EditText)findViewById(R.id.etNota);
        i=getIntent();
        b=i.getExtras();
        k=b.getParcelable("nota");
        if(k!=null){
            pos=b.getInt("pos");
            et.setText(k.getContenido());
        }
    }
    public void aceptar(View v){
        if(k!=null){
            k.setContenido(et.getText().toString());
            b.putParcelable("nota",k);
            b.putInt("pos",pos);
            i.putExtras(b);
            setResult(RESULT_OK,i);
            finish();
        }else{
            k=new Keep();
            k.setContenido(et.getText().toString());
            b.putParcelable("nota",k);
            i.putExtras(b);
            setResult(RESULT_OK,i);
            finish();
        }
    }
    public void cancelar(View v){
        setResult(RESULT_CANCELED);
        finish();
    }
}
