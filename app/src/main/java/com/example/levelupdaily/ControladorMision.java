package com.example.levelupdaily;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ControladorMision {
    private final MisionDAO misionDAO;
    private final SubMisionDAO subMisionDAO;
    private final ExecutorService executor;

    public ControladorMision(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        misionDAO = db.misionDAO();
        subMisionDAO = db.subMisionDAO();
        executor =  AppDatabase.databaseWriteExecutor;
    }

    public void guardarMision(Mision mision, List<SubMision> submisiones, CrearCallback callback){
        executor.execute(()->{
            try{
                misionDAO.insertarMisionConSubmisiones(mision, submisiones, subMisionDAO);

                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface CrearCallback{
        void onSuccess();

        void onError(String error);
    }
}
