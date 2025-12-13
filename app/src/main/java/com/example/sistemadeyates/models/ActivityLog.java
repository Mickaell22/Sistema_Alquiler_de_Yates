package com.example.sistemadeyates.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class ActivityLog {
    @DocumentId
    private String id;

    @PropertyName("user_id")
    private String userId;

    @PropertyName("accion")
    private String accion;

    @PropertyName("timestamp")
    private long timestamp;

    @PropertyName("detalles")
    private String detalles;

    // No-argument constructor required for Firestore
    public ActivityLog() {
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor for creating new logs
    public ActivityLog(String userId, String accion, String detalles) {
        this.userId = userId;
        this.accion = accion;
        this.detalles = detalles;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
