package com.marketteam.desarrollo.tiemposmkt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ConsultaInsert {
    //Falta otro array con las columnas en las que se va a insertar
    public void insert(Context context, ArrayList<String> values, String TABLE_NAME) throws Exception{
        OperacionesBDInterna oper = new OperacionesBDInterna(context,null);
        //Mirar la cantidad de columnas de la tabla - l
        //Partir values por esa cantidad
        oper.open();
        ArrayList<String> columnasT = new ArrayList<>();
        String selectQuery = "SELECT * FROM COLUMNS WHERE tabla= ? ";
        Cursor cols = oper.rawQuery(selectQuery, new String[]{TABLE_NAME});
        if(cols.getCount() > 0){
            cols.moveToFirst();
            int i = 0;
            do{
                columnasT.add(cols.getString(i));
                i++;
            } while(cols.moveToNext());
        }

        int l = columnasT.size();
        ArrayList<String> valuesIns = new ArrayList<>();
        String temp = "";
        int cont = 0;
        for (String val: values) {
            if(cont == (l - 1)){
                //Agregar al content values
                temp += val;
                valuesIns.add(temp);
                cont = 0;
                temp = "";

            } else {
                temp += val + ",";
                cont++;
            }
        }
        ContentValues cv;
        StringTokenizer st;
        for (String valIns: valuesIns) {
            st = new StringTokenizer(valIns, ",");
            cv = new ContentValues();
            for (int j = 0; j < l; j++){
                cv.put(columnasT.get(j), st.nextToken());
            }
            boolean exito = oper.insertar(TABLE_NAME, cv);
            if(!exito){
                Toast.makeText(context, "No se pudo insertar el registro", Toast.LENGTH_SHORT).show();
            }
        }
        oper.close();
    }
}