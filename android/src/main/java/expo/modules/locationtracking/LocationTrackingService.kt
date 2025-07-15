package expo.modules.locationtracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.events.EventEmitter

class LocationTrackingService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val NOTIFICATION_CHANNEL_ID = "LocationTrackingChannel"
    private val NOTIFICATION_ID = 12345
    private var eventEmitter: EventEmitter? = null

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        // Get the EventEmitter from the AppContext if available
        val appCtx = applicationContext as? AppContext
        eventEmitter = appCtx?.get ;<LocationTrackingModule>()?.eventEmitter

        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, // You can choose GPS_PROVIDER, NETWORK_PROVIDER, or PASSIVE_PROVIDER
                5000, // Minimum time interval between updates in milliseconds (e.g., 5 seconds)
                10f, // Minimum distance between updates in meters (e.g., 10 meters)
                this
            )
        } catch (e: SecurityException) {
            eventEmitter?.emit("onLocationError", mapOf("message" to "Location permission not granted: ${e.message}"))
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        // Send location updates to JavaScript
        eventEmitter?.emit("onLocationUpdate", mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "altitude" to location.altitude,
            "accuracy" to location.accuracy,
            "speed" to location.speed,
            "timestamp" to location.time
        ))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle status changes if needed
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled if needed
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Tracking Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Location Tracking")
        .setContentText("Tracking your location in the background")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation) // You might want to use your app's icon here
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
