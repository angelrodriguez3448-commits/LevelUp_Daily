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

        btnLogin.setOnClickListener(v->autenticarUsuario());
    }

    private void autenticarUsuario(){
        ControladorAcceso control = new ControladorAcceso(getApplication());
        String nombre = etNombre.getText().toString();
        String password = etPassword.getText().toString();

        if (nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        control.autenticarUsuario(nombre, password, new ControladorAcceso.LoginCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(()-> {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // Unificamos la clave a "id_usuario" y el tipo a int (que es lo que tiene el objeto Usuario)
                    intent.putExtra("id_usuario", usuario.id_usuario);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(()-> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
