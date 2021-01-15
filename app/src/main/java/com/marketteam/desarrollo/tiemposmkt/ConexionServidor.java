package com.marketteam.desarrollo.tiemposmkt;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConexionServidor extends AsyncTask<String, Void, Void> {

    String URLws = "http://52.0.134.0:8080/MT_AppMovil//HTTPWrapperWS?wservice=1";
    String user = "";
    String pasw = "";
    Context context;

    BufferedReader reader = null;
    String data = "";
    String Content;
    boolean existe = false;

    public ConexionServidor(String user, String pass, Context context){
        this.user = user;
        this.pasw = pass;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(user.equals("") || pasw.equals("")){
            Toast.makeText(context, "Valores vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    protected Void doInBackground(String... urls) {

        URLws = URLws + "&param1=" + user.toLowerCase() + "&param2=" + pasw.toLowerCase() + "&param5=si";
        try
        {
            String serverURL = URLws;
            URL url = new URL(serverURL);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Content = reader.readLine().toString(); // content te regresara en string lo que conteste tu ws
            Toast.makeText(context,Content, Toast.LENGTH_LONG).show();
            Log.i("Contenido ws", Content);
            if(!Content.equals("") && Content != null){
                existe = true;
            }
        }
        catch(Exception e)
        {
            Log.e("Exception", e.toString());
        }

        finally
        {
            try { reader.close(); }
            catch(Exception ex) {}
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public boolean conectar (){
        doInBackground();
        return existe;
    }
}
