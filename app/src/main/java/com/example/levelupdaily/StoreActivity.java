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

    private TextView tvTimer, tvOro, tvTimerFreeGold;
    private Button btnClaimGold;
    private RecyclerView rvTienda;
    private StoreAdapter adapter;
    private List<ItemTiendaDisplay> listaTienda = new ArrayList<>();
    private AvatarUsuario avatarActual;
    private int idUser;

    private static final long RESTOCK_INTERVAL = 3600000; // 1 hora
    private static final long FREE_GOLD_INTERVAL = 900000; // 15 minutos
    private CountDownTimer countDownTimerRestock, countDownTimerFreeGold;

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
        tvTimerFreeGold = findViewById(R.id.tvTimerFreeGold);
        btnClaimGold = findViewById(R.id.btnClaimGold);
        rvTienda = findViewById(R.id.rvTienda);

        rvTienda.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StoreAdapter();
        rvTienda.setAdapter(adapter);

        btnClaimGold.setOnClickListener(v -> reclamarOroGratis());

        cargarDatosUsuario();
        verificarYRestockear();
        verificarYTimerFreeGold();
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

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            
            // Asegurar items base
            List<AvatarItem> nuevos = new ArrayList<>();
            nuevos.add(crearItem("Escudo Divino", "Protege de perder HP una vez.", 50));
            nuevos.add(crearItem("Minipoción de HP", "Recupera 20 HP.", 50));
            nuevos.add(crearItem("Poción de Vida", "Recupera 25 HP.", 60));
            nuevos.add(crearItem("Maxipoción de HP", "Recupera toda la vida.", 150));
            nuevos.add(crearItem("Poción de XP", "Otorga 20 puntos de experiencia.", 80));
            db.tiendaDao().insertarItemsBase(nuevos);
            db.tiendaDao().eliminarSaltoTemporal();

            List<ItemTiendaDisplay> stockActual = db.tiendaDao().getStockTienda();

            if (currentTime - lastRestock >= RESTOCK_INTERVAL || lastRestock == 0 || stockActual.isEmpty()) {
                ejecutarRestock(db, currentTime);
            } else {
                runOnUiThread(() -> {
                    listaTienda.clear();
                    listaTienda.addAll(stockActual);
                    adapter.notifyDataSetChanged();
                    long remaining = RESTOCK_INTERVAL - (currentTime - lastRestock);
                    iniciarTimerRestock(remaining);
                });
            }
        });
    }

    private void ejecutarRestock(AppDatabase db, long time) {
        List<AvatarItem> itemsBase = db.tiendaDao().getTodosLosItems();
        Random r = new Random();
        List<TiendaItem> stockNuevos = new ArrayList<>();
        for (AvatarItem item : itemsBase) {
            stockNuevos.add(new TiendaItem(item.id_item, r.nextInt(10) + 1));
        }
        db.tiendaDao().actualizarStock(stockNuevos);

        getSharedPreferences("TiendaPrefs", MODE_PRIVATE).edit()
                .putLong("last_restock_time", time).apply();

        List<ItemTiendaDisplay> finalStock = db.tiendaDao().getStockTienda();
        runOnUiThread(() -> {
            listaTienda.clear();
            listaTienda.addAll(finalStock);
            adapter.notifyDataSetChanged();
            iniciarTimerRestock(RESTOCK_INTERVAL);
        });
    }

    private AvatarItem crearItem(String n, String d, int p) {
        AvatarItem item = new AvatarItem();
        item.nombre = n;
        item.descripcion = d;
        item.precio = p;
        return item;
    }

    private void iniciarTimerRestock(long ms) {
        if (countDownTimerRestock != null) countDownTimerRestock.cancel();
        countDownTimerRestock = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "Restock en: %02d:%02d", min, sec));
            }
            @Override public void onFinish() { verificarYRestockear(); }
        }.start();
    }

    private void verificarYTimerFreeGold() {
        SharedPreferences prefs = getSharedPreferences("TiendaPrefs", MODE_PRIVATE);
        long lastClaim = prefs.getLong("last_free_gold_time", 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastClaim >= FREE_GOLD_INTERVAL) {
            runOnUiThread(() -> {
                tvTimerFreeGold.setText("¡Recompensa lista!");
                btnClaimGold.setEnabled(true);
            });
        } else {
            iniciarTimerFreeGold(FREE_GOLD_INTERVAL - (currentTime - lastClaim));
        }
    }

    private void iniciarTimerFreeGold(long ms) {
        runOnUiThread(() -> btnClaimGold.setEnabled(false));
        if (countDownTimerFreeGold != null) countDownTimerFreeGold.cancel();
        countDownTimerFreeGold = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tvTimerFreeGold.setText(String.format(Locale.getDefault(), "Disponible en: %02d:%02d", min, sec));
            }
            @Override
            public void onFinish() {
                tvTimerFreeGold.setText("¡Recompensa lista!");
                btnClaimGold.setEnabled(true);
            }
        }.start();
    }

    private void reclamarOroGratis() {
        if (avatarActual == null) return;
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            avatarActual.oro += 50;
            db.avatarDao().actualizarProgreso(avatarActual);
            getSharedPreferences("TiendaPrefs", MODE_PRIVATE).edit()
                    .putLong("last_free_gold_time", System.currentTimeMillis()).apply();
            runOnUiThread(() -> {
                tvOro.setText("Oro: " + avatarActual.oro);
                iniciarTimerFreeGold(FREE_GOLD_INTERVAL);
                Toast.makeText(this, "¡+50 Oro reclamado!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void realizarCompra(ItemTiendaDisplay display) {
        if (avatarActual == null || avatarActual.oro < display.item.precio) {
            Toast.makeText(this, "Oro insuficiente", Toast.LENGTH_SHORT).show();
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            TiendaItem ti = db.tiendaDao().getTiendaItem(display.item.id_item);
            if (ti == null || ti.stock <= 0) return;

            avatarActual.oro -= display.item.precio;
            db.avatarDao().actualizarProgreso(avatarActual);
            ti.stock--;
            db.tiendaDao().updateTiendaItem(ti);

            List<ItemActividad> inv = db.inventarioDao().getInventarioPorAvatar(avatarActual.id_avatar);
            AvatarInventario exist = null;
            for(ItemActividad ia : inv) {
                if(ia.item.id_item == display.item.id_item) {
                    exist = new AvatarInventario(avatarActual.id_avatar, ia.item.id_item, ia.cantidad + 1);
                    break;
                }
            }
            if(exist == null) exist = new AvatarInventario(avatarActual.id_avatar, display.item.id_item, 1);
            db.inventarioDao().agregarAlInventario(exist);

            runOnUiThread(() -> {
                tvOro.setText("Oro: " + avatarActual.oro);
                display.stock--;
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "¡Compraste " + display.item.nombre + "!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_tienda, p, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            ItemTiendaDisplay d = listaTienda.get(pos);
            if (d.item != null) {
                h.tvNombre.setText(d.item.nombre);
                h.tvDesc.setText(d.item.descripcion);
                h.tvPrecio.setText(d.item.precio + " Oro");
                h.tvStock.setText("Stock: " + d.stock);
                h.btnComprar.setEnabled(d.stock > 0);
                h.btnComprar.setOnClickListener(v -> realizarCompra(d));
            }
        }
        @Override public int getItemCount() { return listaTienda.size(); }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimerRestock != null) countDownTimerRestock.cancel();
        if (countDownTimerFreeGold != null) countDownTimerFreeGold.cancel();
    }
}
