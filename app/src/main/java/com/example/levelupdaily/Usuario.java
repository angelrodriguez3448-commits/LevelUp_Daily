package com.example.levelupdaily;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios", indices = {@Index(value = {"nombre_usuario"}, unique = true)})
public class Usuario{
    @PrimaryKey(autoGenerate = true)
    public int id_usuario;

    @ColumnInfo(name = "nombre_usuario")
    public String nombreUsuario;

    @ColumnInfo(name = "password")
    public String password;
}
