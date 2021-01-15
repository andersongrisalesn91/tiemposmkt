package com.marketteam.desarrollo.tiemposmkt;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

public class ConsultaUpdate {

    public void update(Context context, String TABLE_NAME, ContentValues values, String where, String[] whereVals){
        OperacionesBDInterna oper = new OperacionesBDInterna(context,null);
        oper.open();
        //Enviar en values, nombre columna y valor para reemplazar
        boolean exito = oper.actualizar(TABLE_NAME,values,where,whereVals);
        if(!exito){
            Toast.makeText(context, "No se pudo realizar la actualización", Toast.LENGTH_SHORT).show();
        }

        oper.close();
    }
}