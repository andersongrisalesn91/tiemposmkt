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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DatosPrincipal extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idU, flujo, queryActVal,NivelP,IDPVM,IDENP,cniv;
    String DB_NAME= "tiemposmkt.db";
    RecyclerView recyclerDatos;
    RelativeLayout relativeL;
    ArrayList<DatosModel> lista;
    String[] opciones, evaluados,codhijos,nomhijos,valhijos,metahijos,pbhijos,nvhijos, idpvm, proyecto, nproy, visita, ddatos, idemp, imgproy, nomemp, imgemp;
    Integer canitems = 0, actorden = 0, cvaltend = 0, cvaltendr = 0;
    Button fin;
    static DatosPrincipal activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_principal);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        fg = new FuncionesGenerales(getBaseContext(),DB_NAME);
        operaciones = new OperacionesBDInterna(getBaseContext(),DB_NAME);
        conGen = new ConsultaGeneral(getBaseContext(),DB_NAME);
        at = new ActualizarTablas(getBaseContext(),DB_NAME);
        fg.ultimaPantalla("DatosPrincipal");
        idU = fg.UsuarioActual();
        lista = new ArrayList<>();
        relativeL = (RelativeLayout) findViewById(R.id.RLPuntos);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ver_recycler, null);
        recyclerDatos = (RecyclerView) v.findViewById(R.id.RVGenerico);
        relativeL.addView(v);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerDatos.setLayoutManager(llm);
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        TextView LT = (TextView)  findViewById(R.id.TVlabelCT);
        TextView VT = (TextView)  findViewById(R.id.TVValorCT);
        TextView LM = (TextView)  findViewById(R.id.TVLabelMeta);
        TextView VM = (TextView)  findViewById(R.id.TVValorMeta);
        TextView NP = (TextView)  findViewById(R.id.TVNombreNivelPrincipal);
        TextView CP = (TextView)  findViewById(R.id.TVCodigoNivelPrincipal);
        TextView LO2 = (TextView)  findViewById(R.id.TVLabelOrden2);
        TextView LO3 = (TextView)  findViewById(R.id.TVLabelOrden3);
        TextView LO4 = (TextView)  findViewById(R.id.TVLabelOrden4);
        TextView RO2 = (TextView)  findViewById(R.id.TVROrden2);
        TextView RO3 = (TextView)  findViewById(R.id.TVROrden3);
        TextView RO4 = (TextView)  findViewById(R.id.TVROrden4);
        TextView IND = (TextView)  findViewById(R.id.TVIndicador);
        ProgressBar PBD = (ProgressBar)  findViewById(R.id.PBDatos);

        String pvm = fg.getParam("PVMA");
        IDPVM = pvm;
        String nivel = fg.getQ1("select min(nv) from flujo");
        NivelP = nivel;
        cniv = fg.getQ1("select count(nv) from flujo");
        String codigo = fg.getQ1("select iden from flujo where pvm=" + pvm + " and nv=" + nivel);
        IDENP = codigo;
        String nombre = fg.getQ1("select val from etq where pvm=" + pvm + " and nv=" + nivel+ " and ord=0 and cod='" + codigo + "'");
        String totalp = fg.getQ1("select porc from appal where pvm=" + pvm + " and nv=" +  nivel+" and us0='0' and idc=1 and idi=1");
        String metap =  fg.getQ1("select mval from appal where pvm=" + pvm + " and nv=" + nivel + " and us0='0' and idc=1 and idi=1");
        String total = twoDForm.format((Double.parseDouble(totalp))*100) + "";
        String meta = twoDForm.format((Double.parseDouble(metap))*100) + "";
        Integer valpb = Integer.parseInt(fg.getQ1("select valp from appal where pvm=" + pvm + " and nv=" + nivel + " and us0='0' and idc=1 and idi=1"));
        IND.setText("CALIFICACIÓN TOTAL");
        VT.setText(total);
        if (meta.equals("0")){
            LM.setVisibility(View.GONE);
            VM.setVisibility(View.GONE);
            RelativeLayout rld = (RelativeLayout) findViewById(R.id.RLValores);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,20,0,0);
            LT.setLayoutParams(params);
            LO3.setVisibility(View.GONE);
            LO4.setVisibility(View.GONE);
            RO3.setVisibility(View.GONE);
            RO4.setVisibility(View.GONE);
        }else{
            VM.setText(meta);
            LO3.setVisibility(View.GONE);
            LO4.setVisibility(View.GONE);
            RO3.setVisibility(View.GONE);
            RO4.setVisibility(View.GONE);
        }

        NP.setText(nombre.toUpperCase());
        CP.setText(codigo.toUpperCase());
        PBD.setProgress(valpb);
        ArrayList<String>[] objHijos;
        objHijos = null;
        if (nivel.equals("0")){
            String sqlhijos = "select us,val,porc,mval,valp,appal.nv from appal inner join etq on appal.pvm = etq.pvm and appal.nv=etq.nv and appal.us=etq.cod where appal.pvm=" + pvm + " and appal.nv=" + (Integer.parseInt(nivel)-1) + " and us1='" + codigo + "' and us0='0' and idc=1 and  idi=1 group by us,porc,mval,valp order by porc ASC";
            objHijos = conGen.queryObjeto2val(getBaseContext(), sqlhijos, null);
        }else{
            String sqlhijos = "select us,val,porc,mval,valp,appal.nv from appal inner join etq on appal.pvm = etq.pvm and appal.nv=etq.nv and appal.us=etq.cod where appal.pvm=" + pvm + " and appal.nv=" + (Integer.parseInt(nivel)-1) + " and us1='" + codigo + "' and us0='0' and idc=1 and  idi=1 group by us,porc,mval,valp order by porc ASC";
            objHijos = conGen.queryObjeto2val(getBaseContext(), sqlhijos, null);
        }
        if (objHijos != null && !NivelP.equals("0")) {
            codhijos = new String[objHijos.length];
            nomhijos = new String[objHijos.length];
            valhijos = new String[objHijos.length];
            metahijos = new String[objHijos.length];
            pbhijos = new String[objHijos.length];
            nvhijos = new String[objHijos.length];
            for (int op = 0; op < objHijos.length; op++) {
                codhijos[op] = objHijos[op].get(0);
                nomhijos[op] = objHijos[op].get(1);
                valhijos[op] = twoDForm.format((Double.parseDouble(objHijos[op].get(2)))*100) + "";
                metahijos[op] = twoDForm.format((Double.parseDouble(objHijos[op].get(3)))*100) + "";
                pbhijos[op] = objHijos[op].get(4);
                nvhijos[op] = objHijos[op].get(5);
                DatosModel model = new DatosModel(pbhijos[op], valhijos[op], metahijos[op],nomhijos[op],codhijos[op]);
                lista.add(model);
                canitems++;
            }
            DatosAdapter ma = new DatosAdapter(lista);
            recyclerDatos.setAdapter(ma);
        }

    }

    @Override
    public void onBackPressed() {
    }


    class Datos extends RecyclerView.ViewHolder {
        ProgressBar pb;
        TextView valt;
        TextView valv;
        TextView nitem;
        TextView citem;

        public Datos(View itemView) {
            super(itemView);
            this.pb = (ProgressBar)  itemView.findViewById(R.id.PBItemLista);
            this.valt = (TextView) itemView.findViewById(R.id.TVValorHijoTotal);
            this.valv = (TextView) itemView.findViewById(R.id.TVValorHijoVersus);
            this.nitem = (TextView) itemView.findViewById(R.id.TVNomItem);
            this.citem = (TextView) itemView.findViewById(R.id.TVCodItem);
        }
    }

    class DatosModel {
        //ImageView ivemp;
        String vpb;
        String valtot;
        String valver;
        String nomitem;
        String coditem;
        //public DatosModel(ImageView iemp, String nproy, String ndatos) {
        public DatosModel( String valorpb, String valortot, String valorver, String nombreitem,String codigoitem) {
            this.vpb = valorpb;
            this.valtot = valortot;
            this.valver = valorver;
            this.nomitem = nombreitem;
            this.coditem = codigoitem;
        }
    }

    class DatosAdapter extends RecyclerView.Adapter<Datos> {
        ArrayList<DatosModel> lista;

        public DatosAdapter(ArrayList<DatosModel> lista) {
            this.lista = lista;
        }

        @Override
        public Datos onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Datos(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_itemlista, parent, false));
        }

        @Override
        public void onBindViewHolder(Datos holder, final int position) {
            final DatosModel opcion = lista.get(position);
            final Datos holder2 = holder;
            Integer valprogb = Integer.parseInt(opcion.vpb);
            holder.pb.setProgress(valprogb);
            holder.valt.setText(opcion.valtot);
            holder.valv.setText(opcion.valver);
            holder.nitem.setText(opcion.nomitem);
            holder.citem.setText(opcion.coditem);
            //Si está evaluado, poner chulito
            holder.pb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.valt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.valv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.nitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irOpcion(position, holder2);
                }
            });
            holder.citem.setOnClickListener(new View.OnClickListener() {
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
        if (Login.getInstance() != null) {
            Login.getInstance().finish();
        }
        if (SelecMediciones.getInstance() != null) {
            SelecMediciones.getInstance().finish();
        }
        if (DatosIndicadores.getInstance() != null) {
            DatosIndicadores.getInstance().finish();
        }
        FuncionesGenerales fgx = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        DB_NAME = fgx.getQ1("select va from 'PA' where pa='ndb'");
        fgx.ultimaPantalla("DatosPrincipal");
    }

    public void irOpcion(int pos, Datos holder2) {
        try {
            fg.insNI(IDPVM, nvhijos[pos], codhijos[pos]);
        }catch (Exception exq){
            System.out.println("Error al insertar registro");
        }finally {
            if (nvhijos[pos].equals("0") || cniv.equals(NivelP) || Integer.parseInt(fg.getParam("mnd")) == Integer.parseInt(cniv)) {
                fg.limpiaSNI();
                Intent intent = new Intent(this, DatosIndicadores.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DatosPrincipal.class);
                startActivity(intent);
            }
        }

    }

    public void revision() {
        boolean finalizar = true;
        if (evaluados != null && evaluados.length > 0) {
            for (int e = 0; e < canitems; e++) {
                String evalu = evaluados[e];
                String opt = opciones[e];
                if (!evaluados[e].equals("1")) {
                    //0 no evaluado, 1 evaluado
                    finalizar = false;
                }
            }
            if (!finalizar) {
                fin.setVisibility(View.INVISIBLE);
                fin.setEnabled(false);
            } else {
                String queryValy = "SELECT count(COM) as CC FROM 'VALID'  where 'V' || IDV || 'E' || ES || 'C' || CA || 'P' || PRE not in (select 'V' || idv || 'E' || esp || 'C' || cat || 'P' || preg as CV from tv where encuesta='" + fg.getAuditoria() + "' and rt=1) order by 'VALID'.IDV asc";
                ArrayList<String>[] objvaly = conGen.queryObjeto2val(getBaseContext(), queryValy, null);
                String cantval = objvaly[0].get(0);
                if (objvaly != null) {
                    if (cantval.equals("0")) {
                        operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                        fin.setVisibility(View.VISIBLE);
                        fin.setEnabled(true);
                    } else {
                        operaciones.queryNoData("UPDATE ME SET EV='2' WHERE ET='VALIDACIONES'");
                        fin.setVisibility(View.INVISIBLE);
                        fin.setEnabled(false);
                    }
                } else {
                    operaciones.queryNoData("UPDATE ME SET EV='1' WHERE ET='VALIDACIONES'");
                    fin.setVisibility(View.VISIBLE);
                    fin.setEnabled(true);
                }
            }
        }
    }



    public void DatosLateral(View vista) {
        fg.MenuLateral(vista);
    }

    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }

    public void volver(View v) {
        String NivelL = fg.getParam("NIVA");
        if (!cniv.equals("1")){
            fg.eliNI();
            Intent intent = new Intent(this, DatosPrincipal.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, SelecMediciones.class);
            startActivity(intent);
        }
    }

    public static DatosPrincipal getInstance() {
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