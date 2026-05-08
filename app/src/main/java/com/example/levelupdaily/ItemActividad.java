package com.example.levelupdaily;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
public class ItemActividad {
    @Embedded
    public AvatarItem item;
    public int cantidad;
}
