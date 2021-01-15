package com.marketteam.desarrollo.tiemposmkt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuInicio extends AppCompatActivity {



    ConsultaGeneral cGeneral;
    OperacionesBDInterna operaciones;
    FuncionesGenerales fg;
    ActualizarTablas at;
    static MenuInicio activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio);
        activityA = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        cGeneral = new ConsultaGeneral(getBaseContext(),"tiemposmkt.db");
        operaciones = new OperacionesBDInterna(getBaseContext(),"tiemposmkt.db");
        fg = new FuncionesGenerales(getBaseContext(),"tiemposmkt.db");
        at = new ActualizarTablas(getBaseContext(),"Tiemposmkt.db");
        fg.ultimaPantalla("MenuI");
        Button iniciar = (Button) findViewById(R.id.buttonIniciarAud);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAuditoria();
            }
        });
        //Mostrar encuestas con estado Aplazado

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void iniciarAuditoria(){
        at.insertarT1();
        Intent lista = new Intent(this,InfoGeneral.class);
        startActivity(lista);
    }


    public void sincronizar(View vista) {
        Intent sincronizar = new Intent(this,Sincronizar.class);
        startActivity(sincronizar);
    }

    public void MenuLateral(View vista) {
        fg.MenuLateral(vista);
    }

    public void cerrarsesion(View v) {
        PopUps pop = new PopUps();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        pop.popUpConf(getBaseContext(), inflater, 7, 14);
    }



    public void Volver(View v){
        startActivity(new Intent(this,Login.class));
    }

    public static MenuInicio getInstance(){ return activityA; }
}