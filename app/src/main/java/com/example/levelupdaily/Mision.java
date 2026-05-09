package com.example.levelupdaily;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "misiones",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id_usuario",
                childColumns = "id_usuario",
                onDelete = ForeignKey.CASCADE //Si se borra el usuario se borra tambien las misiones
        )
)

public class Mision {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public int id_usuario;

    private String titulo;
    private String tipo;
    private Date fechaLimite;
    private boolean completada;

    public Mision(
            int id_usuario,
            String titulo,
            String tipo,
            Date fechaLimite,
            boolean completada
    ) {

        this.id_usuario = id_usuario;
        this.titulo = titulo;
        this.tipo = tipo;
        this.fechaLimite = fechaLimite;
        this.completada = completada;
    }

    public int getId() {
        return id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public boolean isCompletada() {
        return completada;
    }

    @Override
    public String toString() {

        if(completada) {
            return "? " + titulo;
        }

        return titulo + " - " + tipo;
    }
}