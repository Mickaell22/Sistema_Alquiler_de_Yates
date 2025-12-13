package com.example.sistemadeyates.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class Cliente {
    @DocumentId
    private String id;

    @PropertyName("correo")
    private String correo;

    @PropertyName("cedula")
    private String cedula;

    @PropertyName("telefono")
    private String telefono;

    @PropertyName("nombres")
    private String nombres;

    @PropertyName("apellidos")
    private String apellidos;

    @PropertyName("numero_licencia")
    private String numeroLicencia;

    @PropertyName("estado")
    private boolean estado;

    @PropertyName("fecha_creacion")
    private long fechaCreacion;

    @PropertyName("fecha_modificacion")
    private long fechaModificacion;

    // No-argument constructor required for Firestore
    public Cliente() {
        this.estado = true;
        this.fechaCreacion = System.currentTimeMillis();
        this.fechaModificacion = System.currentTimeMillis();
    }

    // Constructor for creating new clientes
    public Cliente(String correo, String cedula, String telefono, String nombres,
                   String apellidos, String numeroLicencia) {
        this.correo = correo;
        this.cedula = cedula;
        this.telefono = telefono;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numeroLicencia = numeroLicencia;
        this.estado = true;
        this.fechaCreacion = System.currentTimeMillis();
        this.fechaModificacion = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(long fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
