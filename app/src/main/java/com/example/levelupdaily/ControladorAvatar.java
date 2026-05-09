package com.example.levelupdaily;

import android.app.Application;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public class ControladorAvatar {
    private final AvatarDAO avatarDao;
    private final InventarioDao inventarioDao;
    private final ExecutorService executor;

    public ControladorAvatar(Application aplication){
        AppDatabase db = AppDatabase.getDatabase(aplication);
        avatarDao = db.avatarDao();
        inventarioDao = db.inventarioDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void registrarAvatar(int idUs, String nombre, String uriAva, RegistroCallback callback){
        executor.execute(()->{
            try{
                AvatarUsuario nuevoAva = new AvatarUsuario();
                nuevoAva.id_usuario = idUs;
                nuevoAva.avatar_name = nombre;
                nuevoAva.imagen = uriAva;
                nuevoAva.hp = 100;
                nuevoAva.oro = 250;
                nuevoAva.xp = 0;
                nuevoAva.nivel = 1;
                nuevoAva.tiene_escudo = false;

                avatarDao.registrarAvatar(nuevoAva);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void obtenerAvatar(int idUser, DatosCallback callback){
        executor.execute(()->{
            AvatarUsuario avatar = avatarDao.obtenerAvatarPorUsuario(idUser);
            callback.onLoaded(avatar);
        });
    }

    public void procesarRecompensa(int idUser, int oro, int xp, DatosCallback callback) {
        executor.execute(() -> {
            AvatarUsuario avatar = avatarDao.obtenerAvatarPorUsuario(idUser);
            if (avatar != null) {
                avatar.oro += oro;
                avatar.xp += xp;
                if (avatar.xp >= 100) {
                    avatar.xp -= 100;
                    avatar.nivel++;
                }
                avatarDao.actualizarProgreso(avatar);
                callback.onLoaded(avatar);
            }
        });
    }

    public void recibirDanio(int idUser, int danio, DatosCallback callback) {
        executor.execute(() -> {
            AvatarUsuario avatar = avatarDao.obtenerAvatarPorUsuario(idUser);
            if (avatar != null) {
                // LÓGICA DEL ESCUDO: Protege del daño y desaparece
                if (avatar.tiene_escudo) {
                    avatar.tiene_escudo = false;
                    Log.d("Avatar", "El escudo absorbió el impacto.");
                } else {
                    avatar.hp -= danio;
                    if (avatar.hp <= 0) {
                        avatar.hp = 0;
                        procesarDerrota(avatar);
                    }
                }
                avatarDao.actualizarProgreso(avatar);
                callback.onLoaded(avatar);
            }
        });
    }

    private void procesarDerrota(AvatarUsuario avatar) {
        // Reducir el 30% del oro
        int oroPerdido = (int) (avatar.oro * 0.30);
        avatar.oro -= oroPerdido;
        if (avatar.oro < 0) avatar.oro = 0;

        // Restaurar vida a 50
        avatar.hp = 50;
    }

    public void usarItem(int idUser, AvatarItem item, ItemUsoCallback callback) {
        executor.execute(() -> {
            AvatarUsuario avatar = avatarDao.obtenerAvatarPorUsuario(idUser);
            if (avatar == null) {
                callback.onResult(false, "No se encontró el personaje");
                return;
            }

            boolean exito = false;
            String mensaje = "";

            switch (item.nombre) {
                case "Minipoción de HP":
                    if (avatar.hp >= 100) {
                        mensaje = "Tu salud ya está al máximo";
                    } else {
                        avatar.hp = Math.min(100, avatar.hp + 20);
                        exito = true;
                    }
                    break;
                case "Maxipoción de HP":
                    if (avatar.hp >= 100) {
                        mensaje = "Tu salud ya está al máximo";
                    } else {
                        avatar.hp = 100;
                        exito = true;
                    }
                    break;
                case "Poción de Vida":
                    if (avatar.hp >= 100) {
                        mensaje = "Tu salud ya está al máximo";
                    } else {
                        avatar.hp = Math.min(100, avatar.hp + 25);
                        exito = true;
                    }
                    break;
                case "Escudo Divino":
                    if (avatar.tiene_escudo) {
                        mensaje = "Ya tienes un escudo activo";
                    } else {
                        avatar.tiene_escudo = true;
                        exito = true;
                    }
                    break;
                case "Poción de XP":
                    avatar.xp += 20;
                    if (avatar.xp >= 100) {
                        avatar.xp -= 100;
                        avatar.nivel++;
                    }
                    exito = true;
                    break;
                default:
                    mensaje = "Este objeto no se puede usar manualmente";
                    break;
            }

            if (exito) {
                inventarioDao.usarItem(avatar.id_avatar, item.id_item);
                avatarDao.actualizarProgreso(avatar);
                callback.onResult(true, "¡" + item.nombre + " usado!");
            } else {
                callback.onResult(false, mensaje);
            }
        });
    }

    public interface DatosCallback {
        void onLoaded(AvatarUsuario avatar);
    }

    public interface RegistroCallback{
        void onSuccess();
        void onError(String error);
    }

    public interface ItemUsoCallback {
        void onResult(boolean exito, String mensaje);
    }
}
