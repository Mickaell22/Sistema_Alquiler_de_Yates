package com.example.sistemadeyates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<User> usuarios;
    private List<User> usuariosFiltrados;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onUsuarioClick(User usuario);
        void onUsuarioMenuClick(User usuario, View view);
    }

    public UsuarioAdapter(OnUsuarioClickListener listener) {
        this.usuarios = new ArrayList<>();
        this.usuariosFiltrados = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        User usuario = usuariosFiltrados.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuariosFiltrados.size();
    }

    public void setUsuarios(List<User> usuarios) {
        this.usuarios = usuarios;
        this.usuariosFiltrados = new ArrayList<>(usuarios);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            usuariosFiltrados = new ArrayList<>(usuarios);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            usuariosFiltrados = new ArrayList<>();
            for (User usuario : usuarios) {
                if (usuario.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                        usuario.getEmail().toLowerCase().contains(lowerCaseQuery) ||
                        usuario.getRol().toLowerCase().contains(lowerCaseQuery)) {
                    usuariosFiltrados.add(usuario);
                }
            }
        }
        notifyDataSetChanged();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvEmail;
        private TextView tvRol;
        private TextView tvEstado;
        private ImageView btnMenu;
        private ImageView ivUserIcon;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRol = itemView.findViewById(R.id.tvRol);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnMenu = itemView.findViewById(R.id.btnMenu);
            ivUserIcon = itemView.findViewById(R.id.ivUserIcon);
        }

        public void bind(User usuario) {
            tvUsername.setText(usuario.getUsername());
            tvEmail.setText(usuario.getEmail());
            tvRol.setText(usuario.getRol());

            // Mostrar estado
            if (usuario.isActivo()) {
                tvEstado.setText("Activo");
                tvEstado.setAlpha(1.0f);
            } else {
                tvEstado.setText("Inactivo");
                tvEstado.setAlpha(0.5f);
            }

            // Click en el item completo
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUsuarioClick(usuario);
                }
            });

            // Click en el menú (3 puntos)
            btnMenu.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUsuarioMenuClick(usuario, v);
                }
            });
        }
    }
}
