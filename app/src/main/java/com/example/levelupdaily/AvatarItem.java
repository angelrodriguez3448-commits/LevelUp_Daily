package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "items", indices = {@Index(value = {"nombre"}, unique = true)})
public class AvatarItem {
    @PrimaryKey(autoGenerate = true)
    public int id_item;
    public String nombre;
    public String descripcion;
    public int precio;
}
