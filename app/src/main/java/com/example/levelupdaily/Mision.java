package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "misiones",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id_usuario",
                childColumns = "id_usuario",
                onDelete = ForeignKey.CASCADE //Si se borra el usuario se borra tambien las misiones
        )
)

public class Mision {
    @PrimaryKey(autoGenerate = true)
    public int id_mision;

    public int id_usuario; //FK

    public String titulo;

    public String descripcion;

    public Boolean estado; //False = sin cerrar y True = cerrada

    public Date fecha_limite;
}
