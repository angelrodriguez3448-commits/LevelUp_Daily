package com.example.levelupdaily;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "inventario",
        primaryKeys = {"id_avatar", "id_item"},
        indices = {@Index("id_item")},
        foreignKeys = {
                @ForeignKey(
                        entity = AvatarUsuario.class,
                        parentColumns = "id_avatar",
                        childColumns = "id_avatar",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = AvatarItem.class,
                        parentColumns = "id_item",
                        childColumns = "id_item",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class AvatarInventario {
    public int id_avatar;
    public int id_item;

    public int cantidad;

    // Constructor para facilidad de uso
    public AvatarInventario(int id_avatar, int id_item, int cantidad) {
        this.id_avatar = id_avatar;
        this.id_item = id_item;
        this.cantidad = cantidad;
    }
}
