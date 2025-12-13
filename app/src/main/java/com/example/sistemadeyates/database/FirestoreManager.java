package com.example.sistemadeyates.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Singleton Firestore manager for centralized database access
 */
public class FirestoreManager {
    private static FirestoreManager instance;
    private final FirebaseFirestore db;

    // Collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_ACTIVITY_LOGS = "activity_logs";
    public static final String COLLECTION_RESERVAS = "reservas";

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();

        // Configure Firestore settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)  // Disable offline persistence as per requirements
                .build();
        db.setFirestoreSettings(settings);
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * Get reference to users collection
     */
    public CollectionReference getUsersCollection() {
        return db.collection(COLLECTION_USERS);
    }

    /**
     * Get reference to activity_logs collection
     */
    public CollectionReference getActivityLogsCollection() {
        return db.collection(COLLECTION_ACTIVITY_LOGS);
    }

    /**
     * Get reference to reservas collection
     */
    public CollectionReference getReservasCollection() {
        return db.collection(COLLECTION_RESERVAS);
    }
}
