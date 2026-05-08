package com.example.levelupdaily;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StoreActivity extends AppCompatActivity {

    private TextView tvTimer, tvOro;
    private RecyclerView rvTienda;
    private StoreAdapter adapter;
    private List<ItemTiendaDisplay> listaTienda = new ArrayList<>();
    private AvatarUsuario avatarActual;
    private int idUser;

    private static final long RESTOCK_INTERVAL = 3600000; // 1 hora en ms
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        idUser = getIntent().getIntExtra("id_usuario", -1);
        if (idUser == -1) {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTimer = findViewById(R.id.tvTimerRestock);
        tvOro = findViewById(R.id.tvOroDisponible);
        rvTienda = findViewById(R.id.rvTienda);

        rvTienda.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StoreAdapter();
        rvTienda.setAdapter(adapter);

        cargarDatosUsuario();
        verificarYRestockear();
    }

    private void cargarDatosUsuario() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            avatarActual = db.avatarDao().obtenerAvatarPorUsuario(idUser);
            runOnUiThread(() -> {
                if (avatarActual != null) {
                    tvOro.setText("Oro: " + avatarActual.oro);
                }
            });
        });
    }

    private void verificarYRestockear() {
        SharedPreferences prefs = getSharedPreferences("TiendaPrefs", MODE_PRIVATE);
        long lastRestock = prefs.getLong("last_restock_time", 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRestock >= RESTOCK_INTERVAL || lastRestock == 0) {
            restockearTienda(currentTime);
        } else {
            long remaining = RESTOCK_INTERVAL - (currentTime - lastRestock);
            iniciarTimer(remaining);
            cargarStockActual();
        }
    }

    private void restockearTienda(long time) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            
            // 1. Asegurarnos que existan items en la base de datos
            List<AvatarItem> itemsBase = db.tiendaDao().getTodosLosItems();
            if (itemsBase.isEmpty()) {
                List<AvatarItem> nuevos = new ArrayList<>();
                nuevos.add(crearItem("Escudo Divino", "Protege de perder HP una vez.", 50));
                nuevos.add(crearItem("Salto Temporal", "Completa una misión automáticamente.", 150));
                nuevos.add(crearItem("Poción de XP", "Otorga 20 puntos de experiencia.", 80));
                db.tiendaDao().insertarItemsBase(nuevos);
                itemsBase = db.tiendaDao().getTodosLosItems();
            }

            // 2. Generar stock aleatorio
            Random r = new Random();
            List<TiendaItem> stockNuevos = new ArrayList<>();
            for (AvatarItem item : itemsBase) {
                int cantidadAleatoria = r.nextInt(10) + 1; // Entre 1 y 10
                stockNuevos.add(new TiendaItem(item.id_item, cantidadAleatoria));
            }
            db.tiendaDao().actualizarStock(stockNuevos);

            // 3. Guardar tiempo
            getSharedPreferences("TiendaPrefs", MODE_PRIVATE).edit()
                    .putLong("last_restock_time", time).apply();

            runOnUiThread(() -> {
                iniciarTimer(RESTOCK_INTERVAL);
                cargarStockActual();
            });
        });
    }

    private AvatarItem crearItem(String n, String d, int p) {
        AvatarItem item = new AvatarItem();
        item.nombre = n;
        item.descripcion = d;
        item.precio = p;
        return item;
    }

    private void cargarStockActual() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            List<ItemTiendaDisplay> stock = db.tiendaDao().getStockTienda();
            runOnUiThread(() -> {
                listaTienda.clear();
                listaTienda.addAll(stock);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void iniciarTimer(long ms) {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "Restock en: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                verificarYRestockear();
            }
        }.start();
    }

    private void realizarCompra(ItemTiendaDisplay display) {
        if (avatarActual == null) return;
        if (avatarActual.oro < display.item.precio) {
            Toast.makeText(this, "Oro insuficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            
            // Verificamos stock en tiempo real
            TiendaItem ti = db.tiendaDao().getTiendaItem(display.item.id_item);
            if (ti == null || ti.stock <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Sin stock disponible", Toast.LENGTH_SHORT).show());
                return;
            }

            // 1. Descontar Oro
            avatarActual.oro -= display.item.precio;
            db.avatarDao().actualizarProgreso(avatarActual);

            // 2. Descontar Stock
            ti.stock--;
            db.tiendaDao().updateTiendaItem(ti);

            // 3. Agregar al Inventario (Lógica de incremento)
            AvatarInventario exist = null;
            List<ItemActividad> inv = db.inventarioDao().getInventarioPorAvatar(avatarActual.id_avatar);
            for(ItemActividad ia : inv) {
                if(ia.item.id_item == display.item.id_item) {
                    exist = new AvatarInventario(avatarActual.id_avatar, ia.item.id_item, ia.cantidad + 1);
                    break;
                }
            }
            if(exist == null) {
                exist = new AvatarInventario(avatarActual.id_avatar, display.item.id_item, 1);
            }
            db.inventarioDao().agregarAlInventario(exist);
            
            runOnUiThread(() -> {
                tvOro.setText("Oro: " + avatarActual.oro);
                cargarStockActual();
                Toast.makeText(this, "¡Compraste " + display.item.nombre + "!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tienda, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemTiendaDisplay display = listaTienda.get(position);
            holder.tvNombre.setText(display.item.nombre);
            holder.tvDesc.setText(display.item.descripcion);
            holder.tvPrecio.setText("Precio: " + display.item.precio + " Oro");
            holder.tvStock.setText("Stock: " + display.stock);
            
            holder.btnComprar.setEnabled(display.stock > 0);
            holder.btnComprar.setOnClickListener(v -> realizarCompra(display));
        }

        @Override
        public int getItemCount() {
            return listaTienda.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvDesc, tvPrecio, tvStock;
            Button btnComprar;
            public ViewHolder(View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreTienda);
                tvDesc = itemView.findViewById(R.id.tvDescTienda);
                tvPrecio = itemView.findViewById(R.id.tvPrecioTienda);
                tvStock = itemView.findViewById(R.id.tvStockTienda);
                btnComprar = itemView.findViewById(R.id.btnComprar);
            }
        }
    }
}
