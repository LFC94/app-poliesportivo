package com.lfcaplicativos.poliesportivo.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class Ginasio extends AppCompatActivity implements View.OnClickListener {

    private int position = 0;
    private HorizontalCalendar horizontalCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ginasio);

        try {
            Bundle args = new Bundle();
            if (args != null) {
                args.putInt("position", position);
            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(Chaves.ginasio_principal.get(position).getNome());
            setSupportActionBar(toolbar);


            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.DAY_OF_MONTH, 7);


            final Calendar defaultSelectedDate = Calendar.getInstance();

            horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(5)
                    .configure()
                    .formatTopText("MMM yyyy")
                    .formatMiddleText("dd")
                    .formatBottomText("EEE")
                    .textColor(Color.LTGRAY, Color.WHITE)
                    .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                    .end()
                    .defaultSelectedDate(defaultSelectedDate)
                    .build();
            horizontalCalendar.goToday(true);

            Log.i("Default Date", DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString());
            Toast.makeText(Ginasio.this, DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString(), Toast.LENGTH_SHORT).show();
            horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                @Override
                public void onDateSelected(Calendar date, int position) {
                    String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                    Toast.makeText(Ginasio.this, selectedDateStr + " selected!", Toast.LENGTH_SHORT).show();
                    Log.i("onDateSelected", selectedDateStr + " - Position = " + position);
                }

            });


        } catch (Exception e) {

        }
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
}
