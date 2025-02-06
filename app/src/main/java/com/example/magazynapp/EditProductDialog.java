package com.example.magazynapp;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditProductDialog extends Dialog {

    private FirebaseFirestore db;
    private ModifyActivity modifyActivity;

    public EditProductDialog(Context context, String productKey, String productName, double productPrice, int productQuantity, String productBarcode, FirebaseFirestore db, ModifyActivity modifyActivity) {
        super(context);
        setContentView(R.layout.dialog_edit_product);

        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextPrice = findViewById(R.id.editTextPrice);
        EditText editTextQuantity = findViewById(R.id.editTextQuantity);
        EditText editTextBarcode = findViewById(R.id.editTextBarcode);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        this.db = db;
        this.modifyActivity = modifyActivity;

        editTextName.setText(productName);
        editTextPrice.setText(String.valueOf(productPrice));
        editTextQuantity.setText(String.valueOf(productQuantity));
        editTextBarcode.setText(productBarcode);
        editTextBarcode.setEnabled(false);

        btnSave.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            double newPrice = Double.parseDouble(editTextPrice.getText().toString());
            int newQuantity = Integer.parseInt(editTextQuantity.getText().toString());

            DocumentReference productRef = db.collection("Products").document(productKey);
            productRef.get().addOnSuccessListener(snapshot -> {
                int oldQuantity = snapshot.getLong("quantity").intValue();
                productRef.update(
                        "name", newName,
                        "price", newPrice,
                        "quantity", newQuantity
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Produkt zaktualizowany", Toast.LENGTH_SHORT).show();
                        logImpexp(newQuantity > oldQuantity ? "import" : "export", Math.abs(newQuantity - oldQuantity));
                        dismiss();
                        modifyActivity.fetchProductsFromDatabase();
                    } else {
                        Toast.makeText(context, "Błąd aktualizacji", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnDelete.setOnClickListener(v -> {
            DocumentReference productRef = db.collection("Products").document(productKey);
            productRef.get().addOnSuccessListener(snapshot -> {
                int quantity = snapshot.getLong("quantity").intValue();
                productRef.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Produkt usunięty", Toast.LENGTH_SHORT).show();
                        logImpexp("export", quantity);
                        dismiss();
                        modifyActivity.fetchProductsFromDatabase();
                    } else {
                        Toast.makeText(context, "Błąd usuwania", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private void logImpexp(String type, int quantity) {
        HashMap<String, Object> log = new HashMap<>();
        log.put("type", type);
        log.put("quantity", quantity);

        // Formatowanie bieżącej daty do formatu string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());  // Użycie bieżącej daty i godziny
        log.put("date", currentDate); // Dodanie daty jako string

        db.collection("Impexp")
                .add(log)
                .addOnSuccessListener(doc -> {})
                .addOnFailureListener(e -> {});
    }
}
