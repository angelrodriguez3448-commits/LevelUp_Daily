package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UsuarioDAO {
    @Insert
    long registrarUsuario(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE nombre_usuario = :nombre AND password = :pass")
    Usuario login(String nombre, String pass);
}
