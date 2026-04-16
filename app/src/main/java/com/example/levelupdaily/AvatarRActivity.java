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
    private long usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_r);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = extras.getLong("id_usuario");
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

    // Lanzador para abrir la galeria
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
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
        } else{
            control.registrarAvatar(usuario, nombre, uriAvat, new ControladorAvatar.RegistroCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(()->{
                        Toast.makeText(AvatarRActivity.this, "Avatar creado", Toast.LENGTH_SHORT).show();
                    });
                    Intent intent = new Intent(AvatarRActivity.this, MainActivity.class);
                    startActivity(intent);
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
