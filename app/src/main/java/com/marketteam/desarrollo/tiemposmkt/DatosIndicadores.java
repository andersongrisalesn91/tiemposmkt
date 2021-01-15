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
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DatosIndicadores extends AppCompatActivity {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    String idU, flujo, queryActVal,NivelP,SNivelP,IDPVM,IDENP,csniv,capitulo,indicador,codigo;
    String DB_NAME;
    RecyclerView recyclerDatos;
    RelativeLayout relativeL;
    ArrayList<DatosModel> lista;
    String[] opciones, evaluados,codcapt,codindic,nomindic,valindic,metaindic,pbindic,nvindic,snvindic, idpvm, proyecto, nproy, visita, ddatos, idemp, imgproy, nomemp, imgemp;
    Integer canitems = 0, actorden = 0, cvaltend = 0, cvaltendr = 0;
    Button fin;
    static DatosIndicadores activityA;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_indicadores);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        if (DatosPrincipal.getInstance() != null) {
            DatosPrincipal.getInstance().finish();
        }
        FuncionesGenerales fgx = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        DB_NAME = fgx.getQ1("select va from 'PA' where pa='ndb'");
        fgx.ultimaPantalla("DatosIndicadores");

    }
    @Override
    protected void onResume(){
        super.onResume();
        fg = new FuncionesGenerales(getBaseContext(),DB_NAME);
        operaciones = new OperacionesBDInterna(getBaseContext(),DB_NAME);
        conGen = new ConsultaGeneral(getBaseContext(),DB_NAME);
        at = new ActualizarTablas(getBaseContext(),DB_NAME);
        fg.ultimaPantalla("DatosIndicadores");
        idU = fg.UsuarioActual();
        lista = new ArrayList<>();
        relativeL = (RelativeLayout) findViewById(R.id.RLItemsIndicadores);
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
        csniv = fg.getQ1("select count(nv) from flujoind");
        String pvm = fg.getParam("PVMA");
        IDPVM = pvm;
        String nivel = fg.getQ1("select min(nv) from flujo");
        NivelP = nivel;
        String snivel = fg.getQ1("select ifnull(max(snv),0) as msn from flujoind");
        SNivelP = snivel;
        codigo = "0";
        String nombre = "0";
        Integer valpb = 0;
        String codidc = "0";
        String codidi = "0";

        codigo = fg.getQ1("select iden from flujo where pvm=" + pvm + " and nv=" + nivel);
        String sqlnombre = "select val from etq where pvm=" + pvm + " and nv=" + nivel + " and ord=0 and cod='" + codigo + "'";
        String nombrex = fg.getQ1(sqlnombre);
        IDENP = codigo;
        if (csniv.equals("0")) {
            nombre = fg.getQ1("select val from etq where pvm=" + pvm + " and nv=" + nivel + " and ord=0");
            String totalp = fg.getQ1("select porc from appal where pvm=" + pvm + " and nv=" + nivel + " and us0='0' and idc=1 and idi=1 ");
            String metap = fg.getQ1("select mval from appal where pvm=" + pvm + " and nv=" + nivel + " and us0='0' and idc=1 and  idi=1 ");
            String total = twoDForm.format((Double.parseDouble(totalp)) * 100) + "";
            String meta = twoDForm.format((Double.parseDouble(metap)) * 100) + "";
            valpb = Integer.parseInt(fg.getQ1("select valp from appal where pvm=" + pvm + " and nv=" + nivel + " and us0='0' and idc=1 and  idi=1 "));
            VT.setText(total);
            if (meta.equals("0")) {
                LM.setVisibility(View.GONE);
                VM.setVisibility(View.GONE);
                RelativeLayout rld = (RelativeLayout) findViewById(R.id.RLValores);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 20, 0, 0);
                LT.setLayoutParams(params);
                LO3.setVisibility(View.GONE);
                LO4.setVisibility(View.GONE);
                RO3.setVisibility(View.GONE);
                RO4.setVisibility(View.GONE);
            } else {
                VM.setText(meta);
                LO3.setVisibility(View.GONE);
                LO4.setVisibility(View.GONE);
                RO3.setVisibility(View.GONE);
                RO4.setVisibility(View.GONE);
            }
        } else {
            LO3.setVisibility(View.GONE);
            LO4.setVisibility(View.GONE);
            RO3.setVisibility(View.GONE);
            RO4.setVisibility(View.GONE);
            codidc= fg.getQ1("select idi from flujoind where pvm=" + pvm + " and nv=" + nivel + " and snv in (select ifnull(max(snv),0) as msn from flujoind)");
            capitulo = codidc;
            indicador = fg.getQ1("select 'C' || idc || ' - I' || idi from flujoind where pvm=" + pvm + " and nv=" + nivel + " and snv in (select ifnull(max(snv),0) as msn from flujoind)");
            IDENP = codigo;
            nombre = fg.getQ1("select val from etqind where pvm=" + pvm + " and nv=" + nivel + " and snv=" + snivel + " and indic=" + codidc );
        }
        if (csniv.equals("0")) {
            IND.setText("INDICADORES");
        } else {
            String nfin = "";
            for (int i = 0;i < Integer.parseInt(csniv); i++) {
                String capi = fg.getQ1("select idc from flujoind where pvm=" + pvm + " and nv=" + nivel + " and snv=" + i );
                String indi = fg.getQ1("select idi from flujoind where pvm=" + pvm + " and nv=" + nivel + " and snv=" + i );
                String nombre_rutasn = fg.getQ1("select val from etqind where pvm=" + pvm + " and nv=" + nivel + " and snv=" + i + " and capit=" + capi+ " and indic=" + indi);
                if (i == 0) {
                    nfin =  nombre_rutasn;
                }else{
                    nfin = nfin + " > " + nombre_rutasn;
                }
            }
            IND.setText(nfin.toUpperCase());
        }

        NP.setText(nombrex.toUpperCase());
        CP.setText(codigo.toUpperCase());
        PBD.setProgress(valpb);
        ArrayList<String>[] objIndic;
        objIndic = null;
        if (csniv.equals("0")){
            String sqlindic = "select appal.idc,etqind.val,appal.porc,appal.mval,appal.valp,appal.nv,appal.sn,appal.idi from appal inner join etqind on appal.pvm = etqind.pvm and appal.nv = etqind.nv  and appal.sn = etqind.snv and appal.idc = etqind.capit  and appal.idi = etqind.indic where idc=1 AND idi>1 and us='" + codigo + "'";
            objIndic = conGen.queryObjeto2val(getBaseContext(), sqlindic, null);
        }else{
            String sqlindic = "select appal.idc,etqind.val,appal.porc,appal.mval,appal.valp,appal.nv,appal.sn,appal.idi from appal inner join etqind on appal.pvm = etqind.pvm and appal.nv = etqind.nv  and appal.sn = etqind.snv and appal.idc = etqind.capit  and appal.idi = etqind.indic where idc=" + capitulo + " AND idi>0 and us='" + codigo + "'";
            objIndic = conGen.queryObjeto2val(getBaseContext(), sqlindic, null);
        }
        if (objIndic != null) {
            codcapt =  new String[objIndic.length];
            codindic = new String[objIndic.length];
            nomindic = new String[objIndic.length];
            valindic = new String[objIndic.length];
            metaindic = new String[objIndic.length];
            pbindic = new String[objIndic.length];
            nvindic = new String[objIndic.length];
            snvindic = new String[objIndic.length];
            for (int op = 0; op < objIndic.length; op++) {
                codcapt[op] = objIndic[op].get(0);
                nomindic[op] = objIndic[op].get(1);
                valindic[op] = twoDForm.format((Double.parseDouble(objIndic[op].get(2)))*100) + "";
                metaindic[op] = twoDForm.format((Double.parseDouble(objIndic[op].get(3)))*100) + "";
                pbindic[op] = objIndic[op].get(4);
                nvindic[op] = objIndic[op].get(5);
                snvindic[op] = objIndic[op].get(6);
                codindic[op] = objIndic[op].get(7);
                DatosModel model = null;
                if (csniv.equals("0")){
                    model = new DatosModel(pbindic[op], valindic[op], metaindic[op], nomindic[op], codcapt[op]);
                }else {
                    model = new DatosModel(pbindic[op], valindic[op], metaindic[op], nomindic[op], codcapt[op] + " - " + codindic[op]);
                }
                lista.add(model);
                canitems++;
            }
            DatosAdapter ma = new DatosAdapter(lista);
            recyclerDatos.setAdapter(ma);
        }

    }

    public void irOpcion(int pos, Datos holder2) {
        String sqlcsn = "";
        if (csniv.equals("0")){
            sqlcsn = "select count(pvm)as cni from appal where idc=" + codindic[pos] + " AND idi>1 and us=" + codigo ;
        }else {
            sqlcsn = "select count(pvm)as cni from appal where idc=" + codindic[pos] + " AND idi>1 and us=" + codigo ;
        }
        String objCsn = fg.getQ1(sqlcsn);
        if (objCsn.equals("0")){
            Toast.makeText(getBaseContext(),"No hay Niveles inferiores definidos para su seleccion",Toast.LENGTH_LONG).show();
        } else {
            try {
                fg.insSNI(IDPVM, nvindic[pos], snvindic[pos], codcapt[pos], codindic[pos]);
            }finally {
                Intent intent = new Intent(this, DatosIndicadores.class);
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
    public void volver(View v) {
        if (Integer.parseInt(csniv)>0){
            fg.eliSNI();
            Intent intent = new Intent(this, DatosIndicadores.class);
            startActivity(intent);
        }else{
            fg.eliNI();
            Intent intent = new Intent(this, DatosPrincipal.class);
            startActivity(intent);
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

    public static DatosIndicadores getInstance() {
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