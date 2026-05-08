package com.example.levelupdaily;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


@Database(entities = {
        Usuario.class,
        AvatarUsuario.class,
        Mision.class,
        SubMision.class,
        AvatarItem.class,
        AvatarInventario.class,
        TiendaItem.class // Added this
}, version = 3) // Incremented version
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{
    public abstract UsuarioDAO usuarioDao();
    public abstract AvatarDAO avatarDao();
    public abstract MisionDAO misionDAO();
    public abstract SubMisionDAO subMisionDAO();
    public abstract InventarioDao inventarioDao();
    public abstract TiendaDao tiendaDao(); // Added this
    
    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
