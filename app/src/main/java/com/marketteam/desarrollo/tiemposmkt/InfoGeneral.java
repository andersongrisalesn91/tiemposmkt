package com.marketteam.desarrollo.tiemposmkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class InfoGeneral extends AppCompatActivity {

    RecyclerView recyclerInfoGIni;
    ArrayList<InfoGModel> lista;
    ConsultaGeneral cGeneral;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    ActualizarTablas at;
    static InfoGeneral activityA;
    String DB_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_general);
        activityA = this;
    }

    class InfoG extends RecyclerView.ViewHolder{
        TextView audit;
        TextView cli;
        TextView estab;
        public InfoG(View itemView){
            super(itemView);
            this.audit = (TextView) itemView.findViewById(R.id.tvRetIdAuditoria);
            this.cli = (TextView) itemView.findViewById(R.id.tvRetIdCliente);
            this.estab = (TextView) itemView.findViewById(R.id.tvRetEstab);
        }
    }

    class InfoGModel{
        String audit, cli, estab;
        public InfoGModel(String audit, String cliente, String establec){
            this.audit = audit;
            this.cli = cliente;
            this.estab = establec;
        }
    }

    class InfoGAdapter extends RecyclerView.Adapter<InfoG>{
        ArrayList<InfoGModel> lista;

        public  InfoGAdapter(ArrayList<InfoGModel> lista){this.lista = lista;}

        @Override
        public InfoG onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InfoG(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_auditoria,parent,false));
        }

        @Override
        public void onBindViewHolder(InfoG holder, int position) {
            final InfoGModel opcion = lista.get(position);
            holder.audit.setText(opcion.audit);
            holder.cli.setText(opcion.cli);
            holder.estab.setText(opcion.estab);
            //Poner opcion.cli en PA where  VA='cliA'
            final String cliente = opcion.cli;
            final String encuesta = opcion.audit;
            holder.audit.setOnClickListener(new View.OnClickListener() {
                @Override
               public void onClick(View v) {  }
            });
            holder.cli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  }
            });
            holder.estab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  }
            });
        }

        @Override
        public int getItemCount() { return lista.size(); }
    }

    @Override
    protected void onStart() {
        super.onStart();

        cGeneral = new ConsultaGeneral(getBaseContext(),"tiemposmkt.db");
        operaciones = new OperacionesBDInterna(getBaseContext(),"tiemposmkt.db");
        at = new ActualizarTablas(getBaseContext(),"tiemposmkt.db");
        fg = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        fg.ultimaPantalla("InfoGeneral");
        lista = new ArrayList<>();

        //Mostrar encuestas con estado Aplazado
        ArrayList<String>[] objAudi;
        String queryTipoM = "SELECT VAL FROM ACT WHERE VA='TIPOM'";
        ArrayList<String>[] tipox = cGeneral.queryObjeto2val(getBaseContext(), queryTipoM, null);
        String tipom = tipox[0].get(0);
        if(tipom.equals("1")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) < 4 AND CAST(maestro1 AS INT)<1000");
        } else if(tipom.equals("3")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT)=1000");
        }else if(tipom.equals("4")){
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) < 4 AND CAST(maestro1 AS INT)>1000");
        } else {
            objAudi = cGeneral.queryGeneral(getBaseContext(),"t1",new String[]{"encuesta","cliente_t","maestro3"},"estado='2' AND CAST(maestro8 AS INT) > 3 AND CAST(maestro8 AS INT) < 1000");
        }
        if(objAudi != null){
            for(int a = 0; a < objAudi.length; a++){
                String encuesta = objAudi[a].get(0);
                String cliente = objAudi[a].get(1);
                String establecimiento = objAudi[a].get(2);
                InfoGModel mm = new InfoGModel(encuesta,cliente,establecimiento);
                lista.add(mm);
            }
            InfoGAdapter ma = new InfoGAdapter(lista);
            recyclerInfoGIni.setAdapter(ma);
        } else {
            Toast.makeText(getBaseContext(),"No hay encuestas aplazadas", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void AnadirPunto(View v){
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popUp = inflater.inflate(R.layout.popup_cliente,null);
        final EditText observ = (EditText) popUp.findViewById(R.id.textoPass);
        Button desc = (Button) popUp.findViewById(R.id.BTCancelar);
        Button guardar = (Button) popUp.findViewById(R.id.BTContinuar);
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = observ.getText().toString();
                if((texto.equals("")) || (texto == null) || (texto.length() < 4)){
                    //Texto inválido
                    Toast.makeText(getBaseContext(),"El Codigo Ingresado no es valido", Toast.LENGTH_LONG).show();
                } else {
                    //Guardar observaciones
                    //Actualizar campo
                    operaciones.queryNoData("UPDATE t1t SET observaciones='" + texto + "'");
                    operaciones.queryNoData("UPDATE ACT SET VAL='1' where VA='OBSV'");

                    try {
                        String queryCli = "SELECT VAL FROM ACT WHERE VA=?";
                        ArrayList<String>[] objeto = cGeneral.queryObjeto(getBaseContext(),queryCli,new String[]{"PANT"});

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }





    public void MenuLateral(View vista) {
        fg.MenuLateral(vista);
    }
    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }

    public void validadinfo(View vista) {
        EditText ruta = (EditText) findViewById(R.id.ETCMerc);
        EditText nmercaderista = (EditText) findViewById(R.id.ETNMerc);
        ImageButton IBApunto = (ImageButton) findViewById(R.id.IBAgregarPunto);
        TextView TVApunto = (TextView) findViewById(R.id.TVPuntos);
        String rutat = ruta.getText().toString();
        String queryCli = "";
        try {
            queryCli = fg.getQ1("SELECT nomerc FROM mercaderistas WHERE idruta='" + rutat + "'");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Ruta no Asignada a ningun mercaderista",Toast.LENGTH_SHORT);
        }
        finally {

            nmercaderista.setText(queryCli);
            at.actualizarCTUR("principalt","ruta",rutat);
            at.actualizarCTUR("principalt","mercaderista",queryCli);
            Toast.makeText(getBaseContext(),"Informacion Valida, continue ingresando clientes",Toast.LENGTH_LONG);

        }
    }



    public void Volver(View v){
        startActivity(new Intent(this,Login.class));
    }


    public static InfoGeneral getInstance(){ return activityA; }
}