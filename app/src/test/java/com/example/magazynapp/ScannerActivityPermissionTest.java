package com.example.magazynapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.content.pm.PackageManager;

import com.example.magazynapp.ScannerActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ScannerActivityPermissionTest {

    private ScannerActivity scannerActivity;

    @Mock
    private PackageManager mockPackageManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scannerActivity = new ScannerActivity();
    }

    @Test
    void testCheckCameraPermission_Granted() {
        when(mockPackageManager.checkPermission("android.permission.CAMERA", "com.example.magazynapp"))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean result = scannerActivity.checkCameraPermission();
        assertTrue(result);
    }

    @Test
    void testCheckCameraPermission_Denied() {
        when(mockPackageManager.checkPermission("android.permission.CAMERA", "com.example.magazynapp"))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        boolean result = scannerActivity.checkCameraPermission();
        assertFalse(result);
    }
}
