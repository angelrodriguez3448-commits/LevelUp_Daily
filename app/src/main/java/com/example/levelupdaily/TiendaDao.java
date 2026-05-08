package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TiendaDao {
    @Query("SELECT items.*, tienda_stock.stock FROM items " +
            "INNER JOIN tienda_stock ON items.id_item = tienda_stock.id_item")
    List<ItemTiendaDisplay> getStockTienda();

    @Query("SELECT * FROM items")
    List<AvatarItem> getTodosLosItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void actualizarStock(List<TiendaItem> items);

    @Update
    void updateTiendaItem(TiendaItem item);

    @Query("SELECT * FROM tienda_stock WHERE id_item = :itemId")
    TiendaItem getTiendaItem(int itemId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertarItemsBase(List<AvatarItem> items);
}
