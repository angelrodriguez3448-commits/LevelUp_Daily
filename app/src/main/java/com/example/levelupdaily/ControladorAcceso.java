package com.example.levelupdaily;

import android.app.Application;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public class ControladorAcceso {
    private final UsuarioDAO usuarioDao;
    private final ExecutorService executor;

    public ControladorAcceso(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        usuarioDao = db.usuarioDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void autenticarUsuario(String nombre, String password, LoginCallback callback){
        executor.execute(()->{
            try {
                Usuario usuario = usuarioDao.login(nombre, password);
                if(usuario != null){
                    callback.onSuccess(usuario);
                } else{
                    callback.onError("Usuario o contraseña incorrectos");
                }
            } catch (Exception e) {
                Log.e("Login", "Error en login: " + e.getMessage());
                callback.onError("Error de base de datos. Por favor, limpia los datos de la app.");
            }
        });
    }

    public interface LoginCallback{
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
