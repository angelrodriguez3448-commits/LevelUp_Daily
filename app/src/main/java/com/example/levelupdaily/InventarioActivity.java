package com.example.levelupdaily;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InventarioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ItemActividad> datos = new ArrayList<>();
    private RecyclerView.Adapter<MyViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        // OBTENEMOS EL ID DEL USUARIO DESDE EL INTENT (Enviado desde HomeActivity)
        int idUser = getIntent().getIntExtra("id_usuario", -1);
        if (idUser == -1) idUser = getIntent().getIntExtra("ID_user", -1);

        if (idUser == -1) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. Definimos el adaptador de forma segura
        adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.activity_item, parent, false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                ItemActividad item = datos.get(position);
                // FIX: Validación de nulos para evitar que la app se cierre
                if (item != null && item.item != null) {
                    holder.tvNombre.setText(item.item.nombre);
                    holder.tvCantidad.setText("x" + item.cantidad);
                }
            }

            @Override
            public int getItemCount() {
                return datos.size();
            }
        };

        recyclerView.setAdapter(adapter);

        // 2. Cargamos los datos de Room usando el ID real del usuario
        final int finalIdUser = idUser;
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            
            // Primero buscamos el avatar que pertenece a este usuario
            AvatarUsuario avatar = db.avatarDao().obtenerAvatarPorUsuario(finalIdUser);
            
            if (avatar != null) {
                // Obtenemos el inventario usando el ID del AVATAR real
                List<ItemActividad> listaDB = db.inventarioDao().getInventarioPorAvatar(avatar.id_avatar);

                runOnUiThread(() -> {
                    if (listaDB != null) {
                        datos.clear();
                        datos.addAll(listaDB);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.nombreItem);
            tvCantidad = itemView.findViewById(R.id.cantidadItem);
        }
    }
}
