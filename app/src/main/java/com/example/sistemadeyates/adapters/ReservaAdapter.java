package com.example.sistemadeyates.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.models.Reserva;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;
    private List<Reserva> reservasFiltradas;
    private OnReservaClickListener listener;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    public interface OnReservaClickListener {
        void onReservaClick(Reserva reserva);
        void onReservaMenuClick(Reserva reserva, View view);
        String getClienteNombre(String clienteId);
        String getYateNombre(String yateId);
    }

    public ReservaAdapter(OnReservaClickListener listener) {
        this.reservas = new ArrayList<>();
        this.reservasFiltradas = new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "DO"));
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservasFiltradas.get(position);
        holder.bind(reserva);
    }

    @Override
    public int getItemCount() {
        return reservasFiltradas.size();
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        this.reservasFiltradas = new ArrayList<>(reservas);
        notifyDataSetChanged();
    }

    public void filterByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty() || estado.equals("Todos")) {
            reservasFiltradas = new ArrayList<>(reservas);
        } else {
            reservasFiltradas = new ArrayList<>();
            for (Reserva reserva : reservas) {
                if (reserva.getEstado().equalsIgnoreCase(estado)) {
                    reservasFiltradas.add(reserva);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ReservaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReservaId;
        private TextView tvClienteNombre;
        private TextView tvYateNombre;
        private TextView tvEstado;
        private TextView tvFechas;
        private TextView tvPrecioTotal;
        private ImageView btnMenu;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReservaId = itemView.findViewById(R.id.tvReservaId);
            tvClienteNombre = itemView.findViewById(R.id.tvClienteNombre);
            tvYateNombre = itemView.findViewById(R.id.tvYateNombre);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvFechas = itemView.findViewById(R.id.tvFechas);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }

        public void bind(Reserva reserva) {
            // ID de reserva (últimos 8 caracteres)
            String shortId = reserva.getId() != null && reserva.getId().length() > 8
                    ? reserva.getId().substring(reserva.getId().length() - 8)
                    : reserva.getId();
            tvReservaId.setText("Reserva #" + shortId);

            // Cliente y Yate (usar callbacks para obtener nombres)
            String clienteNombre = listener.getClienteNombre(reserva.getClienteId());
            String yateNombre = listener.getYateNombre(reserva.getYateId());

            tvClienteNombre.setText(clienteNombre != null ? clienteNombre : "Cliente desconocido");
            tvYateNombre.setText(yateNombre != null ? yateNombre : "Yate desconocido");

            // Estado con colores
            tvEstado.setText(getEstadoText(reserva.getEstado()));
            tvEstado.setBackgroundColor(getEstadoColor(reserva.getEstado()));

            // Fechas
            String fechaInicio = dateFormat.format(new Date(reserva.getFechaInicio()));
            String fechaFin = dateFormat.format(new Date(reserva.getFechaFin()));
            tvFechas.setText(fechaInicio + " - " + fechaFin);

            // Precio
            tvPrecioTotal.setText(currencyFormat.format(reserva.getPrecioTotal()));

            // Click en el item completo
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReservaClick(reserva);
                }
            });

            // Click en el menú (3 puntos)
            btnMenu.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReservaMenuClick(reserva, v);
                }
            });
        }

        private String getEstadoText(String estado) {
            switch (estado.toLowerCase()) {
                case "pendiente":
                    return "Pendiente";
                case "confirmada":
                    return "Confirmada";
                case "cancelada":
                    return "Cancelada";
                default:
                    return estado;
            }
        }

        private int getEstadoColor(String estado) {
            switch (estado.toLowerCase()) {
                case "pendiente":
                    return Color.parseColor("#FFA500"); // Orange
                case "confirmada":
                    return Color.parseColor("#4CAF50"); // Green
                case "cancelada":
                    return Color.parseColor("#F44336"); // Red
                default:
                    return Color.parseColor("#9E9E9E"); // Gray
            }
        }
    }
}
