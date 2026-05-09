package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;

import android.widget.ImageView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    ControladorAvatar controladorAvatar = new ControladorAvatar(getApplication());
    ControladorMision controladorMision = new ControladorMision(getApplication());
    private ExpandableListView listaPrincipales;
    private ExpandableListView listaSecundarias;

    private Button btnCrearMision, btnTienda, btnVerHistorial;

    private TextView tNombreAvatar;
    private TextView tHP;
    private TextView tOro;
    private TextView tNivel;
    private TextView tXP;
    private ImageView iAvatar;
    private AppDatabase db;

    //Temporal
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listaPrincipales = findViewById(R.id.listaPrincipales);

        listaSecundarias = findViewById(R.id.listaSecundarias);

        btnCrearMision = findViewById(R.id.btnCrearMision);

        btnTienda = findViewById(R.id.btnTienda);

        btnVerHistorial = findViewById(R.id.btnHistorial);

        tNombreAvatar = findViewById(R.id.tNombreAvatar);

        tHP = findViewById(R.id.tHP);

        tOro = findViewById(R.id.tOro);

        tNivel = findViewById(R.id.tNivel);

        tXP = findViewById(R.id.tXP);

        iAvatar = findViewById(R.id.iAvatar);

        db = AppDatabase.getDatabase(this);

        btnCrearMision.setOnClickListener(v -> {

            Intent intent =
                    new Intent(HomeActivity.this,
                            MisionActivity.class);

            intent.putExtra("id_usuario", userID);

            startActivity(intent);
        });

        btnTienda.setOnClickListener(v -> {

            Intent intent =
                    new Intent(HomeActivity.this,
                            StoreActivity.class);

            intent.putExtra("id_usuario", userID);

            startActivity(intent);
        });

        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistorialActivity.class);
            intent.putExtra("id_usuario", userID);
            startActivity(intent);
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            userID = extras.getInt("id_usuario");
        }
        //CLICK SUBTAREAS PRINCIPALES
        listaPrincipales.setOnChildClickListener(
                (parent, v, groupPosition,
                 childPosition, id) -> {

                    marcarSubtarea(
                            parent,
                            groupPosition,
                            childPosition
                    );

                    return true;
                });

        //CLICK SUBTAREAS SECUNDARIAS
        listaSecundarias.setOnChildClickListener(
                (parent, v, groupPosition,
                 childPosition, id) -> {

                    marcarSubtarea(
                            parent,
                            groupPosition,
                            childPosition
                    );

                    return true;
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarMisiones();
        cargarAvatar();
    }

    private void cargarMisiones() {
        controladorMision.cargarDatosMisiones(userID, (p, mp, s, ms) -> {
            runOnUiThread(() -> {
                listaPrincipales.setAdapter(new MisionExpandableAdapter(this, p, mp));
                listaSecundarias.setAdapter(new MisionExpandableAdapter(this, s, ms));
            });
        });
    }

    private void marcarSubtarea(
            ExpandableListView parent,
            int groupPosition,
            int childPosition) {

        MisionExpandableAdapter adapter =
                (MisionExpandableAdapter)
                        parent.getExpandableListAdapter();

        SubMision submision =
                (SubMision)
                        adapter.getChild(
                                groupPosition,
                                childPosition
                        );

        // 1. Ahora llamamos al controlador de misiones para completar la tarea
        controladorMision.completarSubmision(submision.id_submisiones, () -> {

            // 2. Procesar recompensas (5 oro, 10 xp por subtarea)
            controladorAvatar.procesarRecompensa(userID, 5, 10, avatar -> {

                // 3. Verificar si esto cerró la misión completa
                controladorMision.verificarYFinalizarMision(submision.id_mision, userID, new ControladorMision.FinalizacionCallBack() {
                    @Override
                    public void onMisionFinalizada(int oro, int xp) {
                        // Recompensa extra por misión (15 oro, 20 xp según tu ControladorMision)
                        controladorAvatar.procesarRecompensa(userID, oro, xp, a -> runOnUiThread(() -> {
                            Toast.makeText(HomeActivity.this, "¡Misión de Leyenda Completada!", Toast.LENGTH_SHORT).show();
                            cargarAvatar();
                            cargarMisiones();
                        }));
                    }

                    @Override
                    public void onMisionSigueActiva() {
                        runOnUiThread(() -> {
                            cargarAvatar();
                            cargarMisiones();
                        });
                    }

                });
            });
        });
    }

    private void cargarAvatar() {

        controladorAvatar.obtenerAvatar(userID, avatarUsuario -> {
            if(avatarUsuario != null) {

                runOnUiThread(() -> {

                    tNombreAvatar.setText(
                            avatarUsuario.avatar_name
                    );

                    tHP.setText(
                            "HP: " + avatarUsuario.hp
                    );

                    tOro.setText(
                            "Oro: " + avatarUsuario.oro
                    );

                    tNivel.setText(
                            "Nivel: " + avatarUsuario.nivel
                    );

                    tXP.setText(
                            "XP: " + avatarUsuario.xp
                    );

                    // CARGAR PERSONAJE
                    switch (avatarUsuario.imagen) {

                        case "avatar_guerrero":

                            iAvatar.setImageResource(
                                    R.drawable.avatar_guerrero
                            );
                            break;

                        case "avatar_mago":

                            iAvatar.setImageResource(
                                    R.drawable.avatar_mago
                            );
                            break;

                        case "avatar_arquero":

                            iAvatar.setImageResource(
                                    R.drawable.avatar_arquero
                            );
                            break;

                        case "avatar_paladin":

                            iAvatar.setImageResource(
                                    R.drawable.avatar_paladin
                            );
                            break;
                    }
                });
            }
        });
    }
}
