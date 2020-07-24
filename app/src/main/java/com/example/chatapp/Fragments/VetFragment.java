package com.example.chatapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.ConditionVariable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.MainActivity;
import com.example.chatapp.Manager.LocManager;
import com.example.chatapp.Model.Vet;
import com.example.chatapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Fragment class that displays the nearest Vets (5km radius) using GoogleMap
 * Controls what to be displayed on the "Vets" tab
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class VetFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its vet interface view
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment UI should be attached to
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state
     * @return Return the View for Chat Fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vet, container, false);
        final ArrayList<Vet> vets = readData();

        final LocManager locManager = new LocManager(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            /**
             * Set's up the GoogleMap with markers
             * <ol>
             *     <li>Checks if user has permissions again</li>
             *     <li>Clears old markers</li>
             *     <li>Search for vets & creates new markers on a 5km radius around the user's location</li>
             *     <li>Updates the markers when user updates their location</li>
             * </ol>
             * @param mMap The GoogleMap referenced
             */
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Toast.makeText(getContext(), "You didn't click allow", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            2);
                }

                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(locManager.getLatitude(),locManager.getLongitude()))
                        .zoom(15)
                        .bearing(0)
                        .tilt(45)
                        .build();

                // (camera, duration, callback)
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);

                for (int i=0; i<vets.size(); i++) {
                    Vet vet = vets.get(i);
                    if (distanceTo(locManager.getLocation(), 6371.01, vet.getLatitude(), vet.getLongtitude()) < 5) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(vet.getLatitude(), vet.getLongtitude()))
                                .title(vet.getName())
                                .snippet(vet.getAddress() + "  |  " + vet.getTel_office_1()));
                    }
                }

                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    /**
                     * The function does the following:
                     * <ol>
                     *     <li>Clears the current markers</li>
                     *     <li>Search for nearest vets in 5km radius</li>
                     *     <li>Adds the new markers to the map</li>
                     * </ol>
                     * @return Set's the listener back to false
                     */
                    @Override
                    public boolean onMyLocationButtonClick() {
                        mMap.clear(); // clear map markers

                        for (int i=0; i<vets.size(); i++) {
                            Vet vet = vets.get(i);
                            if (distanceTo(locManager.getLocation(), 6371.01, vet.getLatitude(), vet.getLongtitude()) < 5) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(vet.getLatitude(), vet.getLongtitude()))
                                        .title(vet.getName())
                                        .snippet(vet.getAddress() + "  |  " + vet.getTel_office_1()));
                            }
                        }
                        return false;
                    }
                });

            }
        });

        return rootView;
    }

    /**
     * Adapts the drawable to a suitable format to display on the map
     * @param context The context of the map
     * @param vectorResId The id of the drawable image
     * @return BitmapDescriptor suitable to be displayed on the map
     */
    private BitmapDescriptor bitmapDescriptionFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Reads the data ("licensed-vet-centres.csv") in the assets folder
     * Changes the Stringed latitude and longitude to a Double format
     * @return An arraylist of Vet object
     */
    private ArrayList<Vet> readData() {
        ArrayList<Vet> vets = new ArrayList<>();
        try {
            //String csvfileString = getActivity().getApplicationInfo().dataDir + File.separatorChar + "licensed-vet-centres.csv";

            InputStream is = getContext().getAssets().open("licensed-vet-centres.csv");
            InputStreamReader csvfile = new InputStreamReader(is);
            //File csvfile = new File(csvfileString);
            CSVReader reader = new CSVReader(csvfile);
            String[] token;

            reader.readNext();
            while((token = reader.readNext()) != null) {
                Vet vet = new Vet(token[0],
                                token[1],
                                token[2],
                                token[3],
                                token[4],
                                token[5],
                                token[6],
                                Double.parseDouble(token[7]),
                                Double.parseDouble(token[8]));
                vets.add(vet);
            }
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        return vets;
    }

    /**
     * Calculates the distance from one location to another based on the radius specified
     * @param location The current location of user
     * @param radius The radius of Earth
     * @param Lat The latitude of specific location
     * @param Long The longitude of specific location
     * @return
     */
    public double distanceTo(Location location, double radius, double Lat, double Long) {
        return Math.acos(Math.sin(Math.toRadians(Lat)) * Math.sin(Math.toRadians(location.getLatitude())) +
                Math.cos(Math.toRadians(Lat)) * Math.cos(Math.toRadians(location.getLatitude())) *
                Math.cos(Math.toRadians(Long) - Math.toRadians(location.getLongitude()))) * radius;
    }

}
