package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubMisionDAO {
    @Insert
    void guardarSubMision(SubMision subMision);

    @Query("SELECT * FROM submisiones WHERE id_mision = :misionID")
    SubMision obtenerSubMisionesPorMision(int misionID);

    @Query("SELECT * FROM submisiones WHERE id_submision = :submisionID")
    SubMision obtenerSubMisionPorID(int submisionID);

    @Update
    void actualizarEstado(SubMision subMision);
}
