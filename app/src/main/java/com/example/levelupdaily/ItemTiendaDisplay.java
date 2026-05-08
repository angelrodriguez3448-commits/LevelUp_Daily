package com.example.levelupdaily;

import androidx.room.Embedded;

public class ItemTiendaDisplay {
    @Embedded
    public AvatarItem item;
    public int stock;
}
