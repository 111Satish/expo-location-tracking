import ExpoModulesCore
import CoreLocation

public class LocationTrackingModule: Module, CLLocationManagerDelegate {
  private let locationManager = CLLocationManager()

  public func definition() -> ModuleDefinition {
    Name("LocationTracking")

    OnCreate {
      locationManager.delegate = self
      locationManager.requestAlwaysAuthorization()
      locationManager.allowsBackgroundLocationUpdates = true
      locationManager.pausesLocationUpdatesAutomatically = false
    }

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
  }

  public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
    // Here you can send the location to your server or store it locally
  }

  public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
    switch manager.authorizationStatus {
    case .authorizedAlways:
      print("Location authorization: Always")
    case .authorizedWhenInUse:
      print("Location authorization: When In Use")
    case .denied:
      print("Location authorization: Denied")
    case .notDetermined:
      print("Location authorization: Not Determined")
    case .restricted:
      print("Location authorization: Restricted")
    @unknown default:
      print("Location authorization: Unknown")
    }
  }

  public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
    print("Location manager failed with error: \(error.localizedDescription)")
  }
}