package com.example.levelupdaily;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

@Database(entities = {Usuario.class, AvatarUsuario.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract UsuarioDAO usuarioDao();
    public abstract AvatarDAO avatarDao();
    private static volatile AppDatabase INSTANCE;
    //Servicio de cuatro hilos para multiples tareas
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            //Solo para desarrollo, elimina la base con cada cambio
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
