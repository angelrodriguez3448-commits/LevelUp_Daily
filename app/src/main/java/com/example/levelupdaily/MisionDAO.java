package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MisionDAO {

    @Insert
    long insertarMision(Mision mision);

    // Misiones completadas (historial)
    @Query("SELECT * FROM misiones WHERE id_usuario = :idUsuario AND completada = 1")
    List<Mision> obtenerHistorialMisiones(int idUsuario);

    @Query("UPDATE misiones SET completada = 1 WHERE id = :idMision")
    void completarMision(int idMision);

    // Misiones activas (incompletas)
    @Query(" SELECT * FROM misiones WHERE tipo = 'Principal' AND id_usuario = :idUsuario AND completada = 0")
    List<Mision> obtenerPrincipalesActivas(int idUsuario);

    @Query("SELECT * FROM misiones WHERE tipo = 'Secundaria' AND id_usuario = :idUsuario AND completada = 0")
    List<Mision> obtenerSecundariasActivas(int idUsuario);

    @Query("SELECT * FROM misiones WHERE id_usuario = :idUsuario AND completada = 0 AND fechaLimite < :currentDate")
    List<Mision> obtenerMisionesVencidas(int idUsuario, long currentDate);

    @Query("DELETE FROM misiones WHERE id = :idMision")
    void eliminarMision(int idMision);
}