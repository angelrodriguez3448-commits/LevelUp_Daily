package com.example.levelupdaily;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.ViewHolder> {

    // Esta es la lista de objetos que traemos de la base de datos
    private List<ItemActividad> listaItems;

    public InventarioAdapter(List<ItemActividad> listaItems) {
        this.listaItems = listaItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CUIDADO: Aquí debe decir R.layout.item_inventario (el nombre del XML de la fila)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemActividad itemActual = listaItems.get(position);

        // Seteamos los textos usando el objeto "item" dentro de ItemConCantidad
        holder.nombre.setText(itemActual.item.nombre);
        holder.cantidad.setText("x" + itemActual.cantidad);
    }

    @Override
    public int getItemCount() {
        return (listaItems != null) ? listaItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, cantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Estos IDs deben coincidir con los que pusiste en item_inventario.xml
            nombre = itemView.findViewById(R.id.nombreItem);
            cantidad = itemView.findViewById(R.id.cantidadItem);
        }
    }
}