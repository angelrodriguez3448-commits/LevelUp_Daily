package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AvatarRActivity extends AppCompatActivity {

    private EditText etNomAvat;
    private Button  btnFinlaizar;
    private ImageView ivAvat;
    private long usuario;

    private String imagenSeleccionada = "avatar_guerrero";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_r);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = extras.getLong("id_usuario");
        }

        etNomAvat = findViewById(R.id.EditNomAvatar);
        ivAvat = findViewById(R.id.AvatarImage);
        ivAvat.setImageResource(
                R.drawable.avatar_guerrero
        );
        ImageView avatar1 = findViewById(R.id.avatar1);
        ImageView avatar2 = findViewById(R.id.avatar2);
        ImageView avatar3 = findViewById(R.id.avatar3);
        ImageView avatar4 = findViewById(R.id.avatar4);
        avatar1.setOnClickListener(v -> {

            imagenSeleccionada = "avatar_guerrero";

            ivAvat.setImageResource(
                    R.drawable.avatar_guerrero
            );
        });

        avatar2.setOnClickListener(v -> {

            imagenSeleccionada = "avatar_mago";

            ivAvat.setImageResource(
                    R.drawable.avatar_mago
            );
        });

        avatar3.setOnClickListener(v -> {

            imagenSeleccionada = "avatar_arquero";

            ivAvat.setImageResource(
                    R.drawable.avatar_arquero
            );
        });

        avatar4.setOnClickListener(v -> {

            imagenSeleccionada = "avatar_paladin";

            ivAvat.setImageResource(
                    R.drawable.avatar_paladin
            );
        });
        btnFinlaizar = findViewById(R.id.btnRegistro);


        btnFinlaizar.setOnClickListener(v -> registrarAvatar());
    }

    private void registrarAvatar() {
        ControladorAvatar control = new ControladorAvatar(getApplication());
        String nombre = etNomAvat.getText().toString();

        if(nombre.isEmpty()){
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
        } else{
            control.registrarAvatar(usuario, nombre, imagenSeleccionada, new ControladorAvatar.RegistroCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(()->{
                        Toast.makeText(AvatarRActivity.this, "Avatar creado", Toast.LENGTH_SHORT).show();
                    });
                    Intent intent = new Intent(AvatarRActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(()->{
                        Toast.makeText(AvatarRActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}
