const { createRunOncePlugin, withAndroidManifest, withInfoPlist } = require('@expo/config-plugins');

const withLocationTracking = (config) => {
  config = withAndroidManifest(config, (config) => {
    const mainApplication = config.modResults.manifest.application[0];
    mainApplication.service = mainApplication.service || [];
    mainApplication.service.push({
      $: {
        'android:name': 'expo.modules.locationtracking.LocationTrackingService',
        'android:foregroundServiceType': 'location',
      },
    });

    config.modResults.manifest['uses-permission'] = config.modResults.manifest['uses-permission'] || [];
    config.modResults.manifest['uses-permission'].push(
      { $: { 'android:name': 'android.permission.ACCESS_FINE_LOCATION' } },
      { $: { 'android:name': 'android.permission.FOREGROUND_SERVICE' } },
      { $: { 'android:name': 'android.permission.ACCESS_BACKGROUND_LOCATION' } },
    );

    return config;
  });

  config = withInfoPlist(config, (config) => {
    config.modResults.NSLocationWhenInUseUsageDescription = 'This app requires location access to track your location.';
    config.modResults.NSLocationAlwaysAndWhenInUseUsageDescription = 'This app requires location access to track your location in the background.';
    config.modResults.UIBackgroundModes = config.modResults.UIBackgroundModes || [];
    if (!config.modResults.UIBackgroundModes.includes('location')) {
      config.modResults.UIBackgroundModes.push('location');
    }
    return config;
  });

  return config;
};

module.exports = createRunOncePlugin(withLocationTracking, 'location-tracking', '1.0.0');
