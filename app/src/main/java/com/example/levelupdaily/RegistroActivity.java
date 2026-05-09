package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etPassword;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.EditNombre);
        etPassword = findViewById(R.id.EditPassword);
        btnRegistrar = findViewById(R.id.btnRegistro);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        ControladorRegistro control = new ControladorRegistro(getApplication());
        String nombre = etNombre.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(nombre.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            control.registrarUsuario(nombre, password, new ControladorRegistro.RegistroCallback() {
                @Override
                public void onSuccess(long id) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroActivity.this, "¡Usuario creado con éxito!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, AvatarRActivity.class);
                        // Pasamos el ID como entero para mantener consistencia
                        intent.putExtra("id_usuario", (int) id);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroActivity.this, error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}
