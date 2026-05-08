package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MisionDAO {

    @Insert
    long insertarMision(Mision mision);

    @Query("SELECT * FROM misiones WHERE id_usuario = :idUsuario")
    List<Mision> obtenerMisiones(int idUsuario);

    @Query("UPDATE misiones SET completada = 1 WHERE id = :idMision")
    void completarMision(int idMision);

    @Query(" SELECT * FROM misiones WHERE tipo = 'Principal' AND id_usuario = :idUsuario")
    List<Mision> obtenerPrincipales(int idUsuario);

    @Query("SELECT * FROM misiones WHERE tipo = 'Secundaria' AND id_usuario = :idUsuario")
    List<Mision> obtenerSecundarias(int idUsuario);
}