package com.example.sistemadeyates.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class Reserva {
    @DocumentId
    private String id;

    @PropertyName("cliente_id")
    private String clienteId;

    @PropertyName("yate_id")
    private String yateId;

    @PropertyName("fecha_inicio")
    private long fechaInicio;

    @PropertyName("fecha_fin")
    private long fechaFin;

    @PropertyName("estado")
    private String estado;

    @PropertyName("precio_total")
    private double precioTotal;

    // Audit fields
    @PropertyName("fecha_creacion")
    private long fechaCreacion;

    @PropertyName("fecha_modificacion")
    private long fechaModificacion;

    @PropertyName("modificado_por")
    private String modificadoPor;

    @PropertyName("cancelado_por")
    private String canceladoPor;

    // Estado constants
    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_CONFIRMADA = "confirmada";
    public static final String ESTADO_CANCELADA = "cancelada";

    // No-argument constructor required for Firestore
    public Reserva() {
        this.fechaCreacion = System.currentTimeMillis();
        this.fechaModificacion = System.currentTimeMillis();
        this.estado = ESTADO_PENDIENTE;
    }

    // Constructor for creating new reservas
    public Reserva(String clienteId, String yateId, long fechaInicio, long fechaFin,
                   double precioTotal, String modificadoPor) {
        this.clienteId = clienteId;
        this.yateId = yateId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = ESTADO_PENDIENTE;
        this.fechaCreacion = System.currentTimeMillis();
        this.fechaModificacion = System.currentTimeMillis();
        this.modificadoPor = modificadoPor;
        this.canceladoPor = null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getYateId() {
        return yateId;
    }

    public void setYateId(String yateId) {
        this.yateId = yateId;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public long getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(long fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
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

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public String getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(String canceladoPor) {
        this.canceladoPor = canceladoPor;
    }
}
