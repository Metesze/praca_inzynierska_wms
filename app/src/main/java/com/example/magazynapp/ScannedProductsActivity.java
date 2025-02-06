package com.example.magazynapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ScannedProductsActivity extends AppCompatActivity {

    private ListView listViewScanned;
    private Button btnBackToMenu, btnClearList;
    private ScannedProductAdapter adapter; // Adapter do listy
    private ArrayList<Product> scannedProductList;

    private static final String SCANNED_LIST_KEY = "scannedList"; // Klucz dla przechowywania stanu listy


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_products);

        // Inicjalizacja widoków
        listViewScanned = findViewById(R.id.lvScannedProducts);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnClearList = findViewById(R.id.btnClearList);
        Button btnBackToScanner = findViewById(R.id.btnBackToScanner); // Nowy przycisk

        // Sprawdź, czy istnieje zapisany stan
        if (savedInstanceState != null) {
            scannedProductList = (ArrayList<Product>) savedInstanceState.getSerializable(SCANNED_LIST_KEY);
        } else {
            scannedProductList = (ArrayList<Product>) getIntent().getSerializableExtra("scannedList");
            if (scannedProductList == null) {
                scannedProductList = new ArrayList<>();
            }
        }

        // Tworzymy adapter i ustawiamy go na ListView
        adapter = new ScannedProductAdapter(this, scannedProductList);
        listViewScanned.setAdapter(adapter);

        // Obsługuje kliknięcie przycisku "Powrót do menu"
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ScannedProductsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Zakończ aktywność, aby użytkownik nie wrócił do niej po powrocie do menu
        });

        // Obsługuje kliknięcie przycisku "Wyczyść listę"
        btnClearList.setOnClickListener(v -> {
            scannedProductList.clear(); // Czyścimy listę
            adapter.notifyDataSetChanged(); // Powiadamiamy adapter, że dane zostały zaktualizowane
            Toast.makeText(ScannedProductsActivity.this, "Lista została wyczyszczona", Toast.LENGTH_SHORT).show();
        });

        // Obsługuje kliknięcie przycisku "Powrót do skanera"
        btnBackToScanner.setOnClickListener(v -> {
            // Przechodzi do ScannerActivity
            Intent intent = new Intent(ScannedProductsActivity.this, ScannerActivity.class);
            startActivity(intent);
            finish(); // Zakończ aktywność, aby użytkownik nie wrócił do niej po powrocie do skanera
        });
    }


    // Zapisz stan listy podczas zmiany konfiguracji (np. obrót ekranu)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Zapisz listę zeskanowanych produktów w stanie
        outState.putSerializable(SCANNED_LIST_KEY, scannedProductList);
    }

    // Przywrócenie stanu po zmianie konfiguracji
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Przywrócenie listy zeskanowanych produktów
        scannedProductList = (ArrayList<Product>) savedInstanceState.getSerializable(SCANNED_LIST_KEY);
    }
}
