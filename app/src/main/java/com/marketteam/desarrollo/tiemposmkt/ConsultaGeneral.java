package com.marketteam.desarrollo.tiemposmkt;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class ConsultaGeneral {
    String dbname;

    public ConsultaGeneral(Context contexto,String databasename){
        dbname = databasename;
        contexto = contexto;
        OperacionesBDInterna operaciones = new OperacionesBDInterna(contexto,databasename);
    }
    //Realizar una consulta con raw y devolver un objeto
    public ArrayList<String> [] queryObjeto(Context cont, String SQLQuery, String [] whereV){
        OperacionesBDInterna operaciones = new OperacionesBDInterna(cont,dbname);
        operaciones.open();
        Cursor c = operaciones.rawQuery(SQLQuery,whereV);
        int sizeG = c.getCount();
        if(sizeG == 0){
            return null;
        }
        ArrayList<String> [] objeto = new ArrayList[sizeG];
        for (int n = 0; n < sizeG; n++){
            objeto[n] = new ArrayList<>();
        }
        int ind = 0;
        if(sizeG > 0){
            c.moveToFirst();
            do{
                int size = c.getColumnCount();
                for (int k = 0; k < size; k++){
                    //Por cada columna, campo, que lo guarde en el respectivo objeto
                    objeto[ind].add(c.getString(k));
                }
                ind++;
            }while(c.moveToNext());
        } else {
            Toast.makeText(cont, "No existen registros para esa consulta", Toast.LENGTH_SHORT).show();
        }
        operaciones.close();
        return objeto;
    }

    public ArrayList<String>[] queryGeneral(Context cont, String TABLE_NAME, String [] cols, String like){
        OperacionesBDInterna operaciones = new OperacionesBDInterna(cont,dbname);
        operaciones.open();
        Cursor c = operaciones.query(false,TABLE_NAME,cols,like,null,null,null,null,"30");
        int sizeG = c.getCount();
        if(sizeG == 0){
            return null;
        }
        ArrayList<String> [] objeto = new ArrayList[sizeG];
        for (int n = 0; n < sizeG; n++){
            objeto[n] = new ArrayList<>();
        }
        int ind = 0;
        if(sizeG > 0){
            c.moveToFirst();
            do{
                int size = c.getColumnCount();
                for (int k = 0; k < size; k++){
                    //Por cada columna, campo, que lo guarde en el respectivo objeto
                    objeto[ind].add(c.getString(k));
                }
                ind++;
            }while(c.moveToNext());
        } else {
            Toast.makeText(cont, "No existen registros para esa consulta", Toast.LENGTH_SHORT).show();
        }
        operaciones.close();
        return objeto;
    }
    public ArrayList<String> [] queryObjeto2val(Context cont, String SQLQuery, String [] whereV){
        OperacionesBDInterna operaciones = new OperacionesBDInterna(cont,dbname);
        operaciones.open();
        Cursor c = operaciones.rawQuery(SQLQuery,whereV);
        int sizeG = c.getCount();
        if(sizeG == 0){
            return null;
        }
        ArrayList<String> [] objeto = new ArrayList[sizeG];
        for (int n = 0; n < sizeG; n++){
            objeto[n] = new ArrayList<>();
        }
        int ind = 0;
        if(sizeG > 0){
            c.moveToFirst();
            do{
                int size = c.getColumnCount();
                for (int k = 0; k < size; k++){
                    //Por cada columna, campo, que lo guarde en el respectivo objeto
                    objeto[ind].add(c.getString(k));
                }
                ind++;
            }while(c.moveToNext());
        } else {
            Toast.makeText(cont, "No existen registros para esa consulta", Toast.LENGTH_SHORT).show();
        }
        operaciones.close();
        return objeto;
    }
}