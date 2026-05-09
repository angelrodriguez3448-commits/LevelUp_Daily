package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "avatar_usuario",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id_usuario",
                childColumns = "id_usuario",
                onDelete = ForeignKey.CASCADE
        )
)
public class AvatarUsuario {
    @PrimaryKey(autoGenerate = true)
    public int id_avatar;

    public int id_usuario;

    public String avatar_name;

    public String imagen;

    public int hp;
    public int oro;

    public int xp;

    public int nivel;

    public boolean tiene_escudo; // Nuevo campo para el escudo
}
