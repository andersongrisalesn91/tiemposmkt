package com.marketteam.desarrollo.tiemposmkt;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SelecMediciones extends AppCompatActivity {


    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idU, flujo, queryActVal;
    RecyclerView recyclerMediciones;
    RelativeLayout relativeL;
    ArrayList<MedModel> lista;
    String[] opciones, evaluados, idpvm, proyecto, nproy, visita, medicion, idemp, imgproy, nomemp, imgemp;
    Integer canitems = 0, actorden = 0, cvaltend = 0, cvaltendr = 0;
    Button fin;
    static SelecMediciones activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_mediciones);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
    }


    class Med extends RecyclerView.ViewHolder {
        ImageView imgmedx;
        TextView TVNomProyx;
        TextView TVNomMedx;

        public Med(View itemView) {
            super(itemView);
            this.imgmedx = (ImageView) itemView.findViewById(R.id.IVRVimgMED);
            this.TVNomProyx = (TextView) itemView.findViewById(R.id.TVNomProy);
            this.TVNomMedx = (TextView) itemView.findViewById(R.id.TVNomMed);
        }
    }

    class MedModel {
        //ImageView ivemp;
        String nomproy;
        String nommed;
        //public MedModel(ImageView iemp, String nproy, String nmed) {
        public MedModel( String nproy, String nmed) {
            //this.ivemp = iemp;
            this.nomproy = nproy;
            this.nommed = nmed;
        }
    }

    class MedAdapter extends RecyclerView.Adapter<Med> {
        ArrayList<MedModel> lista;

        public MedAdapter(ArrayList<MedModel> lista) {
            this.lista = lista;
        }

        @Override
        public Med onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Med(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_med, parent, false));
        }

        @Override
        public void onBindViewHolder(Med holder, final int position) {
            final MedModel opcion = lista.get(position);
            final Med holder2 = holder;
            //Importante:
            //Esta parte esta incompleta, ya que el modulo de descarga de imagen no se finalizado
            holder.imgmedx.setImageResource(R.drawable.imagen1);
            holder.TVNomProyx.setText(opcion.nomproy);
            holder.TVNomMedx.setText(opcion.nommed);

            //Si está evaluado, poner chulito
            holder.imgmedx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.TVNomProyx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.TVNomMedx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fg = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        String dbname = fg.getQ1("select va from 'PA' where pa='ndb'");
        conGen = new ConsultaGeneral(getBaseContext(),"tiemposmkt.db");
        at = new ActualizarTablas(getBaseContext(),"tiemposmkt.db");

        fg.ultimaPantalla("Mediciones");
        idU = fg.UsuarioActual();
        lista = new ArrayList<>();
        relativeL = (RelativeLayout) findViewById(R.id.RLItemsMediciones);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ver_recycler, null);
        recyclerMediciones = (RecyclerView) v.findViewById(R.id.RVGenerico);
        relativeL.addView(v);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMediciones.setLayoutManager(llm);
        String queryNomUser = "select val from etq where pvm=1 and nv=1 and ord=0 and cod in (select va from 'PA' where pa='UserA')";
        ArrayList<String>[] objUser = conGen.queryObjeto2val(getBaseContext(), queryNomUser, null);
        String queryMed = "select 'pvm'.pvm, py, npy, vi, me, 'pvm'.emp, img, nemp,imgemp from 'pvm' inner join 'emp' on 'pvm'.emp = 'emp'.emp order by pvm asc";
        ArrayList<String>[] objMed = conGen.queryObjeto2val(getBaseContext(), queryMed, null);
        String nomUsuario = objUser[0].get(0);
        String nomEmpresa = objMed[0].get(7);
        String imgEmpresa = objMed[0].get(8);
        TextView nempx = (TextView)  findViewById(R.id.TVCompania);
        TextView nusrx = (TextView)  findViewById(R.id.TVNombreUsuarioLogeo);
        nempx.setText(nomEmpresa);
        nusrx.setText(nomUsuario);
        ImageView imageViewx= (ImageView) findViewById(R.id.IVLogoEmpresa);
        imageViewx.setImageResource(R.drawable.imagen1);

        if (objMed != null) {
            idpvm = new String[objMed.length];
            proyecto = new String[objMed.length];
            nproy = new String[objMed.length];
            visita = new String[objMed.length];
            medicion = new String[objMed.length];
            idemp = new String[objMed.length];
            imgproy = new String[objMed.length];
            nomemp = new String[objMed.length];
            imgemp = new String[objMed.length];
            for (int op = 0; op < objMed.length; op++) {
                String etiqueta = objMed[op].get(2);
                String etiquetamed = objMed[op].get(4);
                idpvm[canitems] = objMed[op].get(0);
                proyecto[canitems] = objMed[op].get(1);
                nproy[canitems] = objMed[op].get(2);
                visita[canitems] = objMed[op].get(3);
                medicion[canitems] = objMed[op].get(4);
                idemp[canitems] = objMed[op].get(5);
                imgproy[canitems] = objMed[op].get(6);
                nomemp[canitems] = objMed[op].get(7);
                imgemp[canitems] = objMed[op].get(8);
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.imagen1);
                MedModel model = new MedModel(etiqueta, etiquetamed);
                lista.add(model);
                canitems++;
            }
            MedAdapter ma = new MedAdapter(lista);
            recyclerMediciones.setAdapter(ma);
        }
    }

    public void irOpcion(int pos, Med holder2) {
        fg.pvmactual(idpvm[pos]);

        Intent intent = new Intent(this, DatosPrincipal.class);
        startActivity(intent);
    }


    public void MedLateral(View vista) {
        fg.MenuLateral(vista);
    }

    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }

    public static SelecMediciones getInstance() {
        return activityA;
    }


    @Override
    protected void onStop() {
        super.onStop();
        /*        operaciones.close();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*operaciones.close();*/
    }

}