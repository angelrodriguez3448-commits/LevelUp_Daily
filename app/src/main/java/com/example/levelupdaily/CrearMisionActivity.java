package com.example.levelupdaily;

import android.app.DatePickerDialog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.collections.UArraySortingKt;

public class CrearMisionActivity extends AppCompatActivity {
    ControladorMision control = new ControladorMision(getApplication());
    private EditText etTitulo, etMultiDescricion;
    private Button btnDate, btnAñadirS, btnCrearM;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private TextView tvFechaLimite;
    private List<EditText> listaEtSubmisiones = new ArrayList<>(); //Lista para EditText
    private LinearLayout containerS;

    private int userActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_mision);

         userActual = getIntent().getIntExtra("ID_user", -1);

        etTitulo = findViewById(R.id.editTitulo);
        etMultiDescricion = findViewById(R.id.editMultiDescripcion);
        tvFechaLimite = findViewById(R.id.tvFecha);
        btnDate = findViewById(R.id.btnDatePicker);
        btnAñadirS = findViewById(R.id.btnAñadirSubM);
        btnCrearM = findViewById(R.id.btnCrearMision);
        containerS = findViewById(R.id.containerSubMisiones);

        btnDate.setOnClickListener(v->{
            //Obtener la fecha actual
            int anno = fechaSeleccionada.get(Calendar.YEAR);
            int mes = fechaSeleccionada.get(Calendar.MONTH);
            int dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dataPickerDialog = new DatePickerDialog(this, (view, y, m, d)->{
                fechaSeleccionada.set(y, m, d);

                // Mostramos la fecha en el TextView
                String fechaFormateada = d + "/" + (m+1) + "/" + y;
                tvFechaLimite.setText(fechaFormateada);
            }, anno, mes, dia);

            dataPickerDialog.show();
        });

        btnAñadirS.setOnClickListener(v->{
            EditText nuevaSub = new EditText(this);
            nuevaSub.setHint("Descrpcion de la submision " + (listaEtSubmisiones.size() + 1));
            // Añadimos el elemento a la interfaz
            containerS.addView(nuevaSub);
            // Añadimos el elemento a la lista
            listaEtSubmisiones.add(nuevaSub);
        });

        btnCrearM.setOnClickListener(v->guardarMision());
    }

    private void guardarMision(){
        String titulo = etTitulo.getText().toString();
        String desc = etMultiDescricion.getText().toString();
        Date fecha = fechaSeleccionada.getTime();

        Mision nuevaMision = new Mision();
        nuevaMision.id_usuario = userActual;
        nuevaMision.titulo = titulo;
        nuevaMision.descripcion = desc;
        nuevaMision.fecha_limite = fecha;

        // Recolectar la lista de submisiones
        List<SubMision> listaParaGuardar = new ArrayList<>();

        for(EditText et : listaEtSubmisiones){
            String textoSub = et.getText().toString().trim();
            if(!textoSub.isEmpty()){
                SubMision sub = new SubMision();
                sub.descripcion = textoSub;
                listaParaGuardar.add(sub);
            }
        }

        control.guardarMision(nuevaMision, listaParaGuardar, new ControladorMision.CrearCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(()->{
                    Toast.makeText(CrearMisionActivity.this, "Mision y submisiones creadas", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(()->{
                    Toast.makeText(CrearMisionActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
