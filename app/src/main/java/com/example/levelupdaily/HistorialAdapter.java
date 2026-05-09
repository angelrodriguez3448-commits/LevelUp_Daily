package com.example.levelupdaily;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<Mision> misiones;

    public HistorialAdapter(List<Mision> misiones) { this.misiones = misiones; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mision m = misiones.get(position);
        holder.text1.setText("✅ " + m.getTitulo());
        holder.text1.setTextColor(Color.parseColor("#6BCB77")); // Verde éxito
        holder.text2.setText("Tipo: " + m.getTipo() + " | Completada");
        holder.text2.setTextColor(Color.LTGRAY);
    }

    @Override
    public int getItemCount() { return misiones.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        ViewHolder(View v) {
            super(v);
            text1 = v.findViewById(android.R.id.text1);
            text2 = v.findViewById(android.R.id.text2);
        }
    }
}
