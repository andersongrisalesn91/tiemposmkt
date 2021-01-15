package com.marketteam.desarrollo.tiemposmkt;

import android.content.ContentValues;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class ActualizarTablas {

    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    Context context;
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    String fechaC, horaC, Idcliente;
    FuncionesGenerales fg;

    public ActualizarTablas(Context contexto,String databasename) {
        this.conGen = new ConsultaGeneral(contexto,databasename);
        this.operaciones = new OperacionesBDInterna(contexto,null);
        this.context = contexto;
        this.locationManager = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        fg = new FuncionesGenerales(contexto,databasename);
    }

    public void insertarT1() {
        String IDAud = IdAuditoria();
        String queryCli = "SELECT va FROM PA WHERE pa=?";
        ArrayList<String>[] objetouser = conGen.queryObjeto(context, queryCli, new String[]{"user"});
        String user = "";
        if (objetouser != null) {
            user = objetouser[0].get(0);
        }
        ContentValues cv = new ContentValues();
        cv.put("idencuesta", IDAud);
        cv.put("idencuestador", user);
        cv.put("hini", fg.fechaActual(3));
        cv.put("hfin", "0");
        cv.put("ruta", "0");
        cv.put("mercaderista", "0");
        cv.put("estado", "0");
        cv.put("sincro", "0");
        cv.put("observ", "0");
        boolean insert = operaciones.insertar("principalt", cv);
        if (insert != true) {
            Toast.makeText(context, "No se ha podido insertar la info en la tabla", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertarT2() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        //Sacar los espacios y meterlos al arreglo
        String[] espacios;
        ArrayList<String> resCampos = fg.existeACT(3);
        String canalAct = resCampos.get(0);
        String subcAct = resCampos.get(1);
        String espAct = resCampos.get(2);
        ArrayList<String>[] objEspacios = conGen.queryGeneral(context, "'203_ESP'", new String[]{"ESP"}, "IDC='" + canalAct + "'");
        if (objEspacios != null) {
            espacios = new String[objEspacios.length];
            for (int c = 0; c < objEspacios.length; c++) {
                espacios[c] = objEspacios[c].get(0);
            }
            for (int e = 0; e < espacios.length; e++) {
                ContentValues cv = new ContentValues();
                cv.put("encuesta", idAud);
                cv.put("esp", espacios[e]);
                //1 - Si, 2- No
                cv.put("rta", "0");
                cv.put("eval", "0");
                cv.put("fcr", fg.fechaActual(3));
                boolean ok = operaciones.insertar("t2t", cv);
                if (ok == false) {
                    Toast.makeText(context, "FAIL Insertar T2T", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void actualizarT2(String pos, int acc) {
        String idEspacio = pos;

        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t2t SET rta='1', eval='2' WHERE esp='" + idEspacio + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t2t SET rta='2', eval='1' WHERE esp='" + idEspacio + "'");
        } else if (acc == 3) {
            //Cambiar a NO y borrar/actualizar a 0 y vacío lo relacionado con ese espacio (Categoría y estándar)
            operaciones.queryNoData("UPDATE t2t SET rta='2', eval='1' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t3t SET rta='2', eval='1' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t4t SET rta='2' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t5t SET rta='2' WHERE esp='" + idEspacio + "'");
        } else if (acc == 4) {
            //Cambiar a NO y borrar/actualizar a 0 y vacío lo relacionado con ese espacio (Categoría y estándar)
            operaciones.queryNoData("UPDATE t2t SET rta='1', eval='2' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t3t SET rta='0', eval='0' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t4t SET rta='0' WHERE esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t5t SET rta='0' WHERE esp='" + idEspacio + "'");
        }
    }
    public void actualizarCTUR(String ntable,String ncampo, String valueact) {
            operaciones.queryNoData("UPDATE " + ntable + " SET " + ncampo + "='" + valueact + "'");
    }

    public void insertarT3() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        //Sacar todas las categorías
        //Revisar si existe ya el/los parámetros
        String ciu_can = fg.getCiuCan();
        //Por cada espacio
        ArrayList<String>[] objEsp = conGen.queryGeneral(context, "'203_ESP'", new String[]{"ESP", "IDT2"}, "IDC='" + ciu_can + "'");
        if (objEsp != null) {
            for (int e = 0; e < objEsp.length; e++) {
                String espAct = objEsp[e].get(0);
                String idt2 = objEsp[e].get(1);
                ArrayList<String>[] objCat = conGen.queryGeneral(context, "'204_CAT'", new String[]{"CAT"}, "IDC='" + ciu_can + "' AND IDT2='" + idt2 + "'");
                if (objCat != null) {
                    for (int c = 0; c < objCat.length; c++) {
                        String catAct = objCat[c].get(0);
                        ContentValues cv = new ContentValues();
                        cv.put("encuesta", idAud);
                        cv.put("esp", espAct);
                        cv.put("cat", catAct);
                        //1 - Si, 2- No
                        cv.put("rta", "0");
                        cv.put("eval", "0");
                        cv.put("fcr", fg.fechaActual(3));
                        boolean ok = operaciones.insertar("t3t", cv);
                        if (ok == false) {
                            Toast.makeText(context, "FAIL Insertar registro en T3T", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    public void actualizarT3(String idCategoria, int acc, String idEspacio) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t3t SET rta='1', eval='2' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t3t SET rta='2', eval='1' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        } else if (acc == 3) {
            //Cambiar a NO y borrar/actualizar a 0 y vacío lo relacionado con esa Categoría (Estándar)
            operaciones.queryNoData("UPDATE t3t SET rta='2', eval='1' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t4t SET rta='2' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t5t SET rta='2' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            //Marcar como evaluada
        } else if (acc == 4) {
            //Cambiar a si y marcar como evaluado
            operaciones.queryNoData("UPDATE t3t SET rta='1', eval='1' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        } else if (acc == 5) {
            //Cambiar a NO y borrar/actualizar a 0 y vacío lo relacionado con esa Categoría (Estándar)
            operaciones.queryNoData("UPDATE t3t SET rta='1', eval='2' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t4t SET rta='0' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            operaciones.queryNoData("UPDATE t5t SET rta='0' WHERE cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
            //Marcar como evaluada
        }
    }

    public void insertarT4() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String ciu_can = fg.getCiuCan();
        String[] estandares;
        //Por cada espacio y categoría
        ArrayList<String>[] objEsp = conGen.queryGeneral(context, "'203_ESP'", new String[]{"ESP", "IDT2"}, "IDC='" + ciu_can + "'");
        if (objEsp != null) {
            for (int e = 0; e < objEsp.length; e++) {
                String espAct = objEsp[e].get(0);
                String idt2 = objEsp[e].get(1);
                ArrayList<String>[] objCat = conGen.queryGeneral(context, "'204_CAT'", new String[]{"CAT", "IDT3"}, "IDC='" + ciu_can + "' AND IDT2='" + idt2 + "'");
                if (objCat != null) {
                    for (int c = 0; c < objCat.length; c++) {
                        String catAct = objCat[c].get(0);
                        String idt3 = objCat[c].get(1);
                        //Estándares
                        ArrayList<String>[] objEstand = conGen.queryGeneral(context, "'205_PREG'", new String[]{"PREG"}, "IDC='" + ciu_can + "' AND IDT3='" + idt3 + "' AND TIPO='1'");
                        if (objEstand != null) {
                            for (int s = 0; s < objEstand.length; s++) {
                                String estand = objEstand[s].get(0);
                                ContentValues cv = new ContentValues();
                                cv.put("encuesta", idAud);
                                cv.put("esp", espAct);
                                cv.put("cat", catAct);
                                cv.put("est", estand);
                                //1 - Si, 2- No
                                cv.put("rta", "0");
                                cv.put("fcr", fg.fechaActual(3));
                                boolean ok = operaciones.insertar("t4t", cv);
                                if (ok == false) {
                                    Toast.makeText(context, "FAIL Insertar registro en T4T", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void actualizarT4(String idEstandar, int acc, String idEspacio, String idCategoria) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t4t SET rta='1' WHERE est='" + idEstandar + "' AND cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t4t SET rta='2' WHERE est='" + idEstandar + "' AND cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        }else if (acc == 0) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t4t SET rta='0' WHERE est='" + idEstandar + "' AND cat='" + idCategoria + "' AND esp='" + idEspacio + "'");
        }
    }

    public void insertarT5() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String ciu_can = fg.getCiuCan();
        String[] estandares;
        //Por cada espacio y categoría
        ArrayList<String>[] objEsp = conGen.queryGeneral(context, "'203_ESP'", new String[]{"ESP", "IDT2"}, "IDC='" + ciu_can + "'");
        if (objEsp != null) {
            for (int e = 0; e < objEsp.length; e++) {
                String espAct = objEsp[e].get(0);
                String idt2 = objEsp[e].get(1);
                ArrayList<String>[] objCat = conGen.queryGeneral(context, "'204_CAT'", new String[]{"CAT", "IDT3"}, "IDC='" + ciu_can + "' AND IDT2='" + idt2 + "'");
                if (objCat != null) {
                    for (int c = 0; c < objCat.length; c++) {
                        String catAct = objCat[c].get(0);
                        String idt3 = objCat[c].get(1);
                        //Estándares
                        ArrayList<String>[] objSku = conGen.queryGeneral(context, "'205_PREG'", new String[]{"PREG"}, "IDC='" + ciu_can + "' AND IDT3='" + idt3 + "' AND TIPO='2'");
                        if (objSku != null) {
                            for (int s = 0; s < objSku.length; s++) {
                                String sku = objSku[s].get(0);
                                ContentValues cv = new ContentValues();
                                cv.put("encuesta", idAud);
                                cv.put("esp", espAct);
                                cv.put("cat", catAct);
                                cv.put("sku", sku);
                                //1 - Si, 2- No
                                cv.put("rta", "0");
                                cv.put("fcr", fg.fechaActual(3));
                                boolean ok = operaciones.insertar("t5t", cv);
                                if (ok == false) {
                                    Toast.makeText(context, "FAIL Insertar registro en T5T", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void actualizarT5(String idSku, int acc, String esp, String cat) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t5t SET rta='1' WHERE sku='" + idSku + "' AND cat='" + cat + "' AND esp='" + esp + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t5t SET rta='2' WHERE sku='" + idSku + "' AND cat='" + cat + "' AND esp='" + esp + "'");
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t5t SET rta='0' WHERE sku='" + idSku + "' AND cat='" + cat + "' AND esp='" + esp + "'");
        }
    }

    public void insertarT6(String esp, String cat, String ncat, Integer tipo) {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String conteo = "";
        if (esp.equals("3") || esp.equals("9")) {
            conteo = "1";
        } else {
            conteo = fg.getContesp();
        }
        ContentValues cv = new ContentValues();
        cv.put("encuesta", idAud);
        cv.put("id", conteo);
        cv.put("esp", esp);
        cv.put("cat", cat);
        cv.put("ncat", ncat);
        if (esp.equals("3") || esp.equals("9")) {
            cv.put("rta", "1");
        } else {
            cv.put("rta", "0");
        }
        cv.put("cant", "0");
        cv.put("tipo", tipo);
        cv.put("fcr", fg.fechaActual(3));
        boolean ok = operaciones.insertar("t6t", cv);
        if (ok == false) {
            Toast.makeText(context, "Fail insert t6t", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarT6(String IDCont, String Esp, int acc, String Cat, String cantidad) {
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t6t SET rta='1' WHERE id=" + IDCont + " and esp=" + Esp + " and cat=" + Cat);
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t6t SET rta='2' WHERE id=" + IDCont + " and esp=" + Esp + " and cat=" + Cat);
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t6t SET rta='0' WHERE id=" + IDCont + " and esp=" + Esp + " and cat=" + Cat);
        } else if (acc == 4) {
            operaciones.queryNoData("UPDATE t6t SET cant='" + cantidad + "' WHERE id=" + IDCont + " and esp=" + Esp + " and cat=" + Cat);
        }
    }

    public void insertarT7(int id, String aud, String cat, String part) {
        String idE = "" + id;
        ContentValues cv = new ContentValues();
        cv.put("encuesta", aud);
        cv.put("cat", cat);
        cv.put("part", part);
        cv.put("ep", idE);
        cv.put("cumple", "0");
        cv.put("fcr", fg.fechaActual(3));
        boolean ok = operaciones.insertar("t7t", cv);
        if (ok == false) {
            Toast.makeText(context, "Fail insert t7t", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarT7(String aud, String cat, String part, String[] valT, String[] valN) {
        //Recorrer valT/valN y hacer inserción por cada uno
            for (int i = 0; i < valT.length; i++) {
                operaciones.queryNoData("UPDATE t7t SET part_t='" + valT[i] + "', part_n='" + valN[i] + "' WHERE encuesta='" + aud + "' AND cat='" + cat + "' AND part='" + part + "' and ep='" + (i + 1) + "'");
            }
    }
    public void actualizarT7U(String aud, String cat, String part, String valT, String valN,int Entrep) {
            operaciones.queryNoData("UPDATE t7t SET part_t='" + valT + "', part_n='" + valN + "' WHERE encuesta='" + aud + "' AND cat='" + cat + "' AND part='" + part + "' and ep='" + (Entrep + 1) + "'");
    }


    public void eliminarT7(String aud, String cat, String part, String posep) {
        //Recorrer valT/valN y hacer inserción por cada uno
        operaciones.queryNoData("DELETE FROM t7t WHERE encuesta='" + aud + "' AND cat='" + cat + "' AND part='" + part + "' and ep='" + posep + "'");

    }

    public void insertarT8(String orden, String idpreg, String preg, String optn, String optt, String aplica, String grupo) {
        String aud = fg.getAuditoria();
        ContentValues cv = new ContentValues();
        cv.put("encuesta", aud);
        cv.put("orden", orden);
        cv.put("idpreg", idpreg);
        cv.put("preg", preg);
        cv.put("optn", optn);
        cv.put("optt", optt);
        cv.put("rta", "0");
        cv.put("aplica", aplica);
        cv.put("fcr", fg.fechaActual(3));
        cv.put("gr", grupo);
        boolean ok = operaciones.insertar("t8t", cv);
        if (ok == false) {
            Toast.makeText(context, "Fail insert t8t", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarT8(String idpreg, String optn, int acc) {
        if (acc == 1) {
            //ingresar rta
            operaciones.queryNoData("UPDATE t8t SET rta='1' WHERE idpreg='" + idpreg + "' and optn=" + optn);
            operaciones.queryNoData("UPDATE t8t SET rta='0' WHERE idpreg='" + idpreg + "' and optn=98");
        } else if (acc == 2) {
            //borrar rta
            operaciones.queryNoData("UPDATE t8t SET rta='0' WHERE idpreg='" + idpreg + "' and optn=" + optn);
            operaciones.queryNoData("UPDATE t8t SET rta='0' WHERE idpreg='" + idpreg + "' and optn=98");
        }
        else if (acc == 3) {
            //borrar rta
            operaciones.queryNoData("UPDATE t8t SET rta='1' WHERE idpreg='" + idpreg + "' and optn=" + optn);
            operaciones.queryNoData("UPDATE t8t SET rta='0' WHERE idpreg='" + idpreg + "' and optn<>" + optn);
        }
    }

    public void actualizarT8RD(String idpreg, String optn) {
            operaciones.queryNoData("UPDATE t8t SET rta='1' WHERE idpreg='" + idpreg + "' and optn=" + optn);
            operaciones.queryNoData("UPDATE t8t SET rta='0' WHERE idpreg='" + idpreg + "' and optn<>" + optn);
    }

    public void insertarT9() {
        ContentValues cv = new ContentValues();
        cv.put("encuesta", "");
        cv.put("esp", "");
        cv.put("rta", "");
        cv.put("fcr", fg.fechaActual(3));
        operaciones.insertar("t9t", cv);
    }

    public void actualizarT9() {

    }

    public void insertarT10() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String ciu_can = fg.getCiuCan();
        String tipom = fg.getTipoM();
        String querySubc = "SELECT VAL FROM ACT WHERE VA='SUBC'";
        ArrayList<String>[] SubCanal = conGen.queryObjeto2val(context, querySubc, null);
        String Subc = SubCanal[0].get(0);
        String querySkuc = "";
        if (Integer.parseInt(tipom)==2 || Integer.parseInt(tipom)==3){
            querySkuc = "SELECT CAT,0 as MAR,SKU,NSKU FROM '206_SKUCAL' where IDC=" + ciu_can + " and SC=0 order by CAT,SKU";
        }else {
            querySkuc = "SELECT CAT,0 as MAR,SKU,NSKU FROM '206_SKUCAL' where IDC=" + ciu_can + " and SC=" + Subc + " order by CAT,SKU";
        }
        ArrayList<String>[] objskuc = conGen.queryObjeto2val(context, querySkuc, null);
        if (objskuc != null) {
            for (int e = 0; e < objskuc.length; e++) {

                String cat = objskuc[e].get(0);
                String marca = objskuc[e].get(1);
                String sku = objskuc[e].get(2);
                String nsku = objskuc[e].get(3);
                ContentValues cv = new ContentValues();
                cv.put("encuesta", idAud);
                cv.put("cat", cat);
                cv.put("mar", marca);
                cv.put("sku", sku);
                cv.put("nsku", nsku);
                cv.put("rta", "0");
                cv.put("mis", "0");
                cv.put("fcr", fg.fechaActual(3));
                boolean ok = operaciones.insertar("t10t", cv);
                if (ok == false) {
                    Toast.makeText(context, "FAIL Insertar registro en T10T", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void actualizarT10(String idSku, int acc) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t10t SET rta='1' WHERE sku='" + idSku + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t10t SET rta='2' WHERE sku='" + idSku + "'");
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t10t SET rta='0' WHERE sku='" + idSku + "'");
        } else if (acc == 4) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t10t SET rta='3' WHERE sku='" + idSku + "'");
        }
    }

    public void actualizarT10M(String idSku, int acc) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t10t SET mis='1' WHERE sku='" + idSku + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t10t SET mis='2' WHERE sku='" + idSku + "'");
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t10t SET mis='0' WHERE sku='" + idSku + "'");
        }
    }

    public void insertarT11() {
        String queryAud = "SELECT VAL FROM ACT WHERE VA=?";
        ArrayList<String>[] idAuditoria = conGen.queryObjeto(context, queryAud, new String[]{"AUD"});
        String idAud = idAuditoria[0].get(0);
        String queryCLI = "SELECT t1t.cliente_t FROM t1t";
        ArrayList<String>[] Clie = conGen.queryObjeto2val(context, queryCLI, null);
        Idcliente = Clie[0].get(0);
        String queryAct = "SELECT CONS,CODB,NACT FROM '209_ACTIVOS' where CLI='" + Idcliente + "' order by CONS ASC";
        ArrayList<String>[] objact = conGen.queryObjeto2val(context, queryAct, null);
        Integer consec = 0;
        if (objact != null) {
            for (int e = 0; e < objact.length; e++) {
                consec++;
                String codigobarras = objact[e].get(1);
                String consecutivo = objact[e].get(0);
                String nombreactivo = objact[e].get(2);
                ContentValues cv = new ContentValues();
                cv.put("encuesta", idAud);
                cv.put("codb", codigobarras);
                cv.put("ida", consecutivo);
                cv.put("nact", nombreactivo);
                cv.put("rta", "0");
                cv.put("coin", "0");
                cv.put("fcr", fg.fechaActual(3));
                boolean ok = operaciones.insertar("t11t", cv);
                if (ok == false) {
                    Toast.makeText(context, "FAIL Insertar registro en T11T", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void actualizarT11(String idA, int acc, String codbar) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t11t SET rta='1' WHERE codb='" + codbar + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET rta='2' WHERE codb='" + codbar + "'");
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET rta='0' WHERE codb='" + codbar + "'");
        } else if (acc == 4) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET rta='3' WHERE codb='" + codbar + "'");
        } else if (acc == 5) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET rta='4' WHERE codb='" + codbar + "'");
        }
    }

    public void actualizarT11C(String idA, int acc, String codbar) {
        //Update si o no
        if (acc == 1) {
            //Cambiar a SI
            operaciones.queryNoData("UPDATE t11t SET coin='1' WHERE codb='" + codbar + "'");
        } else if (acc == 2) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET coin='2' WHERE codb='" + codbar + "'");
        } else if (acc == 3) {
            //Cambiar a NO
            operaciones.queryNoData("UPDATE t11t SET coin='0' WHERE codb='" + codbar + "'");
        } else if (acc == 4) {
            //Cambiar a borrosa
            operaciones.queryNoData("UPDATE t11t SET coin='3' WHERE codb='" + codbar + "'");
        } else if (acc == 5) {
            //Cambiar a NO Visible
            operaciones.queryNoData("UPDATE t11t SET coin='4' WHERE codb='" + codbar + "'");
        }
    }

    public String IdAuditoria() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        //System.out.println(deviceName); // Máquina/maquina
        //System.out.println(fecha); //DD-MM-AAAA HH:MM:SS
        StringTokenizer st = new StringTokenizer(fecha, " ");
        fechaC = st.nextToken();
        horaC = st.nextToken();
        st = new StringTokenizer(fechaC, "-");
        String dia = st.nextToken();
        String mes = st.nextToken();
        String an = st.nextToken();
        st = new StringTokenizer(horaC, ":");
        String hora = st.nextToken();
        String min = st.nextToken();
        String seg = st.nextToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int mSec = calendar.get(Calendar.MILLISECOND);
        String IDAud = an + mes + dia + hora + min + seg + mSec;
        return IDAud;
    }
}
