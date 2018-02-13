package com.lfcaplicativos.poliesportivo.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.Permissao;

import java.util.ArrayList;

public class MapaGinasio extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private int position;

    private Marker currentLocationMaker;
    private LatLng currentLocationLatLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_ginasio);
        try {
            Bundle args = new Bundle();
            args.putInt("position", position);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(Chaves.ginasio_principal.get(position).getLatitude(), Chaves.ginasio_principal.get(position).getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(Chaves.ginasio_principal.get(position).getNome()));
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(latLng).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000;// Time in milliseconds


        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.gps_disabled));
            alertDialog.setMessage(getString(R.string.enable_gps) + "?");
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, Chaves.CHAVE_RESULT_LOCATION);
                }
            });

            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (isNetwork) {
            // from Network Provider

            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        }

        if (isGPS) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.title(getString(R.string.current_location));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMaker = mMap.addMarker(markerOptions);

        //Move to new location
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

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

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.floating_mapaginasio_Local)) {

            if (currentLocationMaker != null) {
                currentLocationMaker.remove();
                currentLocationMaker = null;
                LatLng latLng = new LatLng(Chaves.ginasio_principal.get(position).getLatitude(), Chaves.ginasio_principal.get(position).getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(latLng).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {

                if (Permissao.validaPermicao(this, android.Manifest.permission.ACCESS_FINE_LOCATION, 1) &&
                        Permissao.validaPermicao(this, Manifest.permission.ACCESS_COARSE_LOCATION, 1)) {

                    startGettingLocations();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ArrayList<String> permissions = new ArrayList<>();
                        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

                        requestPermissions(permissions.toArray(new String[permissions.size()]),
                                1);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            int resultado = grantResults[i];
            if (resultado == PackageManager.PERMISSION_GRANTED) {
                startGettingLocations();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Chaves.CHAVE_RESULT_LOCATION && resultCode == Activity.RESULT_OK) {
            startGettingLocations();
        }
    }

}
