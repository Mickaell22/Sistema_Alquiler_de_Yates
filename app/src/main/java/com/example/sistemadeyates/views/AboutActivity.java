package com.example.sistemadeyates.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sistemadeyates.R;
import com.google.android.material.button.MaterialButton;

public class AboutActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private ImageView imgTeamPhoto;
    private TextView tvAboutDescription;
    private TextView tvMember1;
    private TextView tvMember2;
    private TextView tvMember3;
    private TextView tvCourseInfo;
    private MaterialButton btnClose;

    private SharedPreferences themePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme preference
        themePrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        applyTheme();

        setContentView(R.layout.activity_about);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        imgTeamPhoto = findViewById(R.id.imgTeamPhoto);
        tvAboutDescription = findViewById(R.id.tvAboutDescription);
        tvMember1 = findViewById(R.id.tvMember1);
        tvMember2 = findViewById(R.id.tvMember2);
        tvMember3 = findViewById(R.id.tvMember3);
        tvCourseInfo = findViewById(R.id.tvCourseInfo);
        btnClose = findViewById(R.id.btnClose);

        // Set the content
        tvAboutDescription.setText(R.string.about_description);
        tvMember1.setText("• " + getString(R.string.member_1));
        tvMember2.setText("• " + getString(R.string.member_2));
        tvMember3.setText("• " + getString(R.string.member_3));
        tvCourseInfo.setText(R.string.course_info);
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    private void applyTheme() {
        boolean isDarkMode = themePrefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
