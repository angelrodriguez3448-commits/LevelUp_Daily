package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etNombre, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNombre = findViewById(R.id.EditNombre);
        etPassword = findViewById(R.id.EditPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> autenticarUsuario());
    }

    private void autenticarUsuario(){
        ControladorAcceso control = new ControladorAcceso(getApplication());
        String nombre = etNombre.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        control.autenticarUsuario(nombre, password, new ControladorAcceso.LoginCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // Pasamos el ID asegurando que sea un int
                    intent.putExtra("id_usuario", (int) usuario.id_usuario);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
