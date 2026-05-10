package com.example.levelupdaily;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class NotificacionWorker extends Worker {
    public NotificacionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // 1. Definir el margen de "un día" (24 horas en milisegundos)
        long ahora = System.currentTimeMillis();
        long unDiaDespues = ahora + (24 * 60 * 60 * 1000);

        // 2. Consultar misiones que vencen en este rango
        // Pasamos 'ahora' para no notificar misiones que ya vencieron
        List<Mision> misionesPorVencer = db.misionDAO().obtenerMisionesPorVencer(ahora, unDiaDespues);

        if (!misionesPorVencer.isEmpty()) {
            String mensaje = "Tienes " + misionesPorVencer.size() + " misiones que expiran pronto. ¡No dejes que tu HP baje!";
            enviarNotificacion("⚠️ ¡Misión en Peligro!", mensaje);
        }

        return Result.success();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void enviarNotificacion(String titulo, String mensaje){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CANAL_MISIONES")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }
}
