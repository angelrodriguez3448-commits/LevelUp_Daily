package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private AvatarUsuario avatarG;
    private TextView tNombreAvatar, tHP, tOro, tNivel, tXP;
    private ImageButton btnMisiones, btnStore, btnConf, btnInventario;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tNombreAvatar = findViewById(R.id.tNombreAvatar);
        tHP = findViewById(R.id.tHP);
        tOro = findViewById(R.id.tOro);
        tNivel = findViewById(R.id.tNivel);
        tXP = findViewById(R.id.tXP);
        
        btnMisiones = findViewById(R.id.mision);
        btnConf = findViewById(R.id.conf);
        btnStore = findViewById(R.id.store);
        btnInventario = findViewById(R.id.btnAbrirInventario);

        idUser = getIntent().getIntExtra("id_usuario", -1);
        if (idUser == -1) idUser = getIntent().getIntExtra("ID_user", -1);

        if (idUser == -1) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnMisiones.setOnClickListener(v -> abrirPantalla(MisionesActivity.class));
        btnStore.setOnClickListener(v -> abrirPantalla(StoreActivity.class));
        btnConf.setOnClickListener(v -> abrirPantalla(ConfiguracionActivity.class));
        btnInventario.setOnClickListener(v -> abrirPantalla(InventarioActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosPersonaje();
    }

    private void cargarDatosPersonaje() {
        ControladorAvatar controlador = new ControladorAvatar(getApplication());
        controlador.obtenerAvatar(idUser, avatar -> {
            runOnUiThread(() -> {
                if (avatar != null) {
                    this.avatarG = avatar;
                    actualizarInterfaz();
                } else {
                    Intent intent = new Intent(this, AvatarRActivity.class);
                    intent.putExtra("id_usuario", (long) idUser);
                    startActivity(intent);
                }
            });
        });
    }

    private void abrirPantalla(Class<?> cls) {
        if (avatarG != null) {
            Intent intent = new Intent(this, cls);
            intent.putExtra("id_usuario", idUser);
            startActivity(intent);
        }
    }

    private void actualizarInterfaz() {
        if (avatarG != null) {
            tNombreAvatar.setText(avatarG.avatar_name);
            tHP.setText("HP: " + avatarG.hp);
            tOro.setText("Oro: " + avatarG.oro);
            tNivel.setText("Nivel: " + avatarG.nivel);
            tXP.setText("XP: " + avatarG.xp);
        }
    }
}
