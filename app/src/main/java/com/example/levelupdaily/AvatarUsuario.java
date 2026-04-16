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
                onDelete = ForeignKey.CASCADE //Si se borra el usuario se borra tambien el avatar
        )
)
public class AvatarUsuario {
    @PrimaryKey(autoGenerate = true)
    public int id_avatar;

    public int id_usuario; //Esta es la FK

    public String avatar_name;

    public String imagen;

    public int oro;

    public int xp;

    public int nivel;
}
