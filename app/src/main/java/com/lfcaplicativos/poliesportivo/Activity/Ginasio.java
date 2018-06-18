package com.lfcaplicativos.poliesportivo.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lfcaplicativos.poliesportivo.Adapter.RecyclerGinasio;
import com.lfcaplicativos.poliesportivo.Objetos.Horarios;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class Ginasio extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private int position = 0;
    private RecyclerView recyclerGinasioHorario;
    private RecyclerView.Adapter mAdapter;
    private Preferencias preferencias;

    private JSONObject jsonobject, jsonobject1, jsonobject2;
    private JSONArray jsonarrayHorario, jsonarrayHorarios;
    private ProgressDialog mProgressDialog;
    private GoogleMap mMap;
    private TextView textViewNoTimeGinasio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ginasio);

        try {
            Bundle args = new Bundle();
            if (args != null) {
                args.putInt("position", position);
            }
            preferencias = new Preferencias(this);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(Chaves.ginasio_principal.get(position).getNome());
            setSupportActionBar(toolbar);

            textViewNoTimeGinasio = findViewById(R.id.textNoTimeGinasio);
            textViewNoTimeGinasio.setVisibility(View.INVISIBLE);

            recyclerGinasioHorario = findViewById(R.id.recyclerGinasioHoraio);
            recyclerGinasioHorario.setHasFixedSize(true);
            recyclerGinasioHorario.setLayoutManager(new LinearLayoutManager(this));

            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.DAY_OF_MONTH, 7);

            HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarGinasio)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(5)
                    .build();

            horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                @Override
                public void onDateSelected(Calendar date, int position) {
                    carregarHorario(date);
                }

                @Override
                public void onCalendarScroll(HorizontalCalendarView calendarView,
                                             int dx, int dy) {
                }

                @Override
                public boolean onDateLongClicked(Calendar date, int position) {
                    return true;
                }
            });

            carregarHorario(startDate);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


        } catch (Exception e) {
            Log.i("Erro: ", e.getMessage());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ginasio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_ginasio_map) {
            onClick(findViewById(id));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.item_ginasio_map)) {
            Intent intent;
            intent = new Intent(this, MapaGinasio.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }


    private void carregarHorario(final Calendar data) {

        final int dia = data.get(Calendar.DAY_OF_WEEK);
        textViewNoTimeGinasio.setVisibility(View.INVISIBLE);
        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.available_times) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.horarios_ginasio = new ArrayList<Horarios>();


                    String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_URL_HORARIOS) + "?GINASIO=" + String.valueOf(Chaves.ginasio_principal.get(position).getCodigo()) + "&DATA='" + DateFormat.format("yyyy-MM-dd", data) + "'&DIA=" + String.valueOf(dia));
                    jsonobject = new JSONObject(sJson);
                    jsonarrayHorario = jsonobject.getJSONArray("horario");

                    jsonobject1 = jsonarrayHorario.getJSONObject(0);

                    jsonarrayHorarios = jsonobject1.getJSONArray("horarios");
                    for (int i = 0; i < jsonarrayHorarios.length(); i++) {
                        jsonobject = jsonarrayHorarios.getJSONObject(i);

                        final Horarios horarios = new Horarios();
                        Chaves.horarios_ginasio.add(horarios);
                        horarios.setCodigo(jsonobject.optInt("id"));
                        horarios.setHoraInicial(jsonobject.optString("hora_ini").substring(0, 5));
                        horarios.setHoraFinal(jsonobject.optString("hora_fin").substring(0, 5));
                        horarios.setStratus(0);
                        horarios.setTextoStatus(getString(R.string.available));
                    }

                    jsonobject2 = jsonarrayHorario.getJSONObject(1);

                    jsonarrayHorarios = jsonobject2.getJSONArray("horario_marcado");
                    for (int i = 0; i < jsonarrayHorarios.length(); i++) {
                        jsonobject = jsonarrayHorarios.getJSONObject(i);


                        int x = Chaves.horarios_ginasio.size();
                        for (int j = 0; j < x; j++) {
                            if (Chaves.horarios_ginasio.get(j).getCodigo() == jsonobject.optInt("id_horario")) {
                                if (jsonobject.optString("id_usuario").equals(preferencias.getID())) {
                                    Chaves.horarios_ginasio.get(j).setStratus(1);
                                    Chaves.horarios_ginasio.get(j).setTextoStatus(getString(R.string.my));
                                } else {
                                    Chaves.horarios_ginasio.get(j).setStratus(2);
                                    Chaves.horarios_ginasio.get(j).setTextoStatus(getString(R.string.unavailable));
                                }
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(Chaves.horarios_ginasio.size() <= 0)
                                    textViewNoTimeGinasio.setVisibility(View.VISIBLE);
                                mAdapter = new RecyclerGinasio(Chaves.horarios_ginasio);
                                recyclerGinasioHorario.setAdapter(mAdapter);
                                ((RecyclerGinasio) mAdapter).setOnItemClickListener(new RecyclerGinasio.MyClickListener() {
                                    @Override
                                    public void onItemClick(int position, View v) {

                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressDialog.cancel();
            }
        }).start();
    }

}
