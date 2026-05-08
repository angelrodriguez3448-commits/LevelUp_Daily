package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "submisiones",
        foreignKeys = @ForeignKey(
                entity = Mision.class,
                parentColumns = "id",
                childColumns = "id_mision",
                onDelete = ForeignKey.CASCADE
        )
)

public class SubMision {
    @PrimaryKey(autoGenerate = true)
    public int id_submisiones;

    public int id_mision;

    public String descripcion;

    public boolean completada;

    public SubMision(int id_mision,
                     String descripcion,
                     boolean completada){
        this.id_mision = id_mision;
        this.descripcion = descripcion;
        this.completada = completada;
    }

    @Override
    public String toString() {

        if(completada) {
            return "✔ " + descripcion;
        }

        return descripcion;
    }
}
