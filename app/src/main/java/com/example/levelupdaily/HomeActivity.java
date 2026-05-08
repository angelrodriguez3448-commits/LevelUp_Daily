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

public class HomeActivity extends AppCompatActivity {

    private ExpandableListView listaPrincipales;
    private ExpandableListView listaSecundarias;

    private Button btnCrearMision;

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

        AppDatabase.databaseWriteExecutor.execute(() -> {

            //MISIONES
            List<Mision> principales = db.misionDAO().obtenerPrincipales(userID);

            List<Mision> secundarias = db.misionDAO().obtenerSecundarias(userID);

            //MAPAS
            HashMap<Integer,
                    List<SubMision>> mapaPrincipales =
                    new HashMap<>();

            HashMap<Integer,
                    List<SubMision>> mapaSecundarias =
                    new HashMap<>();

            //SUBTAREAS PRINCIPALES
            for(Mision m : principales) {

                mapaPrincipales.put(
                        m.getId(),

                        db.subMisionDAO()
                                .obtenerSubmisiones(
                                        m.getId()
                                )
                );
            }

            //SUBTAREAS SECUNDARIAS
            for(Mision m : secundarias) {

                mapaSecundarias.put(
                        m.getId(),

                        db.subMisionDAO()
                                .obtenerSubmisiones(
                                        m.getId()
                                )
                );
            }

            runOnUiThread(() -> {

                MisionExpandableAdapter adapterP =
                        new MisionExpandableAdapter(
                                this,
                                principales,
                                mapaPrincipales
                        );

                MisionExpandableAdapter adapterS =
                        new MisionExpandableAdapter(
                                this,
                                secundarias,
                                mapaSecundarias
                        );

                listaPrincipales.setAdapter(adapterP);

                listaSecundarias.setAdapter(adapterS);

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

        AppDatabase.databaseWriteExecutor.execute(() -> {

            //COMPLETAR SUBTAREA
            db.subMisionDAO()
                    .completarSubmision(
                            submision.id_submisiones
                    );

            //RECOMPENSAS
            AvatarUsuario avatar =
                    db.avatarDao()
                            .obtenerAvatarPorUsuario(userID);

            avatar.xp += 10;
            avatar.oro += 5;

            //SUBIR NIVEL
            if(avatar.xp >= 100) {

                avatar.nivel += 1;
                avatar.xp = 0;
            }

            db.avatarDao()
                    .actualizarProgreso(avatar);

            runOnUiThread(() -> {

                cargarMisiones();
                cargarAvatar();

            });
        });
    }

    private void cargarAvatar() {

        AppDatabase.databaseWriteExecutor.execute(() -> {

            AvatarUsuario avatar =
                    db.avatarDao()
                            .obtenerAvatarPorUsuario(userID);

            if(avatar != null) {

                runOnUiThread(() -> {

                    tNombreAvatar.setText(
                            avatar.avatar_name
                    );

                    tHP.setText(
                            "HP: " + avatar.hp
                    );

                    tOro.setText(
                            "Oro: " + avatar.oro
                    );

                    tNivel.setText(
                            "Nivel: " + avatar.nivel
                    );

                    tXP.setText(
                            "XP: " + avatar.xp
                    );

                    // CARGAR PERSONAJE
                    switch (avatar.imagen) {

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
