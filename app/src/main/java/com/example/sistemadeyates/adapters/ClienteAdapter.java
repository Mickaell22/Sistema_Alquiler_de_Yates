package com.example.sistemadeyates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sistemadeyates.R;
import com.example.sistemadeyates.models.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> clientes;
    private List<Cliente> clientesFiltrados;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente); // Click en el ítem completo
        void onClienteMenuClick(Cliente cliente, View view); // Click en los 3 puntos
    }

    public ClienteAdapter(OnClienteClickListener listener) {
        this.clientes = new ArrayList<>();
        this.clientesFiltrados = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = clientesFiltrados.get(position);
        holder.bind(cliente);
    }

    @Override
    public int getItemCount() {
        return clientesFiltrados.size();
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
        this.clientesFiltrados = new ArrayList<>(clientes);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            clientesFiltrados = new ArrayList<>(clientes);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            clientesFiltrados = new ArrayList<>();
            for (Cliente cliente : clientes) {
                if ((cliente.getNombres() != null && cliente.getNombres().toLowerCase().contains(lowerCaseQuery)) ||
                        (cliente.getApellidos() != null && cliente.getApellidos().toLowerCase().contains(lowerCaseQuery)) ||
                        (cliente.getCedula() != null && cliente.getCedula().toLowerCase().contains(lowerCaseQuery)) ||
                        (cliente.getCorreo() != null && cliente.getCorreo().toLowerCase().contains(lowerCaseQuery)) ||
                        (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(lowerCaseQuery))) {
                    clientesFiltrados.add(cliente);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ClienteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClienteNombres;
        private TextView tvClienteApellidos;
        private TextView tvClienteCedula;
        private TextView tvClienteEmail;
        private TextView tvTipoCliente;
        private TextView tvClienteEstado;
        private ImageView btnMenuCliente;
        private ImageView ivClienteIcon;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClienteNombres = itemView.findViewById(R.id.tvClienteNombres);
            tvClienteApellidos = itemView.findViewById(R.id.tvClienteApellidos);
            tvClienteCedula = itemView.findViewById(R.id.tvClienteCedula);
            tvClienteEmail = itemView.findViewById(R.id.tvClienteEmail);
            tvTipoCliente = itemView.findViewById(R.id.tvTipoCliente);
            tvClienteEstado = itemView.findViewById(R.id.tvClienteEstado);
            btnMenuCliente = itemView.findViewById(R.id.btnMenuCliente);
            ivClienteIcon = itemView.findViewById(R.id.ivClienteIcon);

            // Click en el ítem completo - para EDITAR (igual que UsuarioAdapter)
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClienteClick(clientesFiltrados.get(position));
                }
            });

            // Click en el menú (3 puntos) - para otras opciones
            btnMenuCliente.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClienteMenuClick(clientesFiltrados.get(position), v);
                }
            });
        }

        public void bind(Cliente cliente) {
            tvClienteNombres.setText(cliente.getNombres());
            tvClienteApellidos.setText(cliente.getApellidos());
            tvClienteCedula.setText("Cédula: " + cliente.getCedula());
            tvClienteEmail.setText(cliente.getCorreo());

            if (cliente.getNumeroLicencia() != null && !cliente.getNumeroLicencia().isEmpty()) {
                tvTipoCliente.setText("NAUTICO");
            } else {
                tvTipoCliente.setText("REGULAR");
            }

            if (cliente.isEstado()) {
                tvClienteEstado.setText("Activo");
            } else {
                tvClienteEstado.setText("Inactivo");
            }
        }
    }
}