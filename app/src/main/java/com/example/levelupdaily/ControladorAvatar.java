package com.example.levelupdaily;

import android.app.Application;
import android.util.Log;

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
                AvatarUsuario nuevoAva = new AvatarUsuario();
                nuevoAva.id_usuario = Math.toIntExact(idUs);
                nuevoAva.avatar_name = nombre;
                nuevoAva.imagen = uriAva;
                nuevoAva.hp = 100;
                nuevoAva.oro = 200; // Iniciamos con 200 de oro
                nuevoAva.xp = 0;
                nuevoAva.nivel = 1;

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
            // FIX: Siempre llamar al callback, incluso si es null
            callback.onLoaded(avatar);
            
            if (avatar != null) {
                Log.d("DB_TEST", "Avatar encontrado: " + avatar.avatar_name);
            } else {
                Log.e("DB_TEST", "No se encontró avatar para el ID usuario: " + idUser);
            }
        });
    }

    public void modificarHP(AvatarUsuario avatar, int cambioHP, DatosCallback callback){
        executor.execute(()->{
            int nuevahp = avatar.hp + cambioHP;
            avatar.hp = Math.max(0, nuevahp);
            avatarDao.actualizarProgreso(avatar);
            callback.onLoaded(avatar);
        });
    }

    public void modificarXP(AvatarUsuario avatar, int cambioXP, DatosCallback callback){
        executor.execute(()->{
            int nuevaXP = avatar.xp + cambioXP;
            if(nuevaXP >= 100){
                avatar.xp = (nuevaXP - 100);
                avatar.nivel++;
            }else{
                avatar.xp = nuevaXP;
            }
            avatarDao.actualizarProgreso(avatar);
            callback.onLoaded(avatar);
        });
    }

    public interface DatosCallback {
        void onLoaded(AvatarUsuario avatar);
    }

    public interface RegistroCallback{
        void onSuccess();
        void onError(String error);
    }
}
