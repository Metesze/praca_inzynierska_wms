package org.example;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BarcodeGeneratorWithMapping {
    public static void main(String[] args) {
        try {
            // Tworzenie folderu na obrazy kodów kreskowych
            File directory = new File("barcodes");
            if (!directory.exists()) {
                directory.mkdir(); // Tworzy folder, jeśli nie istnieje
            }

            // Tablica z kodami kreskowymi (12 cyfr bez cyfry kontrolnej)
            String[] productBarcodes = {
                    "123456789012",
                    "234567890123",
                    "345678901234",
                    "456789012345",
                    "567890123456",
                    "678901234567",
                    "789012345678",
                    "890123456789",
                    "901234567890",
                    "012345678901"
            };

            // Mapa do przechowywania powiązań kodów z nazwami plików
            Map<String, String> barcodeToFileMap = new HashMap<>();

            // Generowanie kodów kreskowych i zapisywanie plików
            for (String barcodeData : productBarcodes) {
                // Obliczanie cyfry kontrolnej
                int checkDigit = calculateCheckDigit(barcodeData);
                String fullBarcode = barcodeData + checkDigit;

                // Nazwa pliku obrazu
                String fileName = "barcodes/barcode_" + fullBarcode + ".png";

                // Generowanie obrazu kodu kreskowego
                BitMatrix matrix = new MultiFormatWriter().encode(
                        fullBarcode,
                        BarcodeFormat.EAN_13,
                        300, 150
                );

                // Zapis obrazu do pliku
                Path outputPath = new File(fileName).toPath();
                MatrixToImageWriter.writeToPath(matrix, "PNG", outputPath);

                // Dodawanie powiązania do mapy
                barcodeToFileMap.put(fullBarcode, fileName);

                System.out.println("Kod kreskowy: " + fullBarcode + " zapisany jako: " + fileName);
            }

            // Wyświetlanie mapowania kodów kreskowych do plików
            System.out.println("\nPowiązania kodów kreskowych z plikami:");
            for (Map.Entry<String, String> entry : barcodeToFileMap.entrySet()) {
                System.out.println("Kod kreskowy: " + entry.getKey() + " -> Plik: " + entry.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metoda do obliczania cyfry kontrolnej dla EAN-13
    public static int calculateCheckDigit(String data) {
        int sum = 0;
        for (int i = 0; i < data.length(); i++) {
            int digit = Character.getNumericValue(data.charAt(i));
            if (i % 2 == 0) { // Nieparzyste pozycje
                sum += digit;
            } else { // Parzyste pozycje
                sum += digit * 3;
            }
        }
        return (10 - (sum % 10)) % 10;
    }
}
