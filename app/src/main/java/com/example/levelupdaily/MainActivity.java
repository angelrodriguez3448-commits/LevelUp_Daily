package com.example.levelupdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.PrimaryKey;

public class MainActivity extends AppCompatActivity {

    private Button btnRegistrar, btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegistrar = findViewById(R.id.btnRegistro);
        btnEntrar = findViewById(R.id.btnLogin);

        btnEntrar.setOnClickListener(v ->Login());
        btnRegistrar.setOnClickListener(v -> Registrar());
    }

    private void Login(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void Registrar(){
        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
    }
}