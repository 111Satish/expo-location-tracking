package expo.modules.locationtracking

import android.content.Intent
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class LocationTrackingModule : Module() {
  override fun definition() = ModuleDefinition {    
    Name("LocationTracking")

    Function("startTracking") { 
      val intent = Intent(appContext.reactContext, LocationTrackingService::class.java)
      appContext.reactContext?.startService(intent)
    }

    Function("stopTracking") {
      val intent = Intent(appContext.reactContext, LocationTrackingService::class.java)
      appContext.reactContext?.stopService(intent)
    }
  }
}
