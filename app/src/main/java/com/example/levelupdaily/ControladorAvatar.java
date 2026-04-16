package com.example.levelupdaily;

import android.app.Application;

import java.util.concurrent.ExecutorService;

public class ControladorAvatar {
    private final AvatarDAO avatarDao;
    private final ExecutorService executor;

    public ControladorAvatar(Application aplication){
        AppDatabase db = AppDatabase.getDatabase(aplication);
        avatarDao = db.avatarDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void registrarAvatar(long idUs, String nombre, String uriAva, RegistroCallback callback){
        executor.execute(()->{
            try{
                //Acceso a la DB
                AvatarUsuario nuevoAva = new AvatarUsuario();
                nuevoAva.id_usuario = Math.toIntExact(idUs);
                nuevoAva.avatar_name = nombre;
                nuevoAva.imagen = uriAva;
                nuevoAva.oro = 0;
                nuevoAva.xp = 0;
                nuevoAva.nivel = 0;

                //Notificar exito
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface RegistroCallback{
        void onSuccess();

        void onError(String error);
    }
}
