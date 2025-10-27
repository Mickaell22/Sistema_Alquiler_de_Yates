package com.example.sistemadeyates.repositories;

import android.content.Context;

import com.example.sistemadeyates.database.AppDatabase;
import com.example.sistemadeyates.database.dao.UserDao;
import com.example.sistemadeyates.models.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.userDao = database.userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    public void getUserByUsername(String username, UserCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getByUsername(username);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getUserByEmail(String email, UserCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getByEmail(email);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void insertUser(User user, UserCallback callback) {
        executorService.execute(() -> {
            try {
                long id = userDao.insert(user);
                user.setId((int) id);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void updateUser(User user, UserCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.update(user);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void deleteUser(User user, UserCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.delete(user);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getAllUsers(UsersCallback callback) {
        executorService.execute(() -> {
            try {
                List<User> users = userDao.getAllUsers();
                callback.onSuccess(users);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getActiveUsers(UsersCallback callback) {
        executorService.execute(() -> {
            try {
                List<User> users = userDao.getActiveUsers();
                callback.onSuccess(users);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
}
