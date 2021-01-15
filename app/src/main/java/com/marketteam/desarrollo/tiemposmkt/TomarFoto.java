package com.marketteam.desarrollo.tiemposmkt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.marketteam.desarrollo.tiemposmkt.R;

import java.io.File;
import java.util.ArrayList;

public class TomarFoto extends AppCompatActivity {

    private static int TAKE_PICTURE = 1;
    ImageView iv;
    String raiz, idt2, idt3, canalAct, subcAct, espAct, catAct, estndAct, path, nombreFoto, idSkuC, idActivo, idOrdenA, fototipo;
    OperacionesBDInterna operaciones;
    ConsultaGeneral conGen;
    FuncionesGenerales fg;
    ActualizarTablas at;
    ImageButton ok;
    TextView tv;
    static String databasename = "tiemposmkt.db";
    static TomarFoto activityA;
    Intent intx;
    LocationManager locationManager;
    Location location;
    RecyclerView recycler;
    ArrayList<RutaModel> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomar_foto);
        activityA = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    class Ruta extends RecyclerView.ViewHolder {
        TextView tvRuta;

        public Ruta(View itemView) {
            super(itemView);
            this.tvRuta = (TextView) itemView.findViewById(R.id.tvRutaFoto);
        }
    }

    class RutaModel {
        String ruta;

        public RutaModel(String rutaF) {
            this.ruta = rutaF;
        }
    }

    class RutaAdpater extends RecyclerView.Adapter<Ruta> {
        ArrayList<RutaModel> lista;

        public RutaAdpater(ArrayList<RutaModel> lista) {
            this.lista = lista;
        }

        @Override
        public Ruta onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Ruta(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_ruta_foto, parent, false));
        }

        @Override
        public void onBindViewHolder(Ruta holder, int position) {
            final Ruta holder2 = holder;
            final RutaModel rm = lista.get(position);
            holder2.tvRuta.setText(rm.ruta);
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("first", "error");
        }
        Criteria criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        operaciones = new OperacionesBDInterna(getBaseContext(),null);
        conGen = new ConsultaGeneral(getBaseContext(),databasename);
        fg = new FuncionesGenerales(getBaseContext(),databasename);
        at = new ActualizarTablas(getBaseContext(),databasename);
        iv = (ImageView) findViewById(R.id.imageViewFoto);
        //Traer variables y leer FOTO de ACT
        ArrayList<String> resCampos = fg.existeACT(5);
        idt2 = resCampos.get(0);
        idt3 = resCampos.get(1);
        canalAct = resCampos.get(2);
        subcAct = resCampos.get(3);
        espAct = resCampos.get(4);
        catAct = resCampos.get(5);
        estndAct = resCampos.get(6);
        //FOTO = 1 - Categoría sin estándares, 2 - Estándar, 3 - SKU
        ArrayList<String>[] objR = conGen.queryGeneral(getBaseContext(), "ACT", new String[]{"VAL"}, "VA='FOTO'");
        if (objR != null) {
            raiz = objR[0].get(0);
        }
        ok = (ImageButton) findViewById(R.id.ibOkFoto);
        tv = (TextView) findViewById(R.id.tvFotoC);
        recycler = (RecyclerView) findViewById(R.id.recyclerViewFoto);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        lista = new ArrayList<>();
        //Llenar el recycler con lo que contenga path
        String queryNombres = "SELECT path FROM tft WHERE encuesta='" + fg.getAuditoria() + "'";
        ArrayList<String>[] nFotos = conGen.queryObjeto2val(getBaseContext(), queryNombres, null);
        if (nFotos != null) {
            for (int i = 0; i < nFotos.length; i++) {
                String nombre = nFotos[i].get(0);
                lista.add(new RutaModel(nombre));
            }
            RutaAdpater ra = new RutaAdpater(lista);
            recycler.setAdapter(ra);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                location = loc;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void tomarFoto(View v) throws Exception {
        Thread two = new Thread() {
            @Override
            public void run() {
                File carpetaIMG = new File(Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/", "img");
                if ((raiz.equals("1")) || (raiz.equals("2")) || (raiz.equals("3"))) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (!carpetaIMG.exists()) {
                        carpetaIMG.mkdirs();
                    }
                    if (raiz.equals("1")) {
                        nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_" + espAct + "_" + catAct + ".jpg";
                    } else if (raiz.equals("2")) {
                        nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_" + espAct + "_" + catAct + "_" + estndAct + ".jpg";
                    } else if (raiz.equals("3")) {
                        nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_" + espAct + "_" + catAct + "_" + estndAct + "_sku.jpg";
                    }
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    //camara.putExtra(MediaStore.EXTRA_SIZE_LIMIT,"1677722");
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("4")) {
                    //Foto fachada
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_fachada.jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("5")) {
                    //Foto selfie
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    camara.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_selfie.jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("6")) {
                    //Rechazo
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_rechazo.jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("7")) {
                    //Premios1
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_Premios1.jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("8")) {
                    //Premios2
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_Premios2.jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("9")) {
                    //Activos
                    idActivo = fg.getActivo();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_activo_" + idActivo + ".jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("10")) {
                    //SKUs Calidad
                    idSkuC = fg.getSkuCalidad();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_skuCalidad_" + idSkuC + ".jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                } else if (raiz.equals("11")) {
                    //Activaciones
                    idOrdenA = fg.getordenTipoA();
                    fototipo = fg.getTipoTipoA();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    nombreFoto = fg.getAuditoria() + "_" + fg.fechaActual(2) + "_ACTIVACIONES" + fototipo + "_" + idOrdenA + ".jpg";
                    path = Environment.getExternalStorageDirectory() + "/DCIM/tiemposmkt/img/" + nombreFoto;
                    File imagen = new File(carpetaIMG, nombreFoto);
                    final Uri uri = Uri.fromFile(imagen);
                    camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camara, TAKE_PICTURE);
                }
            }
        };
        two.start();
        two.join();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                lista.add(new RutaModel(nombreFoto));
                tv.setText("Foto cargada");
                if (raiz.equals("1") || raiz.equals("2") || raiz.equals("3")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'FE" + espAct + "C" + catAct + "' , '" + espAct + "' , '" + catAct + "' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("4")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'FACHADA' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("5")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'SELFIE' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("6")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'RECHAZO' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("7")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'PREMIOS' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("8")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'PREMIOS' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("9")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'ACTIVO_" + idActivo + "' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("10")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'SKUCALIDAD_" + idSkuC + "' , '0' , '0' , '0' , '0')";
                    operaciones.queryNoData(queryFoto);
                } else if (raiz.equals("11")) {
                    String queryFoto = "INSERT INTO tft (encuesta,path,ref,esp,cat,preg,obs) VALUES ('" + fg.getAuditoria() + "' , '" + nombreFoto + "' , 'ACTIVACIONES" + fototipo + "_" + idOrdenA + "' , '0' , '0' , '" + idOrdenA + "' , '0')";
                    operaciones.queryNoData(queryFoto);
                }
                //
                operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FTOM'");
                if (TomarFoto.getInstance() != null) {
                    TomarFoto.getInstance().finish();
                }
                startActivity(new Intent(this, TomarFoto.class));
            }
        }
        /*try{
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            iv.setImageBitmap(bitmap);
            fOut.flush();
            fOut.close();
        } catch(FileNotFoundException e) {
            //UnableToSave();
        }
        catch(IOException e) {
            //UnableToSave();
        } finally {
            tv.setText("Foto cargada");
        }*/
    }

    public void terminar(View v) {
        String queryftom = "SELECT VAL FROM ACT where VA='FTOM'";
        ArrayList<String>[] ftom = conGen.queryObjeto2val(getBaseContext(), queryftom, null);
        String fototomada = ftom[0].get(0);
        String text = tv.getText().toString();
        if (fototomada.equals("1")) {
            if (raiz.equals("1")) {
                operaciones.queryNoData("UPDATE t3t SET eval='1' WHERE esp='" + espAct + "' AND cat='" + catAct + "'");
                Toast.makeText(getBaseContext(), "Foto almacenada", Toast.LENGTH_SHORT).show();
            } else if (raiz.equals("2")) {
                operaciones.queryNoData("UPDATE t3t SET eval='1' WHERE esp='" + espAct + "' AND cat='" + catAct + "'");
                Toast.makeText(getBaseContext(), "Foto almacenada", Toast.LENGTH_SHORT).show();
            } else if (raiz.equals("3")) {
                //¿Qué se hace en SKUs?
            } else if (raiz.equals("4")) {
                //foto fachada
                operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FF'");
                Toast.makeText(getBaseContext(), "Foto de fachada almacenada", Toast.LENGTH_SHORT).show();
            } else if (raiz.equals("5")) {
                //selfie
                operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='SLF'");
                Toast.makeText(getBaseContext(), "Selfie almacenada", Toast.LENGTH_SHORT).show();
            } else if (raiz.equals("9")) {
                operaciones.queryNoData("UPDATE t11t SET foto='1' WHERE encuesta='" + fg.getAuditoria() + "' AND ida='" + fg.getActivo() + "'");
                Toast.makeText(getBaseContext(), "Foto almacenada", Toast.LENGTH_SHORT).show();
            } else if (raiz.equals("10")) {
                operaciones.queryNoData("UPDATE t10t SET foto='1' WHERE encuesta='" + fg.getAuditoria() + "' AND sku='" + fg.getSkuCalidad() + "'");
                Toast.makeText(getBaseContext(), "Foto almacenada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Foto almacenada", Toast.LENGTH_SHORT).show();
            }
            if (raiz.equals("1")) {
                //Revisar si hay SKUs
                ArrayList<String>[] sku = conGen.queryGeneral(getBaseContext(), "t5t", new String[]{"sku"}, "esp='" + espAct + "' AND cat='" + catAct + "'");
                if (sku != null) {
                    //Si, si
                    //SET FL = 2 en ACT
                    operaciones.queryNoData("UPDATE ACT SET VAL='2' WHERE VA='FL'");
                }

                //Si no volver a categotías
            } else if (raiz.equals("2")) {
                //Revisar si hay SKUs
                //Si hay SKUs, ir a esa actividad
                ArrayList<String>[] sku = conGen.queryGeneral(getBaseContext(), "t5t", new String[]{"sku"}, "esp='14' AND cat='" + catAct + "' and rta='0'");
                if (sku != null) {
                    //SET FL = 1 en ACT
                    operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FL'");

                } else {
                    operaciones.queryNoData("UPDATE ACT SET VAL='1' WHERE VA='FL'");

                }
                //Si no, ir a categorías
            }
            if (raiz.equals("11")) {

                String queryOrden = "SELECT VAL FROM ACT where VA='ORDEN'";
                ArrayList<String>[] alorden = conGen.queryObjeto2val(getBaseContext(), queryOrden, null);
                String Orden = alorden[0].get(0);
                operaciones.queryNoData("UPDATE '302_PREGGEN' SET fto='1' where orden=" + Orden);
                String Tipo = "0";
                String queryActivacionesid = "SELECT tipo FROM '302_PREGGEN' where orden=" + Orden;
                ArrayList<String>[] actidpreg = conGen.queryObjeto2val(getBaseContext(), queryActivacionesid, null);
                Tipo = actidpreg[0].get(0);
            }

            if (raiz.equals("6")) {

                double[] gps;
                if (location == null) {
                    gps = new double[]{0, 0};
                    Toast.makeText(getBaseContext(), "No hay ubicación", Toast.LENGTH_LONG).show();
                } else {
                    gps = new double[]{location.getLatitude(), location.getLongitude()};
                }
                //Enviar GPS a finAuditoria()

            } else {
                intx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intx);
            }
        } else {
            Toast.makeText(getBaseContext(), "La foto no ha sido tomada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        operaciones.close();
        Glide.clear(iv);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static TomarFoto getInstance() {
        return activityA;
    }
}