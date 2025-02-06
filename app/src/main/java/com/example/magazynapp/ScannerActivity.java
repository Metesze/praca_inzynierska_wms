package com.example.magazynapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

public class ScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;

    private Button btnScan, btnViewScanned, btnGenerateReport;
    private ArrayList<Product> productList; // Produkty z bazy danych
    private HashMap<String, Integer> scannedProducts; // Kod kreskowy -> ilość
    private HashMap<String, Product> productMap; // Produkt -> kod kreskowy
    private FirebaseFirestore db; // Firebase Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        btnScan = findViewById(R.id.btnScan);
        btnViewScanned = findViewById(R.id.btnViewScanned);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        productList = new ArrayList<>();
        scannedProducts = new HashMap<>();
        productMap = new HashMap<>();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance

        // Wyłącz przycisk skanowania do czasu załadowania danych
        btnScan.setEnabled(false);

        // Pobieranie danych z bazy Firestore
        loadProductsFromDatabase();

        btnScan.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startScanning();
            } else {
                requestCameraPermission();
            }
        });

        btnViewScanned.setOnClickListener(v -> showScannedProducts());

        btnGenerateReport.setOnClickListener(v -> generateInventoryReport());

        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
            intent.putExtra("role", getIntent().getStringExtra("role")); // Przekazanie roli
            startActivity(intent);
            finish();
        });
    }
    public HashMap<String, Integer> getScannedProducts() {
        return scannedProducts;
    }

    public HashMap<String, Product> getProductMap() {
        return productMap;
    }


    // Funkcja do ładowania produktów z Firestore
    private void loadProductsFromDatabase() {
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            productList.clear();
                            productMap.clear();
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                Product product = document.toObject(Product.class);
                                if (product != null) {
                                    productList.add(product);
                                    productMap.put(product.getBarcode(), product);  // Mapa kodu kreskowego do produktu
                                }
                            }
                            Log.d("Firestore", "Załadowano łącznie: " + productList.size() + " produktów.");
                        }
                        btnScan.setEnabled(true); // Włącz przycisk po załadowaniu danych
                    } else {
                        Log.e("Firestore", "Błąd podczas ładowania produktów", task.getException());
                        Toast.makeText(this, "Błąd podczas ładowania produktów", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Funkcja do sprawdzania uprawnień do kamery
    boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // Funkcja do żądania uprawnień do kamery
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    // Rozpoczyna proces skanowania
    private void startScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Zeskanuj kod kreskowy");
        integrator.setBeepEnabled(true);
        integrator.setCameraId(0);
        integrator.initiateScan();
    }

    // Obsługa wyników skanowania
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                String scannedCode = result.getContents();
                processScannedCode(scannedCode);
            } else {
                Toast.makeText(this, "Skanowanie anulowane", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Obsługa przetworzonego kodu
    // Obsługa przetworzonego kodu
    void processScannedCode(String scannedCode) {
        // Sprawdzanie, czy produkt o danym kodzie istnieje w bazie danych
        Product product = productMap.get(scannedCode);  // Sprawdzamy w mapie produktów

        if (product != null) {
            // Jeżeli produkt istnieje, dodajemy lub aktualizujemy liczbę skanów
            int newCount = scannedProducts.getOrDefault(scannedCode, 0) + 1;
            scannedProducts.put(scannedCode, newCount);  // Zwiększamy liczbę zeskanowań
            Toast.makeText(this, "Zeskanowano: " + product.getName(), Toast.LENGTH_SHORT).show();
        } else {
            // Jeśli produkt o danym kodzie nie istnieje, wyświetlamy komunikat
            Toast.makeText(this, "Produkt o kodzie: " + scannedCode + " nie istnieje w bazie", Toast.LENGTH_SHORT).show();
        }
    }


    // Wyświetla zeskanowane produkty
    private void showScannedProducts() {
        ArrayList<Product> scannedList = new ArrayList<>();
        for (String barcode : scannedProducts.keySet()) {
            Product product = productMap.get(barcode);
            if (product != null) {
                Product scannedProduct = new Product(
                        product.getName(),
                        product.getPrice(),
                        scannedProducts.get(barcode),
                        barcode
                );
                scannedList.add(scannedProduct);
            }
        }

        // Przekazanie zeskanowanej listy do nowej aktywności
        Intent intent = new Intent(this, ScannedProductsActivity.class);
        intent.putExtra("scannedList", scannedList);
        startActivity(intent);
    }

    // Generuje raport różnic inwentarzowych
    private void generateInventoryReport() {
        StringBuilder report = new StringBuilder();
        report.append("Raport inwentarzu:\n\n");

        for (Product product : productList) {
            int databaseQuantity = product.getQuantity();
            int scannedQuantity = scannedProducts.getOrDefault(product.getBarcode(), 0);

            if (databaseQuantity != scannedQuantity) {
                report.append("Produkt: ").append(product.getName())
                        .append("\nIlość w bazie: ").append(databaseQuantity)
                        .append("\nIlość zeskanowana: ").append(scannedQuantity)
                        .append("\n\n");
            }
        }

        // Przekazanie raportu do ReportActivity
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("report", report.toString());
        startActivity(intent);
    }

    // Obsługa wyników uprawnień
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Brak dostępu do kamery", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
