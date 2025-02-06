package com.example.magazynapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    private TextView textViewReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Inicjalizacja widoków
        textViewReport = findViewById(R.id.textViewReport);
        Button btnBackToScanner = findViewById(R.id.btnBackToScanner);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        // Pobranie raportu z Intenta
        String report = getIntent().getStringExtra("report");

        if (report != null) {
            // Ustawienie treści raportu w TextView
            textViewReport.setText(report);
        } else {
            // Jeśli raport jest pusty, wyświetlamy odpowiedni komunikat
            textViewReport.setText("Brak danych do raportu.");
        }

        // Obsługuje kliknięcie przycisku "Powrót do skanera"
        btnBackToScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przechodzi do ScannerActivity
                Intent intent = new Intent(ReportActivity.this, ScannerActivity.class);
                startActivity(intent);
                finish(); // Zakończ aktywność, aby użytkownik nie wrócił do niej po powrocie do skanera
            }
        });

        // Obsługuje kliknięcie przycisku "Powrót do menu"
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przechodzi do MainActivity (menu)
                Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Zakończ aktywność, aby użytkownik nie wrócił do niej po powrocie do menu
            }
        });
    }
}
