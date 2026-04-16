package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AvatarDAO {
    @Insert
    void registrarAvatar(AvatarUsuario avatar);

    @Query("SELECT * FROM avatar_usuario WHERE id_usuario = :userID")
    AvatarUsuario obtenerAvatarPorUsuario(int userID);

    @Update
    void actualizarProgreso(AvatarUsuario avatar);
}
