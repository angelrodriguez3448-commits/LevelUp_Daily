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

        btnRegistrar.setOnClickListener(v->registrarUsuario());
    }

    private void registrarUsuario() {
        ControladorRegistro control = new ControladorRegistro(getApplication());
        String nombre = etNombre.getText().toString();
        String password = etPassword.getText().toString();

        if(nombre.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
        } else{
            control.registrarUsuario(nombre, password, new ControladorRegistro.RegistroCallback() {
                @Override
                public void onSuccess(long id) {
                    runOnUiThread(()->{
                        Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    });
                    Intent intent = new Intent(RegistroActivity.this, AvatarRActivity.class);
                    intent.putExtra("id_usuario", id);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(()->{
                        Toast.makeText(RegistroActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}
