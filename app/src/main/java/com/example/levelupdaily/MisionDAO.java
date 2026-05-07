package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MisionDAO {

    @Insert
    long guardarMision(Mision mision);

    @Transaction
    default void insertarMisionConSubmisiones(Mision mision, List<SubMision> subs, SubMisionDAO subMisionDAO){
        long id = guardarMision(mision);
        for (SubMision s : subs){
            s.id_mision = (int) id;
            subMisionDAO.guardarSubMision(s);
        }
    }

    @Query("SELECT * FROM misiones WHERE id_usuario = :userID")
    Mision obtenerMisionesPorUsuario(int userID);

    @Query("SELECT * FROM misiones WHERE id_mision = :misionID")
    Mision obtnerMisionPorID(int misionID);

    @Update
    void cerrarMision(Mision mision);
}
