package com.example.levelupdaily;

import android.app.Application;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public class ControladorRegistro {
    private final UsuarioDAO usuarioDao;
    private final ExecutorService executor;

    public ControladorRegistro(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        usuarioDao = db.usuarioDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void registrarUsuario(String nombre, String password, RegistroCallback callback){
        executor.execute(()->{
            try{
                Usuario nuevoUser = new Usuario();
                nuevoUser.nombreUsuario = nombre;
                nuevoUser.password = password;

                long id = usuarioDao.registrarUsuario(nuevoUser);
                Log.d("Registro", "Usuario creado con ID: " + id);
                callback.onSuccess(id);
            } catch (Exception e){
                Log.e("Registro", "Error al registrar: " + e.getMessage());
                callback.onError("El nombre de usuario ya está en uso o hubo un error.");
            }
        });
    }

    public interface RegistroCallback{
        void onSuccess(long id);
        void onError(String error);
    }
}
