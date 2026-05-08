package com.example.levelupdaily;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface InventarioDao {

    // Esta consulta une la tabla 'items' con 'inventario'
    // Asegúrate de que los nombres en 'INNER JOIN' sean iguales a tus @Entity(tableName = "...")
    @Query("SELECT items.*, inventario.cantidad FROM items " +
            "INNER JOIN inventario ON items.id_item = inventario.id_item " +
            "WHERE inventario.id_avatar = :avatarId")
    List<ItemActividad> getInventarioPorAvatar(int avatarId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void agregarAlInventario(AvatarInventario inventario);

    // Método para cuando el usuario usa un escudo o un salto de misión
    @Query("UPDATE inventario SET cantidad = cantidad - 1 " +
            "WHERE id_avatar = :avatarId AND id_item = :itemId AND cantidad > 0")
    void usarItem(int avatarId, int itemId);
}
