package com.example.magazynapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnProducts = findViewById(R.id.btnProducts);
        Button btnModify = findViewById(R.id.btnModify);
        Button btnScanner = findViewById(R.id.btnScanner);

        Button btnStatistics = findViewById(R.id.btnStatistics);
        // Odczytanie roli użytkownika z SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = preferences.getString("role", "Warehouse Worker"); // Domyslna wartosc "Warehouse Worker"



        btnProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
            startActivity(intent);
        });

        btnModify.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
            startActivity(intent);
        });

        btnScanner.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(intent);
        });


        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }
}