package com.example.sistemadeyates.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class Yate {
    @DocumentId
    private String id;

    @PropertyName("marca")
    private String marca;

    @PropertyName("modelo")
    private String modelo;

    @PropertyName("anio")
    private int anio;

    @PropertyName("tamanio")
    private String tamanio;

    @PropertyName("capacidad")
    private int capacidad;

    @PropertyName("matricula")
    private String matricula;

    @PropertyName("precio_dia")
    private double precioDia;

    @PropertyName("disponible")
    private boolean disponible;

    @PropertyName("fecha_creacion")
    private long fechaCreacion;

    // No-argument constructor required for Firestore
    public Yate() {
        this.disponible = true;
        this.fechaCreacion = System.currentTimeMillis();
    }

    // Constructor for creating new yates
    public Yate(String marca, String modelo, int anio, String tamanio, int capacidad,
                String matricula, double precioDia) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.tamanio = tamanio;
        this.capacidad = capacidad;
        this.matricula = matricula;
        this.precioDia = precioDia;
        this.disponible = true;
        this.fechaCreacion = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public double getPrecioDia() {
        return precioDia;
    }

    public void setPrecioDia(double precioDia) {
        this.precioDia = precioDia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getDisplayName() {
        // Handle incomplete data from Firestore
        if (marca == null || marca.isEmpty()) {
            // If marca is null, just use modelo
            if (modelo != null && !modelo.isEmpty()) {
                if (anio > 0) {
                    return modelo + " (" + anio + ")";
                }
                return modelo;
            }
            return "Yate sin nombre";
        }

        // Normal case: marca + modelo + anio
        StringBuilder displayName = new StringBuilder(marca);
        if (modelo != null && !modelo.isEmpty()) {
            displayName.append(" ").append(modelo);
        }
        if (anio > 0) {
            displayName.append(" (").append(anio).append(")");
        }
        return displayName.toString();
    }
}
