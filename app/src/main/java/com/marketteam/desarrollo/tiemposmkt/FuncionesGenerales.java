package com.marketteam.desarrollo.tiemposmkt;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class FuncionesGenerales extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    ConsultaGeneral conGen;
    OperacionesBDInterna operaciones;
    Context contexto;
    File sd,cini;
    String queryActVal,flujo;
    String dbname;

    public FuncionesGenerales(Context contexto,String databasename){
        dbname = databasename;
        this.contexto = contexto;
        this.operaciones = new OperacionesBDInterna(contexto,databasename);
        this.conGen  = new ConsultaGeneral(contexto,databasename);
    }

    public String UsuarioActual(){
        String idC = "";
        String queryCli = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] objeto = conGen.queryObjeto(contexto,queryCli,new String[]{"UserA"});
        if((objeto == null) || (objeto.length < 1)){
            //No existe ????
            return null;
        } else {
            idC = objeto[0].get(0);
        }
        return idC;
    }
    public String clienteActual(){
        String idC = "";
        String queryCli = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] objeto = conGen.queryObjeto(contexto,queryCli,new String[]{"cliA"});
        if((objeto == null) || (objeto.length < 1)){
            //No existe ????
            return null;
        } else {
            idC = objeto[0].get(0);
        }
        return idC;
    }

    public String pantallaactual(){
        String pact = "";
        String queryCli = "SELECT va FROM PA WHERE pa='ultP'";
        ArrayList<String>[] objeto = conGen.queryObjeto2val(contexto,queryCli,null);
        if((objeto == null) || (objeto.length < 1)){
            //No existe ????
            return null;
        } else {
            pact = objeto[0].get(0);
        }
        return pact;
    }

    // Funcion para actualizar la ultima pantalla a la que se ha accedido
    public void ultimaPantalla(String pantalla){
        ContentValues cv = new ContentValues();
        cv.put("va", pantalla);
        boolean update = operaciones.actualizar("PA",cv,"pa=?",new String[]{"ultP"});
        if(!update){
            Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
        }
    }
    public void pvmactual(String idpvm){
        ContentValues cv = new ContentValues();
        cv.put("va", idpvm);
        boolean update = operaciones.actualizar("PA",cv,"pa=?",new String[]{"PVMA"});
        if(!update){
            Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    public void ultimaPantallafta(String pantalla){
        ContentValues cv = new ContentValues();
        cv.put("va", pantalla);
        boolean update = operaciones.actualizar("PA",cv,"pa=?",new String[]{"ultPTA"});
        if(!update){
            Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param n
     * n es la cantidad de parámetros a buscar
     * @return
     */
    public ArrayList<String> existeACT(int n){
        ArrayList<String> datos = new ArrayList<>();
        //No me importa que no exista el campo, porque si existe siempre, sólo que está en cero
        //Siempre se actualiza el campo
        //Al inicio de cada if agregar datos al ArrayList
        if(n == 3){
            //Consulto canal,subcanal y espacio y recursivamente canal y subcanal
            ArrayList<String> [] canalActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='CIUCAN'");
            if(canalActual != null){
                datos.add(canalActual[0].get(0));
                ArrayList<String> [] subcanalActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='SUBC'");
                if(subcanalActual != null){
                    datos.add(subcanalActual[0].get(0));
                    ArrayList<String> [] espActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='ESP'");
                    if(espActual != null){
                        datos.add(espActual[0].get(0));
                    } else {
                        datos.add("");
                    }
                }
            }
        } else if(n == 4){
            //Consulto idt2, categoría y directamente canal,subcanal y espacio
            ArrayList<String> [] idt2Actual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='IDT2'");
            if(idt2Actual != null){
                datos.add(idt2Actual[0].get(0));
            } else {
                datos.add("");
            }
            ArrayList<String> [] canalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CAN'");
            if(canalActual != null){
                datos.add(canalActual[0].get(0));
                ArrayList<String> [] subcanalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='SUBC'");
                if(subcanalActual != null){
                    datos.add(subcanalActual[0].get(0));
                    ArrayList<String> [] espActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='ESP'");
                    if(espActual != null){
                        datos.add(espActual[0].get(0));
                        ArrayList<String> [] catActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CAT'");
                        if(catActual != null){
                            datos.add(catActual[0].get(0));
                        } else {
                            datos.add("");
                        }
                    } else {
                        datos.add("");
                    }
                }
            }
        } else if(n == 5){
            //Consulto idt2, idt3, estándar y directamente canal,subcanal,espacio y categoría
            ArrayList<String> [] idt2Actual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='IDT2'");
            if(idt2Actual != null){
                datos.add(idt2Actual[0].get(0));
            } else {
                datos.add("");
            }
            ArrayList<String> [] idt3Actual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='IDT3'");
            if(idt3Actual != null){
                datos.add(idt3Actual[0].get(0));
            } else {
                datos.add("");
            }
            ArrayList<String> [] canalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CAN'");
            if(canalActual != null){
                datos.add(canalActual[0].get(0));
                ArrayList<String> [] subcanalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='SUBC'");
                if(subcanalActual != null){
                    datos.add(subcanalActual[0].get(0));
                    ArrayList<String> [] espActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='ESP'");
                    if(espActual != null){
                        datos.add(espActual[0].get(0));
                        ArrayList<String> [] catActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CAT'");
                        if(catActual != null){
                            datos.add(catActual[0].get(0));
                            ArrayList<String> [] estndActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='ESTN'");
                            if(estndActual != null){
                                datos.add(estndActual[0].get(0));
                                ArrayList<String> [] ciucan = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='CIUCAN'");
                                if(ciucan != null){
                                    datos.add(ciucan[0].get(0));
                                } else {
                                    datos.add("");
                                }
                            } else {
                                datos.add("");
                            }
                        } else {
                            datos.add("");
                        }
                    } else {
                        datos.add("");
                    }
                }else {
                    datos.add("");
                }
            } else {
                datos.add("");
            }
        } else if(n == 6){
            //Consulto SKU y directamente canal,subcanal,espacio,categoría y estándar
            ArrayList<String> [] idt2Actual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='IDT2'");
            if(idt2Actual != null){
                datos.add(idt2Actual[0].get(0));
            } else {
                datos.add("");
            }
            ArrayList<String> [] idt3Actual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='IDT3'");
            if(idt3Actual != null){
                datos.add(idt3Actual[0].get(0));
            } else {
                datos.add("");
            }
            ArrayList<String> [] canalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CIUCAN'");
            if(canalActual != null){
                datos.add(canalActual[0].get(0));
                ArrayList<String> [] subcanalActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='SUBC'");
                if(subcanalActual != null){
                    datos.add(subcanalActual[0].get(0));
                    ArrayList<String> [] espActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='ESP'");
                    if(espActual != null){
                        datos.add(espActual[0].get(0));
                        ArrayList<String> [] catActual = conGen.queryGeneral(contexto,"'ACT'", new String[]{"VAL"}, "VA='CAT'");
                        if(catActual != null){
                            datos.add(catActual[0].get(0));
                            ArrayList<String> [] estndActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='ESTN'");
                            if(estndActual != null){
                                datos.add(estndActual[0].get(0));
                                ArrayList<String> [] skuActual = conGen.queryGeneral(contexto,"ACT", new String[]{"VAL"}, "VA='SKU'");
                                if(skuActual != null){
                                    datos.add(skuActual[0].get(0));
                                } else {
                                    datos.add("");
                                }
                            } else {
                                datos.add("");
                            }
                        } else {
                            datos.add("");
                        }
                    } else {
                        datos.add("");
                    }
                }
            }
        }
        return datos;
    }

    public String getAuditoria(){
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(contexto,queryAud,new String[]{"AUD"});
        String auditoria = idAuditoria[0].get(0);
        return auditoria;
    }

    public String getCiuCan(){
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idCC = conGen.queryObjeto(contexto,queryAud,new String[]{"CIUCAN"});
        String ciuCan = idCC[0].get(0);
        return ciuCan;
    }

    public String getCan(){
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idC = conGen.queryObjeto(contexto,queryAud,new String[]{"CAN"});
        String Can = idC[0].get(0);
        return Can;
    }
    public String getTipoM(){
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idTM = conGen.queryObjeto(contexto,queryAud,new String[]{"TIPOM"});
        String TIPM = idTM[0].get(0);
        return TIPM;
    }
    public String getContesp(){
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idCE = conGen.queryObjeto(contexto,queryAud,new String[]{"CONESP"});
        String conEsp = idCE[0].get(0);
        return conEsp;
    }

    public void MenuLateral(View vista){
        Context wrapper = new ContextThemeWrapper(contexto, R.style.menu);
        PopupMenu popupMenu= new PopupMenu(wrapper, vista);
        popupMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater=popupMenu.getMenuInflater();
        inflater.inflate(R.menu.lateral,popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            //Casos del switch - return true
//            case R.id.menu1:
//                //Sincronizar datos
//                //Barra de progreso y notificación / Toast de completado
//                SincronizaVF s = new SincronizaVF(contexto);
//                s.sincronizarTablasVF();
//                return true;
            case R.id.menu2:
                AplazarAud();
                return true;

            case R.id.menu3:
                //RechazarAud();
                return true;
            case R.id.menu4:
                operaciones.queryNoData("UPDATE ACT SET VAL='" + pantallaactual() + "' WHERE VA='PANT'");
                return true;
            case R.id.menu7:
                RevisionErr();
                return true;
            case R.id.menu8:
                //Salir de la aplicación en caso de deshabilitar el botón físico de la tablet
                return true;
            case R.id.menu9:
                //Cerrar la sesión del usuario
                //Ir a Login
                ultimaPantalla("Login");

                //Si está en ua auditoría, confirmación de pérdida de datos
                PopUps pop = new PopUps();
                final LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(LAYOUT_INFLATER_SERVICE);
                pop.popUpConf(contexto,inflater,7,14);
                //Si está en ua auditoría, confirmación de pérdida de datos

                return true;
        }
        return false;
    }
    public String fechaActual(int acc){
        String fecha = "";
        if(acc == 1){
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);
        } else if(acc == 2){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            String fechaTemp = dateFormat.format(date);
            StringTokenizer st = new StringTokenizer(fechaTemp);
            String fechaT = st.nextToken();
            String horaT = st.nextToken();
            st = new StringTokenizer(fechaT,"-");
            fecha = st.nextToken() + st.nextToken() + st.nextToken();
            st = new StringTokenizer(horaT,":");
            fecha += st.nextToken() + st.nextToken() + st.nextToken();
        } else if(acc == 3){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);
        }
        return fecha;
    }

    public String nombreEspAct(String idEsp){
        String espacio = "";
        ArrayList<String>[] nombreE = conGen.queryGeneral(contexto,"'108_ESPACIO'",new String[]{"NESP"},"ESP='" + idEsp + "'");
        if(nombreE != null){
            espacio = nombreE[0].get(0);
        }
        return espacio;
    }

    public String nombreCatAct(String idCat){
        String categoria = "";
        ArrayList<String>[] nombreC = conGen.queryGeneral(contexto,"'106_CATEG'",new String[]{"NCAT"},"CAT='" + idCat + "'");
        if(nombreC != null){
            categoria = nombreC[0].get(0);
        }
        return categoria;
    }

    public String getCodProy(){
        String CodProy = "";
        ArrayList<String>[] nombreCP = conGen.queryGeneral(contexto,"'PA'",new String[]{"va"},"pa='cproy'");
        if(nombreCP != null){
            CodProy = nombreCP[0].get(0);
        }
        return CodProy;
    }
    public String getParam(String par){
        String vlp = "";
        ArrayList<String>[] VP = conGen.queryGeneral(contexto,"'PA'",new String[]{"va"},"pa='" + par + "'");
        if(VP != null){
            vlp = VP[0].get(0);
        }
        return vlp;
    }
    public ArrayList<String>[] getQ(String SQL){
        ArrayList<String>[] objV = conGen.queryObjeto2val(contexto, SQL, null);
        return objV;
    }
    public String getQ1(String SQL){
        String va = "";
        ArrayList<String>[] objV = conGen.queryObjeto2val(contexto, SQL, null);
        if(objV != null){
            va = objV[0].get(0);
        }
        return va;
    }

    public String getActivo(){
        String idActivo = "";
        ArrayList<String>[] activo = conGen.queryGeneral(contexto, "ACT", new String[]{"VAL"},"VA='ACTV'");
        if(activo != null){
            idActivo = activo[0].get(0);
        }
        return idActivo;
    }

    public String getSkuCalidad(){
        String idSku = "";
        ArrayList<String>[] sku = conGen.queryGeneral(contexto, "ACT", new String[]{"VAL"},"VA='SKU_C'");
        if(sku != null){
            idSku = sku[0].get(0);
        }
        return idSku;
    }

    public String getordenTipoA(){
        String idO = "";
        ArrayList<String>[] ord = conGen.queryGeneral(contexto, "ACT", new String[]{"VAL"},"VA='ORDEN'");
        if(ord != null){
            idO = ord[0].get(0);
        }
        return idO;
    }
    public String getTipoTipoA(){
        String idO = "";
        ArrayList<String>[] ord = conGen.queryGeneral(contexto, "ACT", new String[]{"VAL"},"VA='FTATID'");
        if(ord != null){
            idO = ord[0].get(0);
        }
        return idO;
    }
    public String getFototom(Integer orden){
        String idO = "";
        String queryftact = "SELECT count(encuesta) as co FROM tft where preg='" + orden + "'";
        ArrayList<String>[] ord = conGen.queryObjeto2val(contexto, queryftact, null);
        if(ord[0].get(0).equals("0")){
            idO = "0";
        }else {
            idO = "1";
        }
        return idO;
    }

    public void AplazarAud(){
        //Aplazar la encuesta
        //PopUp o vista con lugar para explicar por qué se aplaza
        final LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_confirmacion,null);
        TextView texto = (TextView) popUp.findViewById(R.id.textoConfir);
        texto.setText(contexto.getString(R.string.aplazar));
        Button cancel = (Button) popUp.findViewById(R.id.buttonSkuNo);
        cancel.setText("Cancelar");
        Button ok = (Button) popUp.findViewById(R.id.buttonConfSi);
        ok.setText("Aplazar");
        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT,500,true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popupWindow.dismiss(); }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }

    public void RevisionErr(){
        //Revisión de errores (PopUp o vista ¿?)
        //Lista de CheckBox
        Toast.makeText(contexto, "Función disponible en la siguiente versión", Toast.LENGTH_SHORT).show();
    }

    public String crearTipoA(){
        String Ingreso = "";
        String CTA = "";
        String queryCli = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] objeto = conGen.queryObjeto(contexto,queryCli,new String[]{"CVAL"});
        Ingreso = objeto[0].get(0);
        if (Ingreso.equals("0")) {
            String Tipo = "";

            ArrayList<String>[] objFlujo = conGen.queryGeneral(contexto, "ACT", new String[]{"VAL"}, "VA='TIPOM'");
            if (objFlujo != null) {
                flujo = objFlujo[0].get(0);
            }
            if (flujo.equals("1")) {
                queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and (cu=1 or cu=3)";
            } else if (flujo.equals("2")) {
                queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and (cu=2 or cu=3)";
            } else if (flujo.equals("3")) {
                queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and cu=5";
            } else if (flujo.equals("4")) {
                queryActVal = "SELECT orden,idpreg,preg,cnd1,cnd2,cnd3,cnd4,tipo,gr FROM '302_PREGGEN' where ap=1 and cu=4";
            }
            ArrayList<String>[] actidpreg = conGen.queryObjeto2val(contexto, queryActVal, null);
            if (actidpreg != null) {
                for (Integer i = 0; i < actidpreg.length; i++) {
                    String consval = actidpreg[i].get(3);
                    if (consval != null || consval.equals("0")) {
                        Integer acval = 0;
                        if (consval.equals("0")) {
                            acval = 1;
                        } else {
                            ArrayList<String>[] activval = conGen.queryObjeto2val(contexto, consval, null);
                            acval = Integer.parseInt(activval[0].get(0));
                        }

                        if (acval > 0) {
                            Tipo = actidpreg[i].get(7);
                            operaciones.queryNoData("UPDATE '302_PREGGEN' SET aplica='1' WHERE orden='" + actidpreg[i].get(0) + "'");
                            if (!Tipo.equals("3") && !Tipo.equals("4") && !Tipo.equals("6")) {

                                String queryActopc = "SELECT opn,opt,cnd FROM '302_PREGGEND' WHERE idpreg='" + actidpreg[i].get(1) + "' ORDER BY opn ASC";
                                ArrayList<String>[] actidopc = conGen.queryObjeto2val(contexto, queryActopc, null);
                                if (actidopc != null) {
                                    for (Integer x = 0; x < actidopc.length; x++) {
                                        String consvalx = actidopc[x].get(2);
                                        if (consvalx != null) {
                                            Integer validactopt = 0;
                                            if (consvalx.equals("0")) {
                                                validactopt = 1;
                                            } else {
                                                ArrayList<String>[] activvalx = conGen.queryObjeto2val(contexto, consvalx, null);
                                                validactopt = Integer.parseInt(activvalx[0].get(0));
                                            }

                                            String queryActext = "SELECT count(encuesta) as ce FROM t8t WHERE idpreg='" + actidpreg[i].get(1) + "' and optn='" + actidopc[x].get(0) + "'";
                                            ArrayList<String>[] actidex = conGen.queryObjeto2val(contexto, queryActext, null);

                                            if (Integer.parseInt(actidex[0].get(0)) == 0) {
                                                if (validactopt > 0) {
                                                    insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), actidopc[x].get(0), actidopc[x].get(1), "1", actidpreg[i].get(8));
                                                } else {
                                                    insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), actidopc[x].get(0), actidopc[x].get(1), "2", actidpreg[i].get(8));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {

                                String queryActext = "SELECT count(encuesta) as ce FROM t8t WHERE idpreg='" + actidpreg[i].get(1) + "'";
                                ArrayList<String>[] actidex = conGen.queryObjeto2val(contexto, queryActext, null);
                                if (Integer.parseInt(actidex[0].get(0)) == 0) {
                                    insertarT8(actidpreg[i].get(0), actidpreg[i].get(1), actidpreg[i].get(2), "0", "0", "1", actidpreg[i].get(8));
                                }
                            }
                        }
                    }
                }

                String queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1";
                ArrayList<String>[] actidexx = conGen.queryObjeto2val(contexto, queryActextx, null);
                if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                    CTA = "1";
                } else {
                    CTA = "2";
                }
                operaciones.queryNoData("UPDATE 'PA' SET va='1' where pa='CVAL'");
            }
        } else {
            String queryActextx = "SELECT count(orden) as co,min(orden) as mo FROM '302_PREGGEN' where aplica=1";
            ArrayList<String>[] actidexx = conGen.queryObjeto2val(contexto, queryActextx, null);
            if (Integer.parseInt(actidexx[0].get(0)) > 0) {
                CTA = "1";
            } else {
                CTA = "2";
            }
        }
        return CTA;
    }

    private void insertarT8(String orden, String idpreg, String preg, String optn, String optt, String aplica, String grupo) {
            String aud = getAuditoria();
            ContentValues cv = new ContentValues();
            cv.put("encuesta", aud);
            cv.put("orden", orden);
            cv.put("idpreg", idpreg);
            cv.put("preg", preg);
            cv.put("optn", optn);
            cv.put("optt", optt);
            cv.put("rta", "0");
            cv.put("aplica", aplica);
            cv.put("fcr", fechaActual(3));
            cv.put("gr", grupo);
            boolean ok = operaciones.insertar("t8t", cv);
            if (ok == false) {
                System.out.println("Fallo el ingreso a t8");
            }
    }

    public void insNI(String PVM, String NV, String IDEN) {
        ContentValues cv = new ContentValues();
        cv.put("pvm", PVM);
        cv.put("nv", NV);
        cv.put("iden", IDEN);
        boolean ok = operaciones.insertar("flujo", cv);
        if (ok == false) {
            System.out.println("No se crear el nivel de flujo");
        }
    }
    public void insSNI(String PVM, String NV,String SNV, String IDC, String IDI) {
        ContentValues cv = new ContentValues();
        cv.put("pvm", PVM);
        cv.put("nv", NV);
        cv.put("snv", SNV);
        cv.put("idc", IDC);
        cv.put("idi", IDI);
        boolean ok = operaciones.insertar("flujoind", cv);
        if (ok == false) {
            System.out.println("No se crear el nivel de flujoind");
        }
    }

    public void eliNI() {
        boolean ok = operaciones.queryNoData("delete from flujo where nv in (select min(nv) from flujo)");
        if (ok == false) {
            System.out.println("No se pudo regresar al nivel anterior");
        }
    }
    public void eliSNI() {
        boolean ok = operaciones.queryNoData("delete from flujoind where snv in (select max(snv) from flujoind)");
        if (ok == false) {
            System.out.println("No se pudo regresar al subnivel anterior");
        }
    }
    public void limpiaNI() {
        boolean ok = operaciones.queryNoData("delete from flujo");
        if (ok == false) {
            System.out.println("no se pudo borrar a tabla flujo");
        }
    }
    public void limpiaSNI() {
        boolean ok = operaciones.queryNoData("delete from flujoind");
        if (ok == false) {
            System.out.println("no se pudo borrar a tabla flujoind");
        }
    }

    public void backupdDatabase(){
        try {

            try {
                cini = new File(Environment.getExternalStorageDirectory() , "DATABASESMKT");
                if (!cini.exists()) {
                    cini.mkdirs();
                }
            }catch (Exception exp){
              Log.i("No CREATE DATABASESMKT:",exp.toString());
            } finally {
                try {
                    sd = new File(Environment.getExternalStorageDirectory() + "/DATABASESMKT/" , getCodProy());
                    if (!sd.exists()) {
                        sd.mkdirs();
                    }
                }catch (Exception exp){
                    Log.i("NO CREATE CODPROY:",exp.toString());
                } finally {
                    String sourceDBName = "tiemposmkt.db";
                    String targetDBName = "tiemposmkt_" + getCodProy() + ".db";
                    String currentDBPath = "";
                    String backupDBPath = "";
                    if (sd.canWrite()) {
                        Date now = new Date();

                        if(android.os.Build.VERSION.SDK_INT >= 4.2){
                            currentDBPath = "/data/data/" + contexto.getPackageName() + "/databases/" + sourceDBName;
                        } else {
                            currentDBPath= contexto.getApplicationInfo().dataDir + "/databases/" + sourceDBName;
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                        backupDBPath = targetDBName ;

                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        Log.i("backup","backupDB=" + backupDB.getAbsolutePath());
                        Log.i("backup","sourceDB=" + currentDB.getAbsolutePath());

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            }

        } catch (Exception e) {
            Log.i("Backup", e.toString());
        }
    }

    public void BorrarBaseBackup(){
        String fullPath = "/mnt/sdcard/DATABASESMKT/";
        System.out.println(fullPath);
        try{
            File file = new File(fullPath, "tiemposmkt_" + getCodProy() + ".db");
            if(file.exists()){
                boolean result = file.delete();
                System.out.println("La aplicación no puede borrar el archivo y el resultado es: " + result);
                // file.delete();
            }else{
                System.out.println("La aplicación no puede borrar el archivo.");
            }
        }catch (Exception e){
            Log.e("App", "Error al borrar el archivo " + e.getMessage());
        }
    }


    public void originalDatabase(){
        try {

            try {
                cini = new File(Environment.getExternalStorageDirectory() , "DATABASESMKT");
                if (!cini.exists()) {
                    cini.mkdirs();
                }
            }catch (Exception exp){
                Log.i("No CREATE DATABASESMKT:",exp.toString());
            } finally {
                try {
                    sd = new File(Environment.getExternalStorageDirectory() + "/DATABASESMKT/" , getCodProy());
                    if (!sd.exists()) {
                        sd.mkdirs();
                    }
                }catch (Exception exp){
                    Log.i("NO CREATE CODPROY:",exp.toString());
                } finally {
                    String sourceDBName = "tiemposmkt.db";
                    String targetDBName = "tiemposmkt_" + getCodProy() + ".db";
                    String currentDBPath = "";
                    String backupDBPath = "";
                    if (sd.canWrite()) {
                        Date now = new Date();

                        if(android.os.Build.VERSION.SDK_INT >= 4.2){
                            currentDBPath = "/data/data/" + contexto.getPackageName() + "/databases/" + sourceDBName;
                        } else {
                            currentDBPath= contexto.getApplicationInfo().dataDir + "/databases/" + sourceDBName;
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                        backupDBPath = targetDBName ;

                        File currentDB = new File(sd, backupDBPath);
                        File backupDB = new File(currentDBPath);

                        Log.i("backup","backupDB=" + backupDB.getAbsolutePath());
                        Log.i("backup","sourceDB=" + currentDB.getAbsolutePath());

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            }

        } catch (Exception e) {
            Log.i("Restore Failed", e.toString());
        }
    }

    public void BorrarBaseoriginal(){
        String sourceDBName = "tiemposmkt.db";
        String fullPath = "";
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            fullPath = "/data/data/" + contexto.getPackageName() + "/databases/" + sourceDBName;
        } else {
            fullPath= contexto.getApplicationInfo().dataDir + "/databases/" + sourceDBName;
        }
        System.out.println(fullPath);
        try{
            File file = new File(fullPath);
            if(file.exists()){
                boolean result = file.delete();
                System.out.println("La aplicación no puede borrar el archivo y el resultado es: " + result);
                // file.delete();
            }else{
                System.out.println("La aplicación no puede borrar el archivo.");
            }
        }catch (Exception e){
            Log.e("App", "Error al borrar el archivo " + e.getMessage());
        }
    }



}

