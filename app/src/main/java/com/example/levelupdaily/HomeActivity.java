package com.example.levelupdaily;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    //Objeto avatar global
    private AvatarUsuario avatarG;
    private TextView tNombreAvatar, tHP, tOro, tNivel, tXP;
    private ImageView iAvatar;
    //Botones de prueba
    //private Button btnHP, btnXP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Recuperar el ID del usuario
        int idUser = getIntent().getIntExtra("ID_user", -1);

        Toast.makeText(HomeActivity.this, "Id user" + idUser, Toast.LENGTH_SHORT).show();

        tNombreAvatar = findViewById(R.id.tNombreAvatar);
        tHP = findViewById(R.id.tHP);
        tOro = findViewById(R.id.tOro);
        tNivel = findViewById(R.id.tNivel);
        iAvatar = findViewById(R.id.iAvatar);
        tXP = findViewById(R.id.tXP);
        //Botones de prueba
        /*
        btnHP = findViewById(R.id.btnHP);
        btnXP = findViewById(R.id.btnXP);
         */

        ControladorAvatar controlador = new ControladorAvatar(getApplication());

        controlador.obtenerAvatar(idUser, avatar -> {
            this.avatarG = avatar;
            actualizarInterfaz();
        });

        //Botones de prueba
        /*
        btnHP.setOnClickListener(v->{
            if(avatarG != null){
                int numRandom = (int) (Math.random()*(-10) - 5);
                controlador.modificarHP(avatarG, numRandom, avatar -> {
                    this.avatarG = avatar;
                    actualizarInterfaz();
                });
            }
        });
        btnXP.setOnClickListener(v->{
            if(avatarG != null){
                int numRandom = (int) (Math.random()*20) + 10;
                controlador.modificarXP(avatarG, numRandom, avatar -> {
                    this.avatarG = avatar;
                    actualizarInterfaz();
                });
            }
        });
         */
        //Fin botones de prueba
    }

    private void actualizarInterfaz(){
        runOnUiThread(()->{
            tNombreAvatar.setText("" + avatarG.avatar_name);
            tHP.setText("HP: " + avatarG.hp);
            tOro.setText("Oro: " + avatarG.oro);
            tNivel.setText("Nivel: " + avatarG.nivel);
            tXP.setText("XP: " + avatarG.xp);

            if(avatarG.imagen != null){
                //iAvatar.setImageURI(Uri.parse(avatar.imagen));
            }

            Log.d("UI_TEST", "Vistas actualizadas en pantalla");
        });
    }
}
