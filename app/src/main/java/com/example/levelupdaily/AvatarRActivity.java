package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AvatarRActivity extends AppCompatActivity {

    private EditText etNomAvat;
    private Button btnFoto, btnFinlaizar;
    private ImageView ivAvat;
    private String uriAvat;
    private int idUsuario; // Cambiado a int para consistencia

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_r);
        
        // Intentar obtener el ID como int o long y convertirlo
        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        if (idUsuario == -1) {
            idUsuario = (int) getIntent().getLongExtra("id_usuario", -1);
        }

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNomAvat = findViewById(R.id.EditNomAvatar);
        btnFoto = findViewById(R.id.btnFoto);
        ivAvat = findViewById(R.id.AvatarImage);
        btnFinlaizar = findViewById(R.id.btnRegistro);

        btnFoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btnFinlaizar.setOnClickListener(v -> registrarAvatar());
    }

    ActivityResultLauncher<PickVisualMediaRequest>
            pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if(uri != null){
                    uriAvat = uri.toString();
                    ivAvat.setImageURI(uri);
                }
    });

    private void registrarAvatar() {
        ControladorAvatar control = new ControladorAvatar(getApplication());
        String nombre = etNomAvat.getText().toString();

        if(nombre.isEmpty()){
            Toast.makeText(this, "Por favor escribe un nombre", Toast.LENGTH_SHORT).show();
        } else{
            control.registrarAvatar((long) idUsuario, nombre, uriAvat, new ControladorAvatar.RegistroCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(()->{
                        Toast.makeText(AvatarRActivity.this, "¡Avatar creado con éxito!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AvatarRActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(()-> Toast.makeText(AvatarRActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
