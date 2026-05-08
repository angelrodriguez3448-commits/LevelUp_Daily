package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tienda_stock")
public class TiendaItem {
    @PrimaryKey
    public int id_item;
    public int stock;

    public TiendaItem(int id_item, int stock) {
        this.id_item = id_item;
        this.stock = stock;
    }
}
