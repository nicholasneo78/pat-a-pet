package com.example.chatapp.Manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A manager to determine the location of the user
 * Also listens to updates of the user's location
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class LocManager implements LocationListener {

    /**
     * The current context of the manager
     */
    private final Context mContext;

    /**
     * flag for GPS status
     */
    boolean isGPSEnabled = false;

    /**
     * flag for network status
     */
    boolean isNetworkEnabled = false;

    /**
     * flag for GPS status
     */
    boolean canGetLocation = false;

    /**
     * The location of the user
     */
    Location location;

    /**
     * The latitude of the location
     */
    double latitude;

    /**
     * The longitude of the location
     */
    double longitude;

    /**
     * The minimum distance to change Updates in meters
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    /**
     * The minimum time between updates in milliseconds
     */
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    /**
     * Declaring a Location Manager
     */
    protected LocationManager locationManager;


    /**
     * Creates a LocManager with a 'null' context
     */
    public LocManager() {
        this.mContext = null;
    }

    /**
     * Creates a LocManager with the specified context
     * Gets the location of the user
     * @param context
     */
    public LocManager(Context context) {
        this.mContext = context;
        getLocation();
    }

    /**
     * The function does the following:
     * <ol>
     *     <li>Checks for network and GPS</li>
     *     <li>Gets the lastKnownLocation either by network or GPS</li>
     *     <li>Stops the GPS after location is gotten</li>
     * </ol>
     * @return The lastKnownLocation of the user
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {

                this.canGetLocation = true;

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } catch (SecurityException e) {

                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        } catch (SecurityException e) {

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        stopUsingGPS();
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS
     * */

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocManager.this);
        }
    }

    /**
     * Function to get latitude
     * */

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
