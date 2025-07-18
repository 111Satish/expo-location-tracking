package expo.modules.locationtracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import expo.modules.kotlin.defaultmodules.MainApplication

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val eventEmitter = (application as? MainApplication)?.reactNativeHost?.reactInstanceManager?.currentReactContext
                        ?.getJSModule(expo.modules.core.interfaces.JavaScriptContextProvider::class.java)

                    eventEmitter?.let {
                        val event = Arguments.createMap().apply {
                            putDouble("latitude", location.latitude)
                            putDouble("longitude", location.longitude)
                            putDouble("altitude", location.altitude)
                            putFloat("accuracy", location.accuracy)
                            putFloat("speed", location.speed)
                            putDouble("timestamp", location.time.toDouble())
                        }
                        it.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit("onLocationUpdate", event)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)

        startLocationUpdates()

        return START_STICKY
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("LocationTrackingService", "Location permission not granted", e)
            // Consider sending an error event to JS
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "location_tracking_channel")
            .setContentTitle("Location Tracking Active")
            .setContentText("Your location is being tracked.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Replace with your app icon
            .build()
    }

    private fun createNotificationChannel() {
        val name = "Location Tracking"
        val descriptionText = "Channel for location tracking service"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("location_tracking_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}