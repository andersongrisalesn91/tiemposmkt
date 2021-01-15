package com.marketteam.desarrollo.tiemposmkt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marketteam.desarrollo.tiemposmkt.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Sincronizar extends AppCompatActivity {

    ConsultaGeneral conGen;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    TextView pb1,ssin, cont,fssin,fcont,tvt1,tvt2,tvt3,tvt4,tvt5,tvt6,tvt7,tvt8,tvt9,tvt10,tvt11,tvttf,tvttr,tvttv;
    static Sincronizar activityA;
    Context context;
    String encuestaAct,conteosin,conteosT,conteosFsin,conteosFT;
    StorageReference mStorageRef;
    Uri file;
    ProgressBar pbfotos;
    FirebaseFirestore db;
    ImageView iv;
    Button sincd,sincf;
    ImageButton botonr;
    int ct1,ct2,ct3,ct4,ct5,ct6,ct7,ct8,ct9,ct10,ct11,cttf,cfsin,cttr,cttv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        activityA = this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public void onBackPressed() {}

    @Override
    protected void onStart() {
        super.onStart();
        if(Login.getInstance() != null){Login.getInstance().finish();}
        if(TomarFoto.getInstance() != null){TomarFoto.getInstance().finish();}

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getBaseContext();
        conGen = new ConsultaGeneral(getBaseContext(),"tiemposmkt.db");
        operaciones = new OperacionesBDInterna(getBaseContext(),null);
        fg = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        fg.ultimaPantalla("Sincron");
        db = FirebaseFirestore.getInstance();
        sincd = (Button) findViewById(R.id.buttonSincronizar2);
        sincf = (Button) findViewById(R.id.buttonSincronizar3);
        botonr = (ImageButton) findViewById(R.id.ibCerrarSesion);
        String QueryConteosEnc = "select count(encuesta) as CE from t1 where estado=1 and sincronizada=0";
        ArrayList<String>[] ConteoEnc = conGen.queryObjeto2val(getBaseContext(), QueryConteosEnc, null);
        String QueryConteosEncT = "select count(encuesta) as CE from t1 where estado=1";
        ArrayList<String>[] ConteoEncT = conGen.queryObjeto2val(getBaseContext(), QueryConteosEncT, null);
        String QueryConteosFEnc = "SELECT COUNT(path) AS cant FROM tf WHERE path<>'null' AND (sinc=0 OR sinc is null)";
        ArrayList<String>[] ConteoFEnc = conGen.queryObjeto2val(getBaseContext(), QueryConteosFEnc, null);
        String QueryConteosFEncT = "SELECT COUNT(path) AS cant FROM tf WHERE path<>'null'";
        ArrayList<String>[] ConteoFEncT = conGen.queryObjeto2val(getBaseContext(), QueryConteosFEncT, null);

        conteosin = ConteoEnc[0].get(0);
        conteosT = ConteoEncT[0].get(0);
        conteosFsin = ConteoFEnc[0].get(0);
        conteosFT = ConteoFEncT[0].get(0);
        pbfotos = (ProgressBar) findViewById(R.id.progressBarTF);
        ssin = (TextView) findViewById(R.id.tVTotalSSN);
        cont = (TextView) findViewById(R.id.tVTotalFN);
        fssin = (TextView) findViewById(R.id.tVTotalFSSN);
        fcont = (TextView) findViewById(R.id.tVTotalFCN);
        tvt1 = (TextView) findViewById(R.id.tVTNT);
        tvt2 = (TextView) findViewById(R.id.tVTNT1);
        tvt3 = (TextView) findViewById(R.id.tVTNT2);
        tvt4 = (TextView) findViewById(R.id.tVTNT3);
        tvt5 = (TextView) findViewById(R.id.tVTNT4);
        tvt6 = (TextView) findViewById(R.id.tVTNT5);
        tvt7 = (TextView) findViewById(R.id.tVTNT6);
        tvt8 = (TextView) findViewById(R.id.tVTNT8);
        tvt9 = (TextView) findViewById(R.id.tVTNT9);
        tvt10 = (TextView) findViewById(R.id.tVTNT10);
        tvt11 = (TextView) findViewById(R.id.tVTNT11);
        tvttf = (TextView) findViewById(R.id.tVTNT12);
        tvttr = (TextView) findViewById(R.id.tVTNT7);
        tvttv = (TextView) findViewById(R.id.tVTNT14);

        ssin.setText(conteosin);
        cont.setText(conteosT);
        fssin.setText(conteosFsin);
        fcont.setText(conteosFT);
        iv = (ImageView) findViewById(R.id.ivFotosSinc);
        ArrayList<String>[] fotos = conGen.queryObjeto2val(getBaseContext(),"SELECT COUNT(path) AS cant FROM tf WHERE path<>'null' AND (sinc<>1 OR sinc is null)",null);
        if(fotos != null){
            String cant = fotos[0].get(0);
            if(cant != null){
                if(cant.equals("0")){
                    //OK
                    iv.setImageResource(R.drawable.check_opt);
                } else {
                    //Falta
                    iv.setImageResource(R.drawable.equis_opt);
                }
            }
        }
    }

    public TextView retornartext(int postv)
    {
        if (postv==1) {
            pb1 =  tvt1;
        }
        if (postv==2) {
            pb1 =  tvt2;
        }
        if (postv==3) {
            pb1 =  tvt3;
        }
        if (postv==4) {
            pb1 =  tvt4;
        }
        if (postv==5) {
            pb1 =  tvt5;
        }
        if (postv==6) {
            pb1 =  tvt6;
        }
        if (postv==7) {
            pb1 =  tvt7;
        }
        if (postv==8) {
            pb1 =  tvt8;
        }
        if (postv==9) {
            pb1 =  tvt9;
        }
        if (postv==10) {
            pb1 =  tvt10;
        }
        if (postv==11) {
            pb1 =  tvt11;
        }
        if (postv==12) {
            pb1 =  tvttf;
        }
        if (postv==13) {
            pb1 =  tvttr;
        }
        if (postv==14) {
            pb1 =  tvttv;
        }
        return pb1;
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    public void irselcuest(View v) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent lista = new Intent(this, SelecMediciones.class);
        startActivity(lista);
    }


    public void MenuLateral(View vista) {
        fg.MenuLateral(vista);
    }
    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }

    public void sincronizar(View vista) {
        //SincronizaVF s = new SincronizaVF(getBaseContext());
        sincf.setClickable(false);
        sincd.setClickable(false);
        botonr.setClickable(false);
        Toast.makeText(getBaseContext(),"Sincronizando los datos, por favor espere", Toast.LENGTH_LONG).show();
        sincronizarTablasVF();
    }

    public void sincronizarF(View vista) {
        sincf.setClickable(false);
        sincd.setClickable(false);
        botonr.setClickable(false);
        guardarFotosR();
    }

    public void guardarFotosR() {
        int cantRegistros = 0, mod = 20;
        Map<String, Object> regis;
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT path FROM tf where sinc is null or sinc=0", null);
        if (registros != null) {
            cantRegistros = registros.length;
            if(cantRegistros > 79){
                if(cantRegistros >= 1000){
                    mod = 200;
                } else if(cantRegistros < 240){
                    mod = 60;
                } else if(cantRegistros < 600){
                    mod = 100;
                } else { mod = 150;}
            }
            for (int r = 0; r < registros.length; r++) {
                VerificarConex vc = new VerificarConex();
                boolean net = vc.revisarconexión(context);
                if (net == true) {
                    if((r%mod) == 0){
                        Toast.makeText(getBaseContext(),"Sincronizando las fotos, por favor espere", Toast.LENGTH_SHORT).show();
                    }
                    final String nombreFoto = registros[r].get(0);
                    String path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File f = new File(path);
                    if (f.exists() && f.length() > 0 && f != null) {
                        Uri file = Uri.fromFile(f);
                        if (file != null) {
                            final StorageReference picRef = mStorageRef.child("12160/img/" + nombreFoto);
                            picRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String url = taskSnapshot.getMetadata().getPath();
                                    if (url.equals("") || url == null) {
                                        operaciones.queryNoData("UPDATE tf SET sinc='0' WHERE path='" + nombreFoto + "';");
                                    } else {
                                        cfsin++;
                                        String cfs = "" + (Integer.parseInt(conteosFsin) - cfsin);
                                        fssin.setText(cfs);
                                        operaciones.queryNoData("UPDATE tf SET sinc='1' WHERE path='" + nombreFoto + "';");
                                        if (cfs.equals("0")){
                                            sincf.setClickable(true);
                                            sincd.setClickable(true);
                                            botonr.setClickable(true);
                                            iv.setImageResource(R.drawable.check_opt);
                                            Toast.makeText(context, "Sincronizacion de Fotos Finalizada", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    operaciones.queryNoData("UPDATE tf SET sinc='0' WHERE path='" + nombreFoto + "';");
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    updateProgress(taskSnapshot);
                                }
                            });
                        } else {
                            operaciones.queryNoData("UPDATE tf SET sinc='2' WHERE path='" + nombreFoto + "';");
                        }
                    } else {
                        operaciones.queryNoData("UPDATE tf SET sinc='2' WHERE path='" + nombreFoto + "';");
                    }
                } else {
                    Toast.makeText(getBaseContext(), "No hay red disponible para cargar fotos", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            iv.setImageResource(R.drawable.check_opt);
            sincf.setClickable(true);
            sincd.setClickable(true);
            botonr.setClickable(true);
            Toast.makeText(getBaseContext(), "No hay fotos por Sincronizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize = taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();
        final int progress;
        progress = Math.round((100 * uploadBytes) / fileSize);
        pbfotos.setProgress(progress);
    }
    public void sincronizarTablasVF() {

        //Tomar los datos de cada tabla y enviarlos a tablas a Firebase
        //Traer todos los registros de cada tabla e insertarlos individualmente en Firebase
        VerificarConex vc = new VerificarConex();
        boolean net = vc.revisarconexión(context);
        if (net == true) {
            operaciones = new OperacionesBDInterna(context,null);
            conGen = new ConsultaGeneral(context,"tiemposmkt.db");
            try {
                sincronizarT1();
            } finally {
                //Toast.makeText(context, "Sincronizando por favor espere...", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(context, "No se encuentra conectado a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarT1() {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t1 where estado=1 and sincronizada<>1", null);
        encuestaAct = "";
        int i;
        if (registros != null) {
            Toast.makeText(context, "Sincronizando .........", Toast.LENGTH_LONG).show();
            for (i = 0; i < registros.length; i++) {
                encuestaAct = registros[i].get(0);
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("maquina", registros[i].get(1));
                regis.put("fecha_ini", registros[i].get(2));
                regis.put("fecha_fin", registros[i].get(3));
                regis.put("hora_ini", registros[i].get(4));
                regis.put("hora_fin", registros[i].get(5));
                regis.put("gpslat_ini", registros[i].get(6));
                regis.put("gpslon_ini", registros[i].get(7));
                regis.put("gpslat_fin", registros[i].get(8));
                regis.put("gpslon_fin", registros[i].get(9));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fechaT = dateFormat.format(date);
                StringTokenizer st = new StringTokenizer(fechaT, " ");
                regis.put("fecha_sinc", st.nextToken());
                regis.put("hora_sinc", st.nextToken());
                regis.put("usuario", registros[i].get(12));
                regis.put("sup_tel1", registros[i].get(13));
                regis.put("sup_tel2", registros[i].get(14));
                regis.put("sup_tel3", registros[i].get(15));
                regis.put("sup_pre1", registros[i].get(16));
                regis.put("sup_pre2", registros[i].get(17));
                regis.put("sup_pre3", registros[i].get(18));
                regis.put("estado", registros[i].get(19));
                regis.put("entregada", registros[i].get(20));
                regis.put("sincronizada", registros[i].get(21));
                regis.put("cliente_t", registros[i].get(22));
                regis.put("cliente_n", registros[i].get(23));
                regis.put("maestro1", registros[i].get(24));
                regis.put("maestro2", registros[i].get(25));
                regis.put("maestro3", registros[i].get(26));
                regis.put("maestro4", registros[i].get(27));
                regis.put("maestro5", registros[i].get(28));
                regis.put("maestro6", registros[i].get(29));
                regis.put("maestro7", registros[i].get(30));
                regis.put("maestro8", registros[i].get(31));
                regis.put("maestro9", registros[i].get(32));
                regis.put("maestro10", registros[i].get(33));
                regis.put("maestro11", registros[i].get(34));
                regis.put("maestro12", registros[i].get(35));
                regis.put("mconfir1", registros[i].get(36));
                regis.put("mconfir2", registros[i].get(37));
                regis.put("mconfir3", registros[i].get(38));
                regis.put("mconfir4", registros[i].get(39));
                regis.put("mconfir5", registros[i].get(40));
                regis.put("mconfir6", registros[i].get(41));
                regis.put("mconfir7", registros[i].get(42));
                regis.put("mconfir8", registros[i].get(43));
                regis.put("mconfir9", registros[i].get(44));
                regis.put("mconfir10", registros[i].get(45));
                regis.put("mconfir11", registros[i].get(46));
                regis.put("mconfir12", registros[i].get(47));
                regis.put("observaciones", registros[i].get(48));
                regis.put("fcr", registros[i].get(49));
                regis.put("_ID", registros[i].get(50));
                regis.put("ID2", registros[i].get(51));
                try {
                    final int finalI = i;
                    db.collection("t1_12160").document(registros[i].get(0)).set(regis).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void T) {
                            ct1++;
                            String ces = "" + (Integer.parseInt(conteosin) - ct1);
                            TextView tv = retornartext(1);
                            tv.setText("" + ct1);
                            ssin.setText(ces);
                        }
                    });

                } catch (Exception e) {
                    //Que intente otra vez
                    final String s = registros[i].get(0);
                    db.collection("t1_12160").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "La sincronización ha fallado en T1", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {

                    regis = new HashMap<>();
                    sincronizarT2(encuestaAct);
                }
            }
        } else {
            sincf.setClickable(true);
            sincd.setClickable(true);
            botonr.setClickable(true);
            Intent intX = new Intent(context, Sincronizar.class);
            intX.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intX);
        }
    }

    public void sincronizarT2(String enct2) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t2 where encuesta='" + enct2 + "' and rta<>0 order by ID2", null);
        Map<String, Object> tabla2 = new HashMap<>();
        Map<String, Object> regis;
        String Encuesta = enct2;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("rta", registros[i].get(2));
                regis.put("eval", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                tabla2.put(enct2 + "_" + i, regis);
            }

            try {
                db.collection("t2_12160").document(Encuesta).set(tabla2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct2++;

                        TextView tv = retornartext(2);
                        tv.setText("" + ct2);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t2_12160").document(s).set(tabla2).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T2", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla2.clear();
                sincronizarT3(encuestaAct);
            }
        } else {
            sincronizarT3(encuestaAct);
        }
    }

    public void sincronizarT3(String enct3) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t3 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla3 = new HashMap<>();
        Map<String, Object> regis;
        String Encuesta = enct3;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("rta", registros[i].get(3));
                regis.put("eval", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla3.put(enct3 + "_" + i, regis);
            }
            try {
                db.collection("t3_12160").document(Encuesta).set(tabla3).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct3++;

                        TextView tv = retornartext(3);
                        tv.setText("" + ct3);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t3_12160").document(s).set(tabla3).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T3", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla3.clear();
                sincronizarT4(encuestaAct);
            }

        } else {
            sincronizarT4(encuestaAct);
        }
    }

    public void sincronizarT4(String enct4) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t4 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla4 = new HashMap<>();
        String Encuesta = enct4;
        Map<String, Object> regis;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("est", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla4.put(enct4 + "_" + i, regis);
            }
            try {
                db.collection("t4_12160").document(Encuesta).set(tabla4).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct4++;

                        TextView tv = retornartext(4);
                        tv.setText("" + ct4);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t4_12160").document(s).set(tabla4).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "La sincronización ha fallado en T4", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla4.clear();
                sincronizarT5(encuestaAct);
            }
        } else {
            sincronizarT5(encuestaAct);
        }
    }

    public void sincronizarT5(String enct5) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t5 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla5 = new HashMap<>();
        String Encuesta = enct5;
        Map<String, Object> regis;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("esp", registros[i].get(1));
                regis.put("cat", registros[i].get(2));
                regis.put("sku", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("fcr", registros[i].get(5));
                regis.put("_ID", registros[i].get(6));
                regis.put("ID2", registros[i].get(7));
                tabla5.put(enct5 + "_" + i, regis);
            }
            try {
                db.collection("t5_12160").document(Encuesta).set(tabla5).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct5++;

                        TextView tv = retornartext(5);
                        tv.setText("" + ct5);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t5_12160").document(s).set(tabla5).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t5").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T5", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla5.clear();
                sincronizarT6(encuestaAct);
            }
        } else {
            sincronizarT6(encuestaAct);
        }
    }

    public void sincronizarT6(String enct6) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t6 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla6 = new HashMap<>();
        String Encuesta = enct6;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("id", registros[i].get(1));
                regis.put("esp", registros[i].get(2));
                regis.put("cat", registros[i].get(3));
                regis.put("ncat", registros[i].get(4));
                regis.put("rta", registros[i].get(5));
                regis.put("cant", registros[i].get(6));
                regis.put("tipo", registros[i].get(7));
                regis.put("fcr", registros[i].get(8));
                regis.put("_ID", registros[i].get(9));
                regis.put("ID2", registros[i].get(10));
                tabla6.put(enct6 + "_" + i, regis);
            }
            try {
                db.collection("t6_12160").document(Encuesta).set(tabla6).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct6++;

                        TextView tv = retornartext(6);
                        tv.setText("" + ct6);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t6_12160").document(s).set(tabla6).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t6").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T6", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla6.clear();
                sincronizarT7(encuestaAct);
            }
        } else {
            sincronizarT7(encuestaAct);
        }
    }

    public void sincronizarT7(String enct7) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t7 where encuesta='" + encuestaAct + "' order by ID2", null);
        Map<String, Object> tabla7 = new HashMap<>();
        String Encuesta = enct7;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("part", registros[i].get(2));
                regis.put("ep", registros[i].get(3));
                regis.put("part_t", registros[i].get(4));
                regis.put("part_n", registros[i].get(5));
                regis.put("cumple", registros[i].get(6));
                regis.put("fcr", registros[i].get(7));
                regis.put("_ID", registros[i].get(8));
                regis.put("ID2", registros[i].get(9));
                tabla7.put(enct7 + "_" + i, regis);
            }
            try {
                db.collection("t7_12160").document(Encuesta).set(tabla7).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct7++;

                        TextView tv = retornartext(7);
                        tv.setText("" + ct7);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t7_12160").document(s).set(tabla7).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t7").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T7", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla7.clear();
                sincronizarT8(encuestaAct);
            }
        } else {
            sincronizarT8(encuestaAct);
        }
    }

    public void sincronizarT8(String enct8) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t8 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla8 = new HashMap<>();
        String Encuesta = enct8;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("orden", registros[i].get(1));
                regis.put("idpreg", registros[i].get(2));
                regis.put("preg", registros[i].get(3));
                regis.put("optn", registros[i].get(4));
                regis.put("optt", registros[i].get(5));
                regis.put("rta", registros[i].get(6));
                regis.put("aplica", registros[i].get(7));
                regis.put("fcr", registros[i].get(8));
                regis.put("_ID", registros[i].get(9));
                regis.put("ID2", registros[i].get(10));
                tabla8.put(enct8 + "_" + i, regis);
            }
            try {
                db.collection("t8_12160").document(Encuesta).set(tabla8).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct8++;

                        TextView tv = retornartext(8);
                        tv.setText("" + ct8);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t8_12160").document(s).set(tabla8).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t8").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T8", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla8.clear();
                sincronizarT9(encuestaAct);
            }
        } else {
            sincronizarT9(encuestaAct);
        }
    }

    public void sincronizarT9(String enct9) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t9 where encuesta='" + encuestaAct + "' and prec<>0  order by ID2", null);
        Map<String, Object> tabla9 = new HashMap<>();
        String Encuesta = enct9;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("sku", registros[i].get(2));
                regis.put("prec", registros[i].get(3));
                regis.put("fcr", registros[i].get(4));
                regis.put("_ID", registros[i].get(5));
                regis.put("ID2", registros[i].get(6));
                tabla9.put(enct9 + "_" + i, regis);
            }
            try {
                db.collection("t9_12160").document(Encuesta).set(tabla9).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct9++;

                        TextView tv = retornartext(9);
                        tv.setText("" + ct9);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t9_12160").document(s).set(tabla9).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t9").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T9", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla9.clear();
                sincronizarT10(encuestaAct);
            }
        } else {
            sincronizarT10(encuestaAct);
        }
    }

    public void sincronizarT10(String enct10) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t10 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla10 = new HashMap<>();
        String Encuesta = enct10;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("cat", registros[i].get(1));
                regis.put("mar", registros[i].get(2));
                regis.put("sku", registros[i].get(3));
                regis.put("nsku", registros[i].get(4));
                regis.put("rta", registros[i].get(5));
                regis.put("mis", registros[i].get(6));
                regis.put("fcr", registros[i].get(7));
                regis.put("_ID", registros[i].get(8));
                regis.put("ID2", registros[i].get(9));
                regis.put("foto", registros[i].get(10));
                tabla10.put(enct10 + "_" + i, regis);
            }
            try {
                db.collection("t10_12160").document(Encuesta).set(tabla10).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct10++;

                        TextView tv = retornartext(10);
                        tv.setText("" + ct10);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t10_12160").document(s).set(tabla10).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t10").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T10", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla10.clear();
                sincronizarT11(encuestaAct);
            }
        } else {
            sincronizarT11(encuestaAct);
        }
    }

    public void sincronizarT11(String enct11) {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM t11 where encuesta='" + encuestaAct + "' and rta<>0  order by ID2", null);
        Map<String, Object> tabla11 = new HashMap<>();
        String Encuesta = enct11;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("codb", registros[i].get(1));
                regis.put("ida", registros[i].get(2));
                regis.put("nact", registros[i].get(3));
                regis.put("rta", registros[i].get(4));
                regis.put("coin", registros[i].get(5));
                regis.put("fcr", registros[i].get(6));
                regis.put("_ID", registros[i].get(7));
                regis.put("ID2", registros[i].get(8));
                regis.put("foto", registros[i].get(9));
                tabla11.put(enct11 + "_" + i, regis);
            }
            try {
                db.collection("t11_12160").document(Encuesta).set(tabla11).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        ct11++;

                        TextView tv = retornartext(11);
                        tv.setText("" + ct11);
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("t11_12160").document(s).set(tabla11).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("t11").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en T11", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tabla11.clear();
                sincronizarTF(encuestaAct);
            }
        } else {
            sincronizarTF(encuestaAct);
        }
    }

    public void sincronizarTF(String enctf) {
        boolean fotosubida = false;
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tf where encuesta='" + encuestaAct + "' order by ID2", null);
        Map<String, Object> tablaf = new HashMap<>();
        final String Encuesta = enctf;
        Map<String, Object> regis;

        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("path", registros[i].get(1));
//              String nombre = registros[i].get(1);
                regis.put("ref", registros[i].get(2));
                regis.put("esp", registros[i].get(3));
                regis.put("cat", registros[i].get(4));
                regis.put("preg", registros[i].get(5));
                regis.put("obs", registros[i].get(6));
                regis.put("_ID", registros[i].get(7));
                regis.put("ID2", registros[i].get(8));
                tablaf.put(enctf + "_" + i, regis);
            }
            try {
                db.collection("tf_12160").document(Encuesta).set(tablaf).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        cttf++;
                        TextView tv = retornartext(12);
                        tv.setText("" + cttf);
                        String encu = Encuesta;
                        if (cttf==Integer.parseInt(conteosin)){
                            sincf.setClickable(true);
                            sincd.setClickable(true);
                            botonr.setClickable(true);
                            Toast.makeText(context, "Sincronizacion de Datos Finalizada", Toast.LENGTH_LONG).show();
                            sincronizarTR();
                        }
                        operaciones.queryNoData("UPDATE t1 SET sincronizada='" + 1 + "' WHERE encuesta='" + encu + "'");
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("tf_12160").document(s).set(tablaf).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("tf").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en TF", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tablaf.clear();
                sincronizarTV(encuestaAct);
            }
        } else{
            sincronizarTV(encuestaAct);
        }

    }

    public void sincronizarTR() {
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tr", null);
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                encuestaAct = registros[i].get(0);
                Map<String, Object> regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("maquina", registros[i].get(1));
                regis.put("fecha_ini", registros[i].get(2));
                regis.put("fecha_fin", registros[i].get(3));
                regis.put("hora_ini", registros[i].get(4));
                String fechaC = fg.fechaActual(3);
                StringTokenizer st = new StringTokenizer(fechaC, " ");
                st.nextToken();
                regis.put("hora_fin", st.nextToken());
                regis.put("gpslat_ini", registros[i].get(6));
                regis.put("gpslon_ini", registros[i].get(7));
                regis.put("gpslat_fin", registros[i].get(8));
                regis.put("gpslon_fin", registros[i].get(9));
                regis.put("usuario", registros[i].get(10));
                regis.put("sincronizada", registros[i].get(11));
                regis.put("cliente_t", registros[i].get(12));
                regis.put("cliente_n", registros[i].get(13));
                regis.put("idrazon", registros[i].get(14));
                regis.put("nrazon", registros[i].get(15));
                regis.put("peratendio", registros[i].get(16));
                regis.put("observaciones", registros[i].get(17));
                regis.put("fcr", registros[i].get(18));
                regis.put("_ID", registros[i].get(19));
                try {
                    db.collection("tr_12160").document(registros[i].get(0)).set(regis).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void T) {
                            cttr++;
                            TextView tv = retornartext(13);
                            tv.setText("" + cttr);
                        }
                    });
                } catch (Exception e) {
                    final String s = registros[i].get(0);
                    db.collection("tr_12160").document(s).set(regis).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //db.collection("tr").document(s).set(cv2);
                            Toast.makeText(context, "La sincronización ha fallado en TR", Toast.LENGTH_SHORT).show();
                        }
                    });
                    regis = new HashMap<>();
                } finally {
                    regis = new HashMap<>();
                    sincronizarTF(encuestaAct);
                }
            }
        } else{
            Toast.makeText(context, "No hay Rechazos por Sincronizar", Toast.LENGTH_LONG).show();
        }
    }

    public void sincronizarTV(String enctf) {
        boolean fotosubida = false;
        ArrayList<String>[] registros = conGen.queryObjeto(context, "SELECT * FROM tv where encuesta='"+ encuestaAct + "'", null);
        Map<String, Object> tablaf = new HashMap<>();
        final String Encuesta = enctf;
        Map<String, Object> regis;
        if (registros != null) {
            for (int i = 0; i < registros.length; i++) {
                regis = new HashMap<>();
                regis.put("encuesta", registros[i].get(0));
                regis.put("idv", registros[i].get(1));
                regis.put("et", registros[i].get(2));
                regis.put("esp", registros[i].get(3));
                regis.put("cat", registros[i].get(4));
                regis.put("preg", registros[i].get(5));
                regis.put("rt", registros[i].get(6));
                regis.put("id2", registros[i].get(7));
                tablaf.put(enctf + "_" + i, regis);
            }
            try {
                db.collection("tv_12160").document(Encuesta).set(tablaf).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        cttv++;
                        TextView tv = retornartext(14);
                        tv.setText("" + cttv);
                        String encu = Encuesta;
                    }
                });
            } catch (Exception e) {
                final String s = Encuesta;
                db.collection("tv_12160").document(s).set(tablaf).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //db.collection("tf").document(s).set(cv2);
                        Toast.makeText(context, "La sincronización ha fallado en TV", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                tablaf.clear();

            }
        } else{
            Toast.makeText(context, "No hay registros x sincronizar en TV", Toast.LENGTH_SHORT).show();
        }

    }
    public static Sincronizar getInstance() {return activityA;}
}