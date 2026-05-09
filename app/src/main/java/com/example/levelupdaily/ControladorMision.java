package com.example.levelupdaily;

import android.app.Application;

import java.util.HashMap;
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

    public void guardarMisionConSubmisiones(Mision mision, List<String> submisiones, CrearCallback callback){
        executor.execute(()->{
            try{
                long idMision = misionDAO.insertarMision(mision);

                for(String texto: submisiones){
                    SubMision subMision = new SubMision(
                            (int) idMision,
                            texto,
                            false
                    );
                    subMisionDAO.insertarSubmision(subMision);
                }

                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // Nuevo metodo en ControladorMision.java
    public void cargarDatosMisiones(int userID, CargaMisionesCallback callback) {
        executor.execute(() -> {
            List<Mision> principales = misionDAO.obtenerPrincipalesActivas(userID);
            List<Mision> secundarias = misionDAO.obtenerSecundariasActivas(userID);

            HashMap<Integer, List<SubMision>> mapaPrincipales = new HashMap<>();
            HashMap<Integer, List<SubMision>> mapaSecundarias = new HashMap<>();

            for (Mision m : principales) {
                mapaPrincipales.put(m.getId(), subMisionDAO.obtenerSubmisiones(m.getId()));
            }
            for (Mision m : secundarias) {
                mapaSecundarias.put(m.getId(), subMisionDAO.obtenerSubmisiones(m.getId()));
            }

            callback.onDatosCargados(principales, mapaPrincipales, secundarias, mapaSecundarias);
        });
    }

    public void completarSubmision(int idSubmision, SimpleCallback callback) {
        executor.execute(() -> {
            try {
                subMisionDAO.completarSubmision(idSubmision);
                callback.onDone();
            } catch (Exception e) {
                // Manejo de error opcional
            }
        });
    }

    public void verificarYFinalizarMision(int idMision, int idUser, FinalizacionCallBack callBack){
        executor.execute(()->{
            int pendientes = subMisionDAO.obtenerSubmisionesPendientes(idMision);

            if(pendientes == 0){
                misionDAO.completarMision(idMision);

                int oro = 15;
                int xp = 20;

                callBack.onMisionFinalizada(oro, xp);
            } else{
                callBack.onMisionSigueActiva();
            }
        });
    }

    public void obtenerHistorial(int idUser, HistorialCallback callback){
        executor.execute(()->{
            List<Mision> completadas = misionDAO.obtenerHistorialMisiones(idUser);
            callback.onHistorialCargado(completadas);
        });
    }

    // Interfaces de callback

    public interface CrearCallback{
        void onSuccess();

        void onError(String error);
    }

    public interface FinalizacionCallBack{
        void onMisionFinalizada(int oro, int xp);

        void onMisionSigueActiva();
    }

    public interface CargaMisionesCallback {
        void onDatosCargados(List<Mision> p, HashMap<Integer, List<SubMision>> mp,
                             List<Mision> s, HashMap<Integer, List<SubMision>> ms);
    }

    public interface SimpleCallback {
        void onDone();
    }

    public interface HistorialCallback{
        void onHistorialCargado(List<Mision> misionesCompletadas);
    }
}
