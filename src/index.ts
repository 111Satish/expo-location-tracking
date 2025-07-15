import { requireNativeModule } from 'expo-modules-core';
import { EventEmitter } from 'expo-modules-core';

// It loads the native module object from the JSI or falls back to the bridge module.
const LocationTrackingModule = requireNativeModule('LocationTracking');

const emitter = new EventEmitter(LocationTrackingModule);

export function startTracking() {
  return LocationTrackingModule.startTracking();
}

export function stopTracking() {
  return LocationTrackingModule.stopTracking();
}

export function startSignificantTracking() {
  return LocationTrackingModule.startSignificantTracking();
}

export function stopSignificantTracking() {
  return LocationTrackingModule.stopSignificantTracking();
}

export function addLocationUpdateListener(listener: (event: { latitude: number; longitude: number; altitude: number; accuracy: number; speed: number; timestamp: number }) => void) {
  return emitter.addListener('onLocationUpdate', listener);
}

export function addLocationErrorListener(listener: (event: { message: string }) => void) {
  return emitter.addListener('onLocationError', listener);
}