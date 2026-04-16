package com.example.levelupdaily;

import android.app.Application;

import java.util.concurrent.ExecutorService;

public class ControladorAcceso {
    private final UsuarioDAO usuarioDao;
    private final ExecutorService executor;

    //Constructor
    public ControladorAcceso(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        usuarioDao = db.usuarioDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void autenticarUsuario(String nombre, String password, LoginCallback callback){
        executor.execute(()->{
            Usuario usuario = usuarioDao.login(nombre, password);
            if(usuario != null){
                callback.onSuccess(usuario);
            } else{
                callback.onError("Usuario o contraseña incorrectos");
            }
        });
    }

    public interface LoginCallback{
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
