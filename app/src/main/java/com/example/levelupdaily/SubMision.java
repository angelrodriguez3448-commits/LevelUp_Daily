package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "submisiones",
        foreignKeys = @ForeignKey(
                entity = Mision.class,
                parentColumns = "id_mision",
                childColumns = "id_mision",
                onDelete = ForeignKey.CASCADE
        )
)

public class SubMision {
    @PrimaryKey(autoGenerate = true)
    public int id_submision;

    public int id_mision; //FK

    public String descripcion;

    public Boolean estado_completada; //False = sin completar y True = completada
}
