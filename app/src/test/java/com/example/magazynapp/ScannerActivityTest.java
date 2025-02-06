package com.example.magazynapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.os.Looper;

import com.example.magazynapp.Product;
import com.example.magazynapp.ScannerActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
class ScannerActivityTest {

    private ScannerActivity scannerActivity;

    @Mock
    private Looper mockLooper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        // Initialize the activity with Robolectric to mock framework components
        scannerActivity = Robolectric.setupActivity(ScannerActivity.class);
    }

    @Test
    void testProcessScannedCode_ProductExists() {
        // Creating a mock product
        Product mockProduct = new Product("Testowy Produkt", 10.0, 5, "123456");
        scannerActivity.getProductMap().put("123456", mockProduct);

        // Call the method under test
        scannerActivity.processScannedCode("123456");

        // Verify that the product was added to scannedProducts
        assertTrue(scannerActivity.getScannedProducts().containsKey("123456"));
        assertEquals(1, scannerActivity.getScannedProducts().get("123456")); // Quantity should be 1

        // Simulate scanning the same product again
        scannerActivity.processScannedCode("123456");
        assertEquals(2, scannerActivity.getScannedProducts().get("123456")); // Quantity should be 2
    }

    @Test
    void testProcessScannedCode_ProductDoesNotExist() {
        // Call the method with a non-existent product
        scannerActivity.processScannedCode("000000");

        // The product should not be added to scannedProducts
        assertFalse(scannerActivity.getScannedProducts().containsKey("000000"));
    }
}
