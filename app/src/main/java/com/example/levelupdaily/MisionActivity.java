package com.example.levelupdaily;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MisionActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etSubtareas;

    private Spinner spTipo;

    private Button btnGuardar;
    private Button btnFecha;

    private Calendar fechaSeleccionada = Calendar.getInstance();

    private AppDatabase db;

    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mision);

        etTitulo = findViewById(R.id.etTitulo);

        etSubtareas = findViewById(R.id.etSubtareas);

        spTipo = findViewById(R.id.spTipo);

        btnFecha = findViewById(R.id.btnFecha);

        btnGuardar = findViewById(R.id.btnGuardar);

        db = AppDatabase.getDatabase(this);

        //Tipos de misión
        String[] tipos = {
                "Principal",
                "Secundaria"
        };

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            userID = extras.getInt("id_usuario");
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        tipos
                );

        spTipo.setAdapter(adapter);

        //Seleccionar fecha
        btnFecha.setOnClickListener(v -> {

            int year =
                    fechaSeleccionada.get(Calendar.YEAR);

            int month =
                    fechaSeleccionada.get(Calendar.MONTH);

            int day =
                    fechaSeleccionada.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, y, m, d) -> {
                                fechaSeleccionada.set(y, m, d);

                                // Mostrarla en el TextView de forma legible
                                String fechaFormateada = d + "/" + (m + 1) + "/" + y;

                                btnFecha.setText(
                                        fechaFormateada
                                );
                            },
                            year,
                            month,
                            day
                    );

            dialog.show();
        });

        btnGuardar.setOnClickListener(
                v -> guardarMision()
        );
    }

    private void guardarMision() {

        String titulo =
                etTitulo.getText()
                        .toString()
                        .trim();

        String tipo =
                spTipo.getSelectedItem()
                        .toString();

        Date fecha =
                fechaSeleccionada.getTime();

        String textoSubtareas =
                etSubtareas.getText()
                        .toString()
                        .trim();

        //Validar t�tulo
        if(titulo.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese un título",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        //Validar subtareas
        if(textoSubtareas.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese submisiones",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        //Separar subtareas
        String[] subtareasRaw =
                textoSubtareas.split("\n");

        ArrayList<String> listaLimpia =
                new ArrayList<>();

        for(String s : subtareasRaw) {

            if(!s.trim().isEmpty()) {

                listaLimpia.add(
                        s.trim()
                );
            }
        }

        //VALIDAR PRINCIPAL
        if(tipo.equalsIgnoreCase("Principal")) {

            if(listaLimpia.size() < 3) {

                Toast.makeText(
                        this,
                        "La misi�n principal necesita m�nimo 3 subtareas",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            if(fecha == null) {

                Toast.makeText(
                        this,
                        "Seleccione fecha l�mite",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }
        }

        //VALIDAR SECUNDARIA
        if(tipo.equalsIgnoreCase("Secundaria")) {

            if(listaLimpia.size() < 1) {

                Toast.makeText(
                        this,
                        "La misi�n secundaria necesita m�nimo 1 subtarea",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }
        }

        //Crear misi�n
        Mision nuevaMision =
                new Mision(
                        userID,
                        titulo,
                        tipo,
                        fecha,
                        false
                );

        AppDatabase.databaseWriteExecutor.execute(() -> {

            //Guardar misi�n
            long idMision =
                    db.misionDAO()
                            .insertarMision(
                                    nuevaMision
                            );

            //Guardar subtareas
            for(String texto : listaLimpia) {

                SubMision submision =
                        new SubMision(
                                (int) idMision,
                                texto,
                                false
                        );

                db.subMisionDAO()
                        .insertarSubtarea(
                                submision
                        );
            }

            runOnUiThread(() -> {

                Toast.makeText(
                        this,
                        "Misi�n creada",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }
}