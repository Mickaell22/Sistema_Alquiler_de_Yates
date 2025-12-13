package com.example.sistemadeyates.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class User {
    @DocumentId
    private String id;

    @PropertyName("username")
    private String username;

    @PropertyName("email")
    private String email;

    @PropertyName("password")
    private String password;

    @PropertyName("rol")
    private String rol;

    @PropertyName("activo")
    private boolean activo;

    @PropertyName("fecha_creacion")
    private long fechaCreacion;

    // No-argument constructor required for Firestore
    public User() {
        this.activo = true;
        this.fechaCreacion = System.currentTimeMillis();
    }

    // Constructor for creating new users
    public User(String username, String email, String password, String rol) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
