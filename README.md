# Expo Location Tracking

An Expo package for background location tracking.

## Installation

```bash
expo install expo-location-tracking
```

## Usage

```javascript
import LocationTracking from 'expo-location-tracking';

// Start tracking
LocationTracking.startTracking();

// Stop tracking
LocationTracking.stopTracking();
```

This package uses a config plugin to automatically configure the necessary permissions and background modes for both Android and iOS. No further configuration should be necessary.