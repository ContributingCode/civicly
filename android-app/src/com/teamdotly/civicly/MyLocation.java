package com.teamdotly.civicly;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class MyLocation {
	protected static LocationManager locationManager;
	protected static Location prev;
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	 private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
	

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	
	public static Location getLocation(Context c) {
		locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new LocationListener() {
					
					public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
						// TODO Auto-generated method stub
						
					}
					
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					public void onLocationChanged(Location arg0) {
						// TODO Auto-generated method stub
						
					}
				}
        		);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
     	   		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
     	   		
        }
        if(!isBetterLocation( location, prev))
     	       location=prev;
        else
     	       prev = location;
        
	       if (location != null) {
	            String message = String.format(
	                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
	                    location.getLongitude(), location.getLatitude()
	            );
	        }
	       return location;

	}
}
