package com.lfcaplicativos.poliesportivo.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lfcaplicativos.poliesportivo.Objetos.Horarios;
import com.lfcaplicativos.poliesportivo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Lucas on 09/12/2017.
 */

public class RecyclerGinasio extends RecyclerView.Adapter<RecyclerGinasio.DataObjectHolder> {
    private ArrayList<Horarios> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView textGinasioInfo, textGinasioStatus;
        LinearLayout linearLayoutGinasioHorario;


        public DataObjectHolder(View itemView) {
            super(itemView);
            textGinasioInfo = (TextView) itemView.findViewById(R.id.textGinasioInfo);
            textGinasioStatus = (TextView) itemView.findViewById(R.id.textGinasioStatus);
            linearLayoutGinasioHorario = (LinearLayout) itemView.findViewById(R.id.linearLayoutGinasioHorario);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public RecyclerGinasio(ArrayList<Horarios> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cardprincipal, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        holder.textGinasioInfo.setText(sdfDate.format(mDataset.get(position).getHoraInicial()) + " - " + sdfDate.format(mDataset.get(position).getHoraFinal()));
        holder.textGinasioStatus.setText(mDataset.get(position).getTextoStatus());

        holder.linearLayoutGinasioHorario.setBackgroundResource(R.color.available);


    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void addItem(Horarios dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
