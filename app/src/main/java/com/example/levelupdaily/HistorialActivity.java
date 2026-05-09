package com.example.levelupdaily;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistorialActivity extends AppCompatActivity {
    private RecyclerView rvHistorial;
    private ControladorMision controladorMision;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        rvHistorial = findViewById(R.id.rvHistorial);
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        controladorMision = new ControladorMision(getApplication());
        userID = getIntent().getIntExtra("id_usuario", -1);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        cargarHistorial();
    }

    private void cargarHistorial() {
        controladorMision.obtenerHistorial(userID, misiones -> {
            runOnUiThread(() -> {
                // Aquí usarías un adaptador sencillo para el RecyclerView
                HistorialAdapter adapter = new HistorialAdapter(misiones);
                rvHistorial.setAdapter(adapter);
            });
        });
    }
}