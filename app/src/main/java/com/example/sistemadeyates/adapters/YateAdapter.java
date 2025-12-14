package com.example.sistemadeyates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.models.Yate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class YateAdapter extends RecyclerView.Adapter<YateAdapter.YateViewHolder> {

    private List<Yate> yates;
    private List<Yate> yatesFiltrados;
    private OnYateClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnYateClickListener {
        void onYateClick(Yate yate);
        void onYateMenuClick(Yate yate, View view);
    }

    public YateAdapter(OnYateClickListener listener) {
        this.yates = new ArrayList<>();
        this.yatesFiltrados = new ArrayList<>();
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "DO"));
    }

    @NonNull
    @Override
    public YateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yate, parent, false);
        return new YateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YateViewHolder holder, int position) {
        Yate yate = yatesFiltrados.get(position);
        holder.bind(yate);
    }

    @Override
    public int getItemCount() {
        return yatesFiltrados.size();
    }

    public void setYates(List<Yate> yates) {
        this.yates = yates;
        this.yatesFiltrados = new ArrayList<>(yates);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            yatesFiltrados = new ArrayList<>(yates);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            yatesFiltrados = new ArrayList<>();
            for (Yate yate : yates) {
                if ((yate.getMarca() != null && yate.getMarca().toLowerCase().contains(lowerCaseQuery)) ||
                        (yate.getModelo() != null && yate.getModelo().toLowerCase().contains(lowerCaseQuery)) ||
                        (yate.getMatricula() != null && yate.getMatricula().toLowerCase().contains(lowerCaseQuery))) {
                    yatesFiltrados.add(yate);
                }
            }
        }
        notifyDataSetChanged();
    }

    class YateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMarcaModelo;
        private TextView tvMatricula;
        private TextView tvAnioTamanio;
        private TextView tvCapacidad;
        private TextView tvPrecioDia;
        private TextView tvDisponible;
        private ImageView btnMenuYate;
        private ImageView ivYateIcon;

        public YateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarcaModelo = itemView.findViewById(R.id.tvMarcaModelo);
            tvMatricula = itemView.findViewById(R.id.tvMatricula);
            tvAnioTamanio = itemView.findViewById(R.id.tvAnioTamanio);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidad);
            tvPrecioDia = itemView.findViewById(R.id.tvPrecioDia);
            tvDisponible = itemView.findViewById(R.id.tvDisponible);
            btnMenuYate = itemView.findViewById(R.id.btnMenuYate);
            ivYateIcon = itemView.findViewById(R.id.ivYateIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onYateClick(yatesFiltrados.get(position));
                }
            });

            btnMenuYate.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onYateMenuClick(yatesFiltrados.get(position), v);
                }
            });
        }

        public void bind(Yate yate) {
            String marca = yate.getMarca() != null ? yate.getMarca() : "";
            String modelo = yate.getModelo() != null ? yate.getModelo() : "";
            tvMarcaModelo.setText(marca + " " + modelo);

            tvMatricula.setText("Matrícula: " + (yate.getMatricula() != null ? yate.getMatricula() : "N/D"));

            tvAnioTamanio.setText("Año " + yate.getAnio() + " • " + (yate.getTamanio() != null ? yate.getTamanio() : ""));

            tvCapacidad.setText("Capacidad: " + yate.getCapacidad() + " pax");

            tvPrecioDia.setText(currencyFormat.format(yate.getPrecioDia()) + " / día");

            if (yate.isDisponible()) {
                tvDisponible.setText("Disponible");
                tvDisponible.setAlpha(1.0f);
            } else {
                tvDisponible.setText("No disponible");
                tvDisponible.setAlpha(0.5f);
            }
        }
    }
}
