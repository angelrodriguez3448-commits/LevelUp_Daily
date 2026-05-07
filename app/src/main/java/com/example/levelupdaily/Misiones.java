package com.example.levelupdaily;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
public class Misiones extends AppCompatActivity  {
    private Button NM;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misiones);

        NM = (Button) findViewById(R.id.nuevamis);
        NM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Misiones.this, NewMis.class);
                startActivity(intent);
            }
        });


    }
}
