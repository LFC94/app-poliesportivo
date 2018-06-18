package com.lfcaplicativos.poliesportivo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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

public class MapaGinasio extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private int position;
    private ProgressDialog mProgressDialog;
    private Marker currentLocationMaker;


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

        } catch (Exception ignored) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        adicionarLocalAtual();
    }


    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000;// Time in milliseconds


        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS) {
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

        if (isGPS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.map) + "...", true);
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mProgressDialog != null)
            mProgressDialog.cancel();
        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        LatLng currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permitido = false;
        if (grantResults.length > 0) {
            permitido = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permitido = false;
                    break;
                }
            }
        }
        if (permitido)
            switch (requestCode) {
                case Chaves.CHAVE_PERMISAO_LOCATION:
                    adicionarLocalAtual();
                    break;
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Chaves.CHAVE_RESULT_LOCATION:
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPS) {
                    startGettingLocations();
                }
                break;
        }
    }

    public void adicionarLocalAtual() {

        LatLng latLng = new LatLng(Chaves.ginasio_principal.get(position).getLatitude(), Chaves.ginasio_principal.get(position).getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(Chaves.ginasio_principal.get(position).getNome()));
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(latLng).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (Permissao.validaPermicao(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                Permissao.validaPermicao(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            startGettingLocations();
        } else {
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            Permissao.chamarPermicao(this, permissions.toArray(new String[permissions.size()]),
                    Chaves.CHAVE_PERMISAO_LOCATION);

        }
    }
}
