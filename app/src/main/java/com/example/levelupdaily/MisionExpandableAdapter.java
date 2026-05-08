package com.example.levelupdaily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MisionExpandableAdapter
        extends BaseExpandableListAdapter {

    private Context context;

    private List<Mision> listaMisiones;

    private HashMap<Integer,
            List<SubMision>> mapaSubtareas;

    public MisionExpandableAdapter(
            Context context,
            List<Mision> listaMisiones,
            HashMap<Integer, List<SubMision>> mapaSubtareas) {

        this.context = context;
        this.listaMisiones = listaMisiones;
        this.mapaSubtareas = mapaSubtareas;
    }

    @Override
    public int getGroupCount() {
        return listaMisiones.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        int idMision =
                listaMisiones.get(groupPosition).getId();

        return mapaSubtareas.get(idMision).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listaMisiones.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition,
                           int childPosition) {

        int idMision =
                listaMisiones.get(groupPosition).getId();

        return mapaSubtareas.get(idMision)
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition,
                           int childPosition) {

        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {

        Mision mision =
                (Mision) getGroup(groupPosition);

        if(convertView == null) {

            convertView =
                    LayoutInflater.from(context)
                            .inflate(
                                    android.R.layout.simple_expandable_list_item_1,
                                    parent,
                                    false
                            );
        }

        TextView textView =
                convertView.findViewById(android.R.id.text1);

        textView.setText(
                mision.getTitulo()
                        + "  "
                        + mision.getFechaLimite()
        );

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {

        SubMision subMision =
                (SubMision) getChild(groupPosition,
                        childPosition);

        if(convertView == null) {

            convertView =
                    LayoutInflater.from(context)
                            .inflate(
                                    android.R.layout.simple_list_item_checked,
                                    parent,
                                    false
                            );
        }

        CheckedTextView checked =
                convertView.findViewById(android.R.id.text1);

        checked.setText(subMision.descripcion);

        checked.setChecked(subMision.completada);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,
                                     int childPosition) {

        return true;
    }
}