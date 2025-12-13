package com.example.sistemadeyates.views;

import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.adapters.UsuarioAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ClientesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialToolbar toolbar;

    private void initializeViews(){
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAddUser);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBarUsers);
        emptyState = findViewById(R.id.emptyState);
        toolbar = findViewById(R.id.toolbar);
    }
}
