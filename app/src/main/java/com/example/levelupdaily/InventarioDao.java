package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface InventarioDao {

    // Se ha añadido "AND inventario.cantidad > 0" para que no aparezcan ítems agotados
    @Query("SELECT items.*, inventario.cantidad FROM items " +
            "INNER JOIN inventario ON items.id_item = inventario.id_item " +
            "WHERE inventario.id_avatar = :avatarId AND inventario.cantidad > 0")
    List<ItemActividad> getInventarioPorAvatar(int avatarId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void agregarAlInventario(AvatarInventario inventario);

    @Query("UPDATE inventario SET cantidad = cantidad - 1 " +
            "WHERE id_avatar = :avatarId AND id_item = :itemId AND cantidad > 0")
    void usarItem(int avatarId, int itemId);
}
