package com.example.magazynapp;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddProductDialog extends Dialog {
    private static final String TAG = "AddProductDialog";
    private FirebaseFirestore db;
    private ModifyActivity modifyActivity;

    public AddProductDialog(Context context, FirebaseFirestore db, ModifyActivity modifyActivity) {
        super(context);
        setContentView(R.layout.dialog_add_product);

        // Powiązanie widoków z layoutem
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextPrice = findViewById(R.id.editTextPrice);
        EditText editTextQuantity = findViewById(R.id.editTextQuantity);
        EditText editTextBarcode = findViewById(R.id.editTextBarcode);
        Button btnAdd = findViewById(R.id.btnAdd);

        // Inicjalizacja Firestore
        this.db = db;
        this.modifyActivity = modifyActivity;

        // Obsługa kliknięcia przycisku dodawania produktu
        btnAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String priceInput = editTextPrice.getText().toString().trim();
            String quantityInput = editTextQuantity.getText().toString().trim();
            String barcode = editTextBarcode.getText().toString().trim();

            // Walidacja danych wejściowych
            if (name.isEmpty() || priceInput.isEmpty() || quantityInput.isEmpty() || barcode.isEmpty()) {
                Toast.makeText(context, "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            int quantity;

            try {
                price = Double.parseDouble(priceInput);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Nieprawidłowa wartość ceny", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                quantity = Integer.parseInt(quantityInput);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Nieprawidłowa ilość", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tworzenie obiektu produktu
            Product product = new Product(name, price, quantity, barcode);

            // Dodawanie produktu do Firestore
            db.collection("Products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(context, "Produkt dodany pomyślnie", Toast.LENGTH_SHORT).show();
                        logImpexp("import", quantity); // Dodanie logu importu
                        dismiss();
                        modifyActivity.fetchProductsFromDatabase();  // Odświeżenie listy po dodaniu produktu
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Błąd podczas dodawania produktu", e);
                        Toast.makeText(context, "Błąd dodawania produktu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Funkcja do logowania importu/eksportu w kolekcji Impexp
    private void logImpexp(String type, int quantity) {
        // Tworzenie obiektu HashMap do przechowywania danych logu
        HashMap<String, Object> log = new HashMap<>();
        log.put("type", type);
        log.put("quantity", quantity);

        // Formatowanie bieżącej daty do formatu string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());  // Użycie bieżącej daty i godziny
        log.put("date", currentDate); // Dodanie daty jako string

        // Dodanie logu do kolekcji "Impexp" w Firestore
        db.collection("Impexp")
                .add(log)
                .addOnSuccessListener(doc -> Log.d(TAG, "Log dodany do Impexp"))
                .addOnFailureListener(e -> Log.e(TAG, "Błąd dodawania logu do Impexp", e));
    }
}
