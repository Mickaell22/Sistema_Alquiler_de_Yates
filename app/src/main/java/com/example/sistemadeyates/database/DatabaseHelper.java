package com.example.sistemadeyates.database;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.models.Cliente;
import com.example.sistemadeyates.models.Reserva;
import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.models.Yate;
import com.example.sistemadeyates.repositories.ClienteRepository;
import com.example.sistemadeyates.repositories.ReservaRepository;
import com.example.sistemadeyates.repositories.UserRepository;
import com.example.sistemadeyates.repositories.YateRepository;
import com.example.sistemadeyates.utils.EncryptionUtils;

import java.util.Calendar;

/**
 * Helper class for Firestore database initialization
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance;
    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final YateRepository yateRepository;
    private final ReservaRepository reservaRepository;

    private DatabaseHelper(Context context) {
        this.userRepository = new UserRepository(context);
        this.clienteRepository = new ClienteRepository(context);
        this.yateRepository = new YateRepository(context);
        this.reservaRepository = new ReservaRepository(context);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Initialize default users in Firestore
     * Only creates users if none exist
     */
    public void initializeDefaultUsers(Runnable onComplete) {
        // Check if users already exist
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(java.util.List<User> users) {
                if (users == null || users.isEmpty()) {
                    Log.d(TAG, "No users found. Initializing default users...");
                    createDefaultUsers(onComplete);
                } else {
                    Log.d(TAG, "Users already exist. Skipping initialization.");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking existing users: " + error);
                // Still try to create default users
                createDefaultUsers(onComplete);
            }
        });
    }

    private void createDefaultUsers(Runnable onComplete) {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@yates.com");
        admin.setPassword(EncryptionUtils.hashPassword("admin123"));
        admin.setRol("ADMIN");
        admin.setActivo(true);

        userRepository.insertUser(admin, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Admin user created successfully with ID: " + user.getId());

                // Create employee user
                User empleado = new User();
                empleado.setUsername("empleado1");
                empleado.setEmail("empleado1@yates.com");
                empleado.setPassword(EncryptionUtils.hashPassword("emp123"));
                empleado.setRol("EMPLEADO");
                empleado.setActivo(true);

                userRepository.insertUser(empleado, new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "Employee user created successfully with ID: " + user.getId());
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error creating employee user: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error creating admin user: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    /**
     * Initialize default clientes in Firestore
     * Only creates clientes if none exist
     */
    public void initializeDefaultClientes(Runnable onComplete) {
        // Check if clientes already exist
        clienteRepository.getAllClientes(new ClienteRepository.ClientesCallback() {
            @Override
            public void onSuccess(java.util.List<Cliente> clientes) {
                if (clientes == null || clientes.isEmpty()) {
                    Log.d(TAG, "No clientes found. Initializing default clientes...");
                    createDefaultClientes(onComplete);
                } else {
                    Log.d(TAG, "Clientes already exist. Skipping initialization.");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking existing clientes: " + error);
                // Still try to create default clientes
                createDefaultClientes(onComplete);
            }
        });
    }

    private void createDefaultClientes(Runnable onComplete) {
        // Create first cliente
        Cliente cliente1 = new Cliente(
                "juan.perez@email.com",
                "001-1234567-8",
                "809-555-1234",
                "Juan Carlos",
                "Perez Rodriguez",
                "LIC-2023-001"
        );

        clienteRepository.insertCliente(cliente1, new ClienteRepository.ClienteCallback() {
            @Override
            public void onSuccess(Cliente cliente) {
                Log.d(TAG, "Cliente 1 created successfully with ID: " + cliente.getId());

                // Create second cliente
                Cliente cliente2 = new Cliente(
                        "maria.santos@email.com",
                        "001-9876543-2",
                        "809-555-5678",
                        "Maria Elena",
                        "Santos Mejia",
                        "LIC-2023-002"
                );

                clienteRepository.insertCliente(cliente2, new ClienteRepository.ClienteCallback() {
                    @Override
                    public void onSuccess(Cliente cliente) {
                        Log.d(TAG, "Cliente 2 created successfully with ID: " + cliente.getId());

                        // Create third cliente
                        Cliente cliente3 = new Cliente(
                                "carlos.garcia@email.com",
                                "001-5555555-5",
                                "809-555-9999",
                                "Carlos Alberto",
                                "Garcia Lopez",
                                "LIC-2023-003"
                        );

                        clienteRepository.insertCliente(cliente3, new ClienteRepository.ClienteCallback() {
                            @Override
                            public void onSuccess(Cliente cliente) {
                                Log.d(TAG, "Cliente 3 created successfully with ID: " + cliente.getId());
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error creating cliente 3: " + error);
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error creating cliente 2: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error creating cliente 1: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    /**
     * Initialize default yates in Firestore
     * Only creates yates if none exist
     */
    public void initializeDefaultYates(Runnable onComplete) {
        // Check if yates already exist
        yateRepository.getAllYates(new YateRepository.YatesCallback() {
            @Override
            public void onSuccess(java.util.List<Yate> yates) {
                if (yates == null || yates.isEmpty()) {
                    Log.d(TAG, "No yates found. Initializing default yates...");
                    createDefaultYates(onComplete);
                } else {
                    Log.d(TAG, "Yates already exist. Skipping initialization.");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking existing yates: " + error);
                // Still try to create default yates
                createDefaultYates(onComplete);
            }
        });
    }

    private void createDefaultYates(Runnable onComplete) {
        // Create first yate - Azimut 55 pies
        Yate yate1 = new Yate("Azimut", "55S", 2021, "55 pies", 12, "YT-2021-001", 1500.00);

        yateRepository.insertYate(yate1, new YateRepository.YateCallback() {
            @Override
            public void onSuccess(Yate yate) {
                Log.d(TAG, "Yate 1 created successfully with ID: " + yate.getId());

                // Create second yate - Sunseeker 75 pies
                Yate yate2 = new Yate("Sunseeker", "75 Yacht", 2022, "75 pies", 15, "YT-2022-002", 2500.00);

                yateRepository.insertYate(yate2, new YateRepository.YateCallback() {
                    @Override
                    public void onSuccess(Yate yate) {
                        Log.d(TAG, "Yate 2 created successfully with ID: " + yate.getId());

                        // Create third yate - Princess 60 pies
                        Yate yate3 = new Yate("Princess", "V60", 2020, "60 pies", 10, "YT-2020-003", 1800.00);

                        yateRepository.insertYate(yate3, new YateRepository.YateCallback() {
                            @Override
                            public void onSuccess(Yate yate) {
                                Log.d(TAG, "Yate 3 created successfully with ID: " + yate.getId());
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error creating yate 3: " + error);
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error creating yate 2: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error creating yate 1: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    /**
     * Initialize default reservas in Firestore
     * Only creates reservas if none exist
     */
    public void initializeDefaultReservas(Runnable onComplete) {
        // Check if reservas already exist
        reservaRepository.getAllReservas(new ReservaRepository.ReservasCallback() {
            @Override
            public void onSuccess(java.util.List<Reserva> reservas) {
                if (reservas == null || reservas.isEmpty()) {
                    Log.d(TAG, "No reservas found. Initializing default reservas...");
                    createDefaultReservas(onComplete);
                } else {
                    Log.d(TAG, "Reservas already exist. Skipping initialization.");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking existing reservas: " + error);
                // Still try to create default reservas
                createDefaultReservas(onComplete);
            }
        });
    }

    private void createDefaultReservas(Runnable onComplete) {
        // Need to get cliente and yate IDs first
        clienteRepository.getAllClientes(new ClienteRepository.ClientesCallback() {
            @Override
            public void onSuccess(java.util.List<Cliente> clientes) {
                if (clientes == null || clientes.size() < 3) {
                    Log.e(TAG, "Not enough clientes to create reservas");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                    return;
                }

                yateRepository.getAllYates(new YateRepository.YatesCallback() {
                    @Override
                    public void onSuccess(java.util.List<Yate> yates) {
                        if (yates == null || yates.size() < 3) {
                            Log.e(TAG, "Not enough yates to create reservas");
                            if (onComplete != null) {
                                onComplete.run();
                            }
                            return;
                        }

                        // Get default admin user ID
                        userRepository.getUserByUsername("admin", new UserRepository.UserCallback() {
                            @Override
                            public void onSuccess(User user) {
                                if (user == null) {
                                    Log.e(TAG, "Admin user not found for reservas");
                                    if (onComplete != null) {
                                        onComplete.run();
                                    }
                                    return;
                                }

                                String adminId = user.getId();
                                createReservasWithIds(clientes, yates, adminId, onComplete);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error getting admin user: " + error);
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error getting yates for reservas: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting clientes for reservas: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    private void createReservasWithIds(java.util.List<Cliente> clientes, java.util.List<Yate> yates, String adminId, Runnable onComplete) {
        Calendar calendar = Calendar.getInstance();

        // Reserva 1: Cliente 1, Yate 1, próxima semana, 5 días, estado confirmada
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        long fechaInicio1 = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        long fechaFin1 = calendar.getTimeInMillis();
        double precio1 = 5 * yates.get(0).getPrecioDia(); // 5 días

        Reserva reserva1 = new Reserva(clientes.get(0).getId(), yates.get(0).getId(),
                fechaInicio1, fechaFin1, precio1, adminId);
        reserva1.setEstado(Reserva.ESTADO_CONFIRMADA);

        reservaRepository.insertReserva(reserva1, new ReservaRepository.ReservaCallback() {
            @Override
            public void onSuccess(Reserva reserva) {
                Log.d(TAG, "Reserva 1 created successfully with ID: " + reserva.getId());

                // Reserva 2: Cliente 2, Yate 2, en 15 días, 3 días, estado pendiente
                Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.DAY_OF_MONTH, 15);
                long fechaInicio2 = calendar2.getTimeInMillis();
                calendar2.add(Calendar.DAY_OF_MONTH, 3);
                long fechaFin2 = calendar2.getTimeInMillis();
                double precio2 = 3 * yates.get(1).getPrecioDia(); // 3 días

                Reserva reserva2 = new Reserva(clientes.get(1).getId(), yates.get(1).getId(),
                        fechaInicio2, fechaFin2, precio2, adminId);
                // Dejar como pendiente (estado por defecto)

                reservaRepository.insertReserva(reserva2, new ReservaRepository.ReservaCallback() {
                    @Override
                    public void onSuccess(Reserva reserva) {
                        Log.d(TAG, "Reserva 2 created successfully with ID: " + reserva.getId());

                        // Reserva 3: Cliente 3, Yate 3, en 30 días, 7 días, estado confirmada
                        Calendar calendar3 = Calendar.getInstance();
                        calendar3.add(Calendar.DAY_OF_MONTH, 30);
                        long fechaInicio3 = calendar3.getTimeInMillis();
                        calendar3.add(Calendar.DAY_OF_MONTH, 7);
                        long fechaFin3 = calendar3.getTimeInMillis();
                        double precio3 = 7 * yates.get(2).getPrecioDia(); // 7 días

                        Reserva reserva3 = new Reserva(clientes.get(2).getId(), yates.get(2).getId(),
                                fechaInicio3, fechaFin3, precio3, adminId);
                        reserva3.setEstado(Reserva.ESTADO_CONFIRMADA);

                        reservaRepository.insertReserva(reserva3, new ReservaRepository.ReservaCallback() {
                            @Override
                            public void onSuccess(Reserva reserva) {
                                Log.d(TAG, "Reserva 3 created successfully with ID: " + reserva.getId());
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error creating reserva 3: " + error);
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error creating reserva 2: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error creating reserva 1: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    /**
     * No shutdown needed for Firestore (unlike Room)
     */
    public void shutdown() {
        Log.d(TAG, "DatabaseHelper shutdown (no-op for Firestore)");
    }
}
