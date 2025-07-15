package com.reactnativelocationtracking

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class LocationTrackingModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "LocationTracking"
    }

    @ReactMethod
    fun startTracking(promise: Promise) {
        if (ContextCompat.checkSelfPermission(reactApplicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            promise.reject("PERMISSION_DENIED", "Location permission not granted")
            return
        }

        val intent = Intent(reactApplicationContext, LocationTrackingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reactApplicationContext.startForegroundService(intent)
        } else {
            reactApplicationContext.startService(intent)
        }
        promise.resolve(null)
    }

    @ReactMethod
    fun stopTracking() {
        val intent = Intent(reactApplicationContext, LocationTrackingService::class.java)
        reactApplicationContext.stopService(intent)
    }

    @ReactMethod
    fun requestBackgroundLocationPermission(promise: Promise) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(reactApplicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // We can't directly request background permission from a module. This should be done in the Activity.
                // For now, we'll just reject the promise. The user needs to grant this manually or via the main activity.
                promise.reject("PERMISSION_DENIED", "Background location permission not granted. Please request from Activity.")
            } else {
                promise.resolve(true)
            }
        } else {
            // On older Android versions, ACCESS_FINE_LOCATION implies background access.
            promise.resolve(true)
        }
    }
}
