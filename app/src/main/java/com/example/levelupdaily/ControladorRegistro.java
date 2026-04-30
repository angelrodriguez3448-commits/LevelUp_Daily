package com.example.levelupdaily;

import android.app.Application;

import java.util.concurrent.ExecutorService;

public class ControladorRegistro {
    private final UsuarioDAO usuarioDao;
    private final ExecutorService executor;

    //Constructor
    public ControladorRegistro(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        usuarioDao = db.usuarioDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void registrarUsuario(String nombre, String password, RegistroCallback callback){
        executor.execute(()->{
            try{
                //Acceso a la DB
                Usuario nuevoUser = new Usuario();
                nuevoUser.nombreUsuario = nombre;
                nuevoUser.password = password;

                long id = usuarioDao.registrarUsuario(nuevoUser);

                //Notificar exito
                callback.onSuccess(id);
            } catch (android.database.sqlite.SQLiteConstraintException e){
                callback.onError("El nombre de usuario ya esta en uso");
            }
        });
    }

    //Interfaz para comunicacion con la Activity
    public interface RegistroCallback{
        void onSuccess(long id);

        void onError(String error);
    }
}