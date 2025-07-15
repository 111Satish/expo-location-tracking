import ExpoModulesCore
import CoreLocation

public class LocationTrackingModule: Module, CLLocationManagerDelegate {
  private let locationManager = CLLocationManager()
  private var hasListeners = false

  public func definition() -> ModuleDefinition {
    Name("LocationTracking")

    OnCreate {
      locationManager.delegate = self
      locationManager.requestAlwaysAuthorization()
      locationManager.allowsBackgroundLocationUpdates = true
      locationManager.pausesLocationUpdatesAutomatically = false
      locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
    }

    Events("onLocationUpdate", "onLocationError")

    Function("startTracking") {
      locationManager.startUpdatingLocation()
    }

    Function("stopTracking") {
      locationManager.stopUpdatingLocation()
      locationManager.stopMonitoringSignificantLocationChanges()
    }

    Function("startSignificantTracking") {
      locationManager.startMonitoringSignificantLocationChanges()
    }

    Function("stopSignificantTracking") {
      locationManager.stopMonitoringSignificantLocationChanges()
    }

    OnStartObserving {
      hasListeners = true
    }

    OnStopObserving {
      hasListeners = false
    }
  }

  public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
    guard hasListeners else { return }
    guard let location = locations.last else { return }

    sendEvent("onLocationUpdate", [
      "latitude": location.coordinate.latitude,
      "longitude": location.coordinate.longitude,
      "altitude": location.altitude,
      "accuracy": location.horizontalAccuracy,
      "speed": location.speed,
      "timestamp": location.timestamp.timeIntervalSince1970 * 1000 // Convert to milliseconds
    ])
  }

  public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
    // Handle authorization status changes if needed
    // You might want to send an event to JS about the status change
  }

  public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
    guard hasListeners else { return }
    sendEvent("onLocationError", ["message": error.localizedDescription])
  }
}
