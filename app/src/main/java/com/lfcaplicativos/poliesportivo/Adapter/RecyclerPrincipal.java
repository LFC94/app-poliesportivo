package com.lfcaplicativos.poliesportivo.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lfcaplicativos.poliesportivo.Objetos.Ginasios;
import com.lfcaplicativos.poliesportivo.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 09/12/2017.
 */

public class RecyclerPrincipal extends RecyclerView.Adapter<RecyclerPrincipal.DataObjectHolder> {
    private ArrayList<Ginasios> mDataset;
    private static MyClickListener myClickListener;
    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView text_CardPrincipal_Titulo, text_CardPrincipal_Endereco, text_CardPrincipal_Modalidade;
        ImageView image_CardPrincipal_Logo;


        public DataObjectHolder(View itemView) {
            super(itemView);
            text_CardPrincipal_Titulo = (TextView) itemView.findViewById(R.id.text_CardPrincipal_Titulo);
            text_CardPrincipal_Endereco = (TextView) itemView.findViewById(R.id.text_CardPrincipal_Endereco);
            text_CardPrincipal_Modalidade = (TextView) itemView.findViewById(R.id.text_CardPrincipal_Modalidade);
            image_CardPrincipal_Logo = (ImageView) itemView.findViewById(R.id.image_CardPrincipal_Logo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public RecyclerPrincipal(ArrayList<Ginasios> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cardprincipal, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        String endereco;
        endereco = mDataset.get(position).getEndereco() + ", " + mDataset.get(position).getNumero() + ", " +
                mDataset.get(position).getBairro() + " - " + mDataset.get(position).getCidade() + " - " +
                mDataset.get(position).getEstado();

        holder.text_CardPrincipal_Titulo.setText(mDataset.get(position).getNome() + " - " + mDataset.get(position).getFantasia());
        holder.text_CardPrincipal_Endereco.setText(endereco);
        holder.text_CardPrincipal_Modalidade.setText(mDataset.get(position).getModalidade());
        holder.image_CardPrincipal_Logo.setImageBitmap(mDataset.get(position).getLogo());

    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void addItem(Ginasios dataObj, int index) {
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
