package com.example.lprub.notassync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.lprub.notassync.R;
import com.example.lprub.notassync.pojo.Keep;

import java.util.List;

public class ClaseAdaptador extends ArrayAdapter<Keep>{


    private Context ctx;
    private int res;
    private LayoutInflater lInflator;
    private List<Keep> valores;

    static class ViewHolder {
        public TextView tv1, tv2;
    }

    public ClaseAdaptador(Context context, int resource, List<Keep> objects) {
        super(context, resource, objects);
        this.ctx = context;//actividad
        this.res = resource;//layout del item
        this.valores = objects;//lista de valores
        this.lInflator = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean borrar(int position) {
        try {
            valores.remove(position);
            this.notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1
        ViewHolder gv = new ViewHolder();
        if(convertView==null){
            convertView = lInflator.inflate(res, null);
            TextView tv = (TextView) convertView.findViewById(R.id.tvContenido);
            gv.tv1 = tv;
            tv = (TextView) convertView.findViewById(R.id.tvSinc);
            gv.tv2 = tv;

            convertView.setTag(gv);
        } else {
            gv = (ViewHolder) convertView.getTag();
        }
        gv.tv1.setText(valores.get(position).getContenido().replace("|"," "));
        gv.tv2.setText(valores.get(position).getEstado()+"");
        return convertView;
    }


}