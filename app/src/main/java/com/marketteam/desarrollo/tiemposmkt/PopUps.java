package com.marketteam.desarrollo.tiemposmkt;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.marketteam.desarrollo.tiemposmkt.R;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PopUps extends AppCompatActivity {
    FuncionesGenerales fg;
    OperacionesBDInterna operaciones;
    public void popUpConf(final Context contexto, LayoutInflater inflater, int caso, int pantalla) {
        operaciones = new OperacionesBDInterna(contexto,null);
        fg = new FuncionesGenerales(contexto,"tiemposmkt.db");
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        String tx = "";

        switch (caso) {
            case 1:
                tx = contexto.getString(R.string.conf_espac);
                break;
            case 2:
                tx = contexto.getString(R.string.conf_categ);
                break;
            case 3:
                tx = contexto.getString(R.string.conf_estnd);
                break;
            case 4:
                tx = contexto.getString(R.string.conf_sku);
                break;
            case 5:
                //Cambiar SI por NO en respuesta
                tx = contexto.getString(R.string.conf_borrar_datos);
                break;
            case 6:
                //Volver con ítems sin evaluar
                tx = contexto.getString(R.string.conf_volver);
                break;
            case 7:
                //Cerrar sesión con auditoría en curso
                tx = contexto.getString(R.string.cerrarses);
                break;
            case 8:
                //Cerrar sesión con auditoría en curso
                tx = contexto.getString(R.string.regresar);
                break;
            case 9:
                //Cerrar sesión con auditoría en curso
                tx = "Si Regresa, Borrara el poc creado y debera llenarlo nuevamente.";
                break;
            case 10:
                //Cerrar sesión con auditoría en curso
                tx = "Va a regresar a espacios, recuerde que un espacio no puede tener todas sus categorias en NO";
                break;
            case 11:
                //Cerrar sesión con auditoría en curso
                tx = "para finalizar debe corregir o revisar todas las validaciones";
                break;
        }
        texto.setText(tx);
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        final int c = caso;
        final int p = pantalla;
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hacer proceso dependiendo el módulo
                if (c == 1) {

                } else if (c == 2) {

                } else if (c == 3) {

                } else if (c == 4) {
                    //SKU confirmar si o no
                } else if (c == 5) {

                } else if (c == 6) {
                    //Volver a lo anterior sin tener los ítems evaluados
                    //Guardar en la tabla la información actual si es que no está guardada ya
                    Intent volver = null;

                    volver.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(volver);
                } else if (c == 7) {
                    FuncionesGenerales fg = new FuncionesGenerales(contexto,"tiemposmkt.db");
                    fg.ultimaPantalla("Login");
                    Intent login = new Intent(contexto, Login.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(login);
                } else if (c == 8) {
                    //Volver a lo anterior sin tener los ítems evaluados
                    //Guardar en la tabla la información actual si es que no está guardada ya
                    Intent volver = null;
                    volver.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(volver);
                } else if (c == 9) {

                    String queryElimPoc = "delete from t6t where id=" + fg.getContesp();
                    operaciones.queryNoData(queryElimPoc);
                    Intent volver = null;
                }else if (c == 10) {
                    Intent volver = null;
                }else if (c == 11) {
                    Intent volver = null;
                }

                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void popUpCliente(final Context contexto, LayoutInflater inflater, String[] datos) {

        final OperacionesBDInterna operaciones = new OperacionesBDInterna(contexto,null);
        final ConsultaGeneral cGeneral = new ConsultaGeneral(contexto,"tiemposmkt.db");

        View popUp = inflater.inflate(R.layout.popup_confcliente, null);
        Button ok = (Button) popUp.findViewById(R.id.buttonConfCliente);
        Button cancel = (Button) popUp.findViewById(R.id.buttonAnCliente);
        TextView popId = (TextView) popUp.findViewById(R.id.ConfId);
        TextView popProp = (TextView) popUp.findViewById(R.id.ConfProp);
        TextView popEstb = (TextView) popUp.findViewById(R.id.ConfEstb);
        final String id = datos[0];
        final String prop = datos[1];
        final String estb = datos[2];
        popId.setText(id);
        popProp.setText(prop);
        popEstb.setText(estb);

        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, 500, true);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "SELECT va FROM PA WHERE pa=?";
                ArrayList<String>[] obj = cGeneral.queryObjeto(contexto, query, new String[]{"cliA"});
                if (obj == null) {
                    ContentValues cv = new ContentValues();
                    cv.put("pa", "cliA");
                    cv.put("va", id);
                    boolean insert = operaciones.insertar("PA", cv);
                    if (!insert) {
                        Toast.makeText(contexto, "Registro no insertado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Update al campo cliA
                    ContentValues cv = new ContentValues();
                    cv.put("va", id);
                    boolean update = operaciones.actualizar("PA", cv, "pa='cliA'", null);
                    if (!update) {
                        Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
                    }
                }
                operaciones.queryNoData("UPDATE PA SET va='1' WHERE pa='opc'");
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }
}