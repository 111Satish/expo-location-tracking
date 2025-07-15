package expo.modules.locationtracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class LocationTrackingModule : Module() {
    override fun definition() = ModuleDefinition {
        Name("LocationTracking")

        // Events that can be sent from the native module to JavaScript
        Events("onLocationUpdate", "onLocationError")

        Function("startTracking") {
            val context = appContext.reactContext ?: throw IllegalStateException("React ApplicationContext is null")

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw SecurityException("Location permissions not granted. Please ensure ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION are granted.")
            }

            // Start the LocationTrackingService
            val intent = Intent(context, LocationTrackingService::class.java)
            context.startService(intent)
        }

        Function("stopTracking") {
            val context = appContext.reactContext ?: throw IllegalStateException("React ApplicationContext is null")
            // Stop the LocationTrackingService
            val intent = Intent(context, LocationTrackingService::class.java)
            context.stopService(intent)
        }

        // You might not need separate functions for significant tracking if your service handles both
        // or if significant tracking is a configuration option for startTracking.
        // For now, I'll keep them as placeholders or remove if not explicitly needed.
        Function("startSignificantTracking") {
            // Implement start significant tracking logic here
            // This might involve configuring the LocationTrackingService differently
        }

        Function("stopSignificantTracking") {
            // Implement stop significant tracking logic here
        }
    }
}
