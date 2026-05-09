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
    private ControladorAvatar controladorAvatar;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        idUser = getIntent().getIntExtra("id_usuario", -1);
        if (idUser == -1) idUser = getIntent().getIntExtra("ID_user", -1);

        if (idUser == -1) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        controladorAvatar = new ControladorAvatar(getApplication());
        recyclerView = findViewById(R.id.recyclerInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.activity_item, parent, false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                ItemActividad itemAct = datos.get(position);
                if (itemAct != null && itemAct.item != null) {
                    holder.tvNombre.setText(itemAct.item.nombre);
                    holder.tvCantidad.setText("x" + itemAct.cantidad);
                    
                    // Hacer que el item sea clickable
                    holder.itemView.setOnClickListener(v -> {
                        usarObjeto(itemAct.item);
                    });
                }
            }

            @Override
            public int getItemCount() {
                return datos.size();
            }
        };

        recyclerView.setAdapter(adapter);
        cargarInventario();
    }

    private void cargarInventario() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            AvatarUsuario avatar = db.avatarDao().obtenerAvatarPorUsuario(idUser);
            if (avatar != null) {
                List<ItemActividad> listaDB = db.inventarioDao().getInventarioPorAvatar(avatar.id_avatar);
                runOnUiThread(() -> {
                    datos.clear();
                    if (listaDB != null) datos.addAll(listaDB);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void usarObjeto(AvatarItem item) {
        controladorAvatar.usarItem(idUser, item, (exito, mensaje) -> {
            runOnUiThread(() -> {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                if (exito) {
                    cargarInventario(); // Refrescar lista si se consumió el item
                }
            });
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
