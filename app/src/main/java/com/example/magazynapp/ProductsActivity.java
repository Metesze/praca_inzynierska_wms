package com.example.magazynapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        listView = findViewById(R.id.listViewProducts);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        productList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);

        // Inicjalizacja Firestore
        db = FirebaseFirestore.getInstance();

        // Pobieranie danych z kolekcji "Products" w Firestore
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                String productName = document.getString("name");
                                Double price = document.getDouble("price");
                                Long quantity = document.getLong("quantity");
                                String barcode = document.getString("barcode");

                                if (productName != null && price != null && quantity != null && barcode != null) {
                                    // Dodaj produkt do listy z kodem kreskowym
                                    productList.add("Nazwa: " + productName +
                                            " - Cena: " + price +
                                            " - Ilość: " + quantity +
                                            " - Kod kreskowy: " + barcode);
                                }
                            }
                            // Aktualizuj ListView
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w("ProductsActivity", "Error getting documents.", task.getException());
                    }
                });

        // Obsługa przycisku powrotu do menu głównego
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, MainActivity.class);
            intent.putExtra("role", getIntent().getStringExtra("role")); // Przekazanie roli
            startActivity(intent);
            finish();
        });
    }
}
