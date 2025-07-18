package expo.modules.locationtracking

import android.content.Intent
import android.content.Context
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.exception.Exceptions

class LocationTrackingModule : Module() {
  private val context: Context
    get() = appContext.reactContext ?: throw Exceptions.ReactContextLost()

  override fun definition() = ModuleDefinition {
    Name("LocationTracking")

    Events("onLocationUpdate", "onLocationError")

    Function("startTracking") {
      val intent = Intent(context, LocationTrackingService::class.java)
      context.startService(intent)
    }

    Function("stopTracking") {
      val intent = Intent(context, LocationTrackingService::class.java)
      context.stopService(intent)
    }

    // For parity with iOS, but these will just call the regular tracking on Android
    // as Android doesn't have a direct equivalent to "significant location changes" API
    // that relaunches the app in the same way. The foreground service is the robust way.
    Function("startSignificantTracking") {
      val intent = Intent(context, LocationTrackingService::class.java)
      context.startService(intent)
    }

    Function("stopSignificantTracking") {
      val intent = Intent(context, LocationTrackingService::class.java)
      context.stopService(intent)
    }
  }
}