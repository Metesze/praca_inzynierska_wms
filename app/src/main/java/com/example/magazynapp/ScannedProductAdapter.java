package com.example.magazynapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ScannedProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> scannedProductList;

    // Konstruktor
    public ScannedProductAdapter(Context context, ArrayList<Product> scannedProductList) {
        this.context = context;
        this.scannedProductList = scannedProductList;
    }

    @Override
    public int getCount() {
        return scannedProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return scannedProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Użycie ViewHolder dla lepszej wydajności
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_scanned_product, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewName = convertView.findViewById(R.id.textViewProductName);
            viewHolder.textViewQuantity = convertView.findViewById(R.id.textViewProductQuantity);
            viewHolder.textViewPrice = convertView.findViewById(R.id.textViewProductPrice);

            convertView.setTag(viewHolder);  // Zapisujemy ViewHolder w konwercie
        } else {
            viewHolder = (ViewHolder) convertView.getTag();  // Pobieramy ViewHolder
        }

        // Pobieranie produktu z listy
        Product product = scannedProductList.get(position);

        // Wypełnianie widoków danymi z produktu
        viewHolder.textViewName.setText(product.getName());
        viewHolder.textViewQuantity.setText("Ilość: " + product.getQuantity());

        // Formatowanie ceny (np. do 2 miejsc po przecinku)
        String formattedPrice = formatPrice(product.getPrice());
        viewHolder.textViewPrice.setText("Cena: " + formattedPrice + " PLN");

        return convertView;
    }

    // Helper method to format the price to 2 decimal places
    private String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(price);
    }

    // ViewHolder pattern for better performance
    private static class ViewHolder {
        TextView textViewName;
        TextView textViewQuantity;
        TextView textViewPrice;
    }
}
