package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubMisionDAO {

    @Insert
    void insertarSubtarea(SubMision subMision);

    @Query("SELECT * FROM subMisiones WHERE id_mision = :idMision")
    List<SubMision> obtenerSubmisiones(int idMision);

    @Query("UPDATE submisiones SET completada = 1 WHERE id_submisiones = :idSubmision")
    void completarSubmision(int idSubmision);

    @Query("SELECT COUNT(*) FROM submisiones WHERE id_mision = :idMision AND completada = 0")
    int obtenerSubmisionesPendientes(int idMision);
}