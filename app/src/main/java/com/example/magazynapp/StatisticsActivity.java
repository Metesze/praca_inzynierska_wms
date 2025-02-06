package com.example.magazynapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private Button btnStartDate, btnEndDate, btnGenerate;
    private Calendar startDate, endDate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        barChart = findViewById(R.id.barChart);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnGenerate = findViewById(R.id.btnGenerate);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        db = FirebaseFirestore.getInstance();

        // Date pickers
        btnStartDate.setOnClickListener(v -> showDatePickerDialog(startDate, btnStartDate));
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(endDate, btnEndDate));

        // Generate chart
        btnGenerate.setOnClickListener(v -> generateChart());
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
            intent.putExtra("role", getIntent().getStringExtra("role")); // Przekazanie roli
            startActivity(intent);
            finish();
        });
    }

    private void showDatePickerDialog(Calendar calendar, Button button) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            button.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void generateChart() {
        db.collection("Impexp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<BarEntry> importEntries = new ArrayList<>();
                        ArrayList<BarEntry> exportEntries = new ArrayList<>();
                        ArrayList<String> months = new ArrayList<>();  // Lista miesięcy (unikalne)
                        ArrayList<Integer> monthIndices = new ArrayList<>();  // Indeksy miesięcy dla pozycji X

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String type = document.getString("type");
                            long quantity = document.getLong("quantity");
                            String date = document.getString("date");

                            if (isWithinDateRange(date)) {
                                // Używamy tylko miesiąca z daty (1 - 12)
                                int month = Integer.parseInt(date.substring(5, 7)) - 1; // Miesiące są od 0 do 11

                                // Dodajemy nazwę miesiąca tylko raz, jeśli jeszcze go nie dodano
                                if (!months.contains(getMonthName(month))) {
                                    months.add(getMonthName(month)); // Dodajemy nazwę miesiąca
                                    monthIndices.add(month); // Przypisujemy miesiąc do odpowiedniego indeksu
                                }

                                // Ustawienie słupków importu i eksportu
                                if ("import".equals(type)) {
                                    importEntries.add(new BarEntry(month + 0.25f, quantity)); // Przesunięcie słupka importu
                                } else if ("export".equals(type)) {
                                    exportEntries.add(new BarEntry(month - 0.25f, quantity)); // Przesunięcie słupka eksportu
                                }
                            }
                        }

                        // Tworzymy zestawy danych dla importu i eksportu
                        BarDataSet importDataSet = new BarDataSet(importEntries, "Import");
                        importDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
                        importDataSet.setValueTextColor(ColorTemplate.COLORFUL_COLORS[0]);

                        BarDataSet exportDataSet = new BarDataSet(exportEntries, "Export");
                        exportDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]);
                        exportDataSet.setValueTextColor(ColorTemplate.COLORFUL_COLORS[1]);

                        // Ustawienie szerokości słupków
                        BarData barData = new BarData(importDataSet, exportDataSet);
                        barData.setBarWidth(0.3f); // Ustawiamy szerokość słupków

                        barChart.setData(barData);

                        // Konfiguracja osi X
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); // Wyświetlamy tylko unikalne miesiące
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1f); // Zapewnia, że etykiety będą widoczne
                        xAxis.setLabelCount(months.size(), true); // Dopasowanie liczby etykiet na osi X

                        // Konfiguracja osi Y
                        YAxis leftAxis = barChart.getAxisLeft();
                        leftAxis.setAxisMinimum(0);
                        barChart.getAxisRight().setEnabled(false);

                        // Odświeżenie wykresu
                        Log.d("Months", months.toString());  // Logowanie miesięcy
                        barChart.invalidate(); // Refresh chart
                    }
                });
    }

    // Funkcja zwracająca nazwę miesiąca na podstawie jego numeru (0-11)
    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }


    private boolean isWithinDateRange(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long date = sdf.parse(dateString).getTime();
            return date >= startDate.getTimeInMillis() && date <= endDate.getTimeInMillis();
        } catch (Exception e) {
            return false;
        }
    }
}
