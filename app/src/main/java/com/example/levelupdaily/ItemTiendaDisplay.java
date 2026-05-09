package com.example.levelupdaily;

import androidx.room.Embedded;

/**
 * Representa un objeto en la tienda con su información base y stock actual.
 */
public class ItemTiendaDisplay {
    @Embedded
    public AvatarItem item;
    public int stock;
}
