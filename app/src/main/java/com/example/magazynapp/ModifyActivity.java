package com.example.magazynapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ModifyActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editTextSearch;
    private ArrayList<String> productList;
    private ArrayList<String> originalProductList; // Pełna kopia listy produktów
    private ArrayList<String> productKeys;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);


        listView = findViewById(R.id.listViewModify);
        editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu); // Przycisk powrotu

        productList = new ArrayList<>();

        productKeys = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);

        // Inicjalizacja Firestore
        db = FirebaseFirestore.getInstance();

        // Pobierz dane z Firestore
        fetchProductsFromDatabase();

        // Obsługa przycisku powrotu do menu głównego
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // Powrót do głównego menu
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Obsługa kliknięcia przycisku dodawania nowego produktu
        btnAddProduct.setOnClickListener(v -> {
            // Tworzymy instancję AddProductDialog i przekazujemy ją do wyświetlenia
            AddProductDialog addProductDialog = new AddProductDialog(ModifyActivity.this, db, this);
            addProductDialog.show();
        });

        // Obsługa kliknięcia na produkt w liście (edycja lub usunięcie)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < productList.size()) {
                String selectedProductName = productList.get(position);
                String selectedProductKey = productKeys.get(position);

                // Pobieramy szczegóły produktu z Firestore
                fetchProductDetails(selectedProductKey, selectedProductName);
            } else {
                Toast.makeText(ModifyActivity.this, "Błąd: Indeks produktu jest nieprawidłowy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Funkcja do pobierania produktów z Firestore
    public void fetchProductsFromDatabase() {
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            productList.clear();
                            productKeys.clear();
                            originalProductList = new ArrayList<>(); // Resetowanie oryginalnej listy

                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                String productName = document.getString("name");
                                if (productName != null) {
                                    productList.add(productName);
                                    originalProductList.add(productName); // Kopia pełnej listy
                                    productKeys.add(document.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ModifyActivity.this, "Błąd podczas ładowania produktów", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Funkcja do pobierania szczegółów produktu
    public void fetchProductDetails(String productKey, String productName) {
        db.collection("Products").document(productKey).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            double price = document.getDouble("price");
                            int quantity = document.getLong("quantity").intValue();
                            String barcode = document.getString("barcode");

                            // Przekaż te szczegóły do dialogu edycji
                            EditProductDialog editProductDialog = new EditProductDialog(ModifyActivity.this, productKey, name, price, quantity, barcode, db, this);
                            editProductDialog.show();
                        } else {
                            Toast.makeText(ModifyActivity.this, "Produkt nie istnieje", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ModifyActivity.this, "Błąd podczas pobierania danych produktu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Filtruje produkty na podstawie wprowadzonego tekstu
    private void filterProductList(String query) {
        if (query.isEmpty()) {
            // Jeśli pole jest puste, przywracamy oryginalną listę
            productList.clear();
            productList.addAll(originalProductList);
        } else {
            // W przeciwnym razie filtrujemy produkty
            ArrayList<String> filteredList = new ArrayList<>();
            for (String productName : originalProductList) {
                if (productName.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(productName);
                }
            }
            productList.clear();
            productList.addAll(filteredList);
        }
        adapter.notifyDataSetChanged(); // Aktualizacja widoku
    }

}
