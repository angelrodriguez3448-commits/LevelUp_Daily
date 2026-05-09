package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private ControladorAvatar controladorAvatar;
    private ControladorMision controladorMision;
    private ExpandableListView listaPrincipales;
    private ExpandableListView listaSecundarias;

    private Button btnCrearMision, btnTienda, btnInventario;
    private TextView tNombreAvatar, tHP, tOro, tNivel, tXP;
    private ImageView iAvatar, imgShieldActive;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar controladores
        controladorAvatar = new ControladorAvatar(getApplication());
        controladorMision = new ControladorMision(getApplication());

        // Vincular vistas
        listaPrincipales = findViewById(R.id.listaPrincipales);
        listaSecundarias = findViewById(R.id.listaSecundarias);
        btnCrearMision = findViewById(R.id.btnCrearMision);
        btnTienda = findViewById(R.id.btnTienda);
        btnInventario = findViewById(R.id.btnInventario);
        
        tNombreAvatar = findViewById(R.id.tNombreAvatar);
        tHP = findViewById(R.id.tHP);
        tOro = findViewById(R.id.tOro);
        tNivel = findViewById(R.id.tNivel);
        tXP = findViewById(R.id.tXP);
        iAvatar = findViewById(R.id.iAvatar);
        imgShieldActive = findViewById(R.id.imgShieldActive);

        // Obtener ID de usuario
        userID = getIntent().getIntExtra("id_usuario", -1);
        if (userID == -1) {
            userID = getIntent().getIntExtra("ID_user", -1);
        }

        // Configurar clics
        btnCrearMision.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MisionActivity.class);
            intent.putExtra("id_usuario", userID);
            startActivity(intent);
        });

        btnTienda.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
            intent.putExtra("id_usuario", userID);
            startActivity(intent);
        });

        btnInventario.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, InventarioActivity.class);
            intent.putExtra("id_usuario", userID);
            startActivity(intent);
        });

        setupListListeners();
    }

    private void setupListListeners() {
        listaPrincipales.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            marcarSubtarea(parent, groupPosition, childPosition);
            return true;
        });

        listaSecundarias.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            marcarSubtarea(parent, groupPosition, childPosition);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userID != -1) {
            cargarMisiones();
            cargarAvatar();
        }
    }

    private void cargarMisiones() {
        controladorMision.cargarDatosMisiones(userID, (p, mp, s, ms) -> {
            runOnUiThread(() -> {
                listaPrincipales.setAdapter(new MisionExpandableAdapter(this, p, mp));
                listaSecundarias.setAdapter(new MisionExpandableAdapter(this, s, ms));
            });
        });
    }

    private void marcarSubtarea(ExpandableListView parent, int groupPosition, int childPosition) {
        MisionExpandableAdapter adapter = (MisionExpandableAdapter) parent.getExpandableListAdapter();
        SubMision submision = (SubMision) adapter.getChild(groupPosition, childPosition);

        controladorMision.completarSubmision(submision.id_submisiones, () -> {
            controladorAvatar.procesarRecompensa(userID, 5, 10, avatar -> {
                controladorMision.verificarYFinalizarMision(submision.id_mision, userID, new ControladorMision.FinalizacionCallBack() {
                    @Override
                    public void onMisionFinalizada(int oro, int xp) {
                        controladorAvatar.procesarRecompensa(userID, oro, xp, a -> runOnUiThread(() -> {
                            Toast.makeText(HomeActivity.this, "¡Misión Completada!", Toast.LENGTH_SHORT).show();
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
                    tNombreAvatar.setText(avatarUsuario.avatar_name);
                    tHP.setText("HP: " + avatarUsuario.hp);
                    tOro.setText("Oro: " + avatarUsuario.oro);
                    tNivel.setText("Nivel: " + avatarUsuario.nivel);
                    tXP.setText("XP: " + avatarUsuario.xp);

                    // Mostrar/Ocultar escudo
                    if (imgShieldActive != null) {
                        imgShieldActive.setVisibility(avatarUsuario.tiene_escudo ? View.VISIBLE : View.GONE);
                    }

                    // CARGAR PERSONAJE
                    if (avatarUsuario.imagen != null) {
                        switch (avatarUsuario.imagen) {
                            case "avatar_guerrero":
                                iAvatar.setImageResource(R.drawable.avatar_guerrero);
                                break;
                            case "avatar_mago":
                                iAvatar.setImageResource(R.drawable.avatar_mago);
                                break;
                            case "avatar_arquero":
                                iAvatar.setImageResource(R.drawable.avatar_arquero);
                                break;
                            case "avatar_paladin":
                                iAvatar.setImageResource(R.drawable.avatar_paladin);
                                break;
                        }
                    }
                });
            }
        });
    }
}
