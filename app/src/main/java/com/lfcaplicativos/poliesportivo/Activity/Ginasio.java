package com.lfcaplicativos.poliesportivo.Activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lfcaplicativos.poliesportivo.CalendarPager.DaysList;
import com.lfcaplicativos.poliesportivo.CalendarPager.LinearLayoutPagerManager;
import com.lfcaplicativos.poliesportivo.CalendarPager.MyCalendar;
import com.lfcaplicativos.poliesportivo.CalendarPager.adapter.RVDaysWeek;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;

import java.util.ArrayList;
import java.util.Calendar;

public class Ginasio extends AppCompatActivity implements View.OnClickListener {

    private int position = 0;
    private MyCalendar calendar;
    private ArrayList<DaysList> listDays;
    private RVDaysWeek adapterDays;
    private RecyclerView mRecyclerView;
    private TextView txtMonthAndYear;
    private Context context;
    private int countGeneratedDays = 7;

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

            calendario();


            Log.i("Default Date", DateFormat.format("EEE, MMM d, yyyy", startDate).toString());
            Toast.makeText(Ginasio.this, DateFormat.format("EEE, MMM d, yyyy", startDate).toString(), Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.i("Erro: ", e.getMessage());
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

    @SuppressLint("ClickableViewAccessibility")
    private void calendario() {
        this.context = getBaseContext();
        this.calendar = new MyCalendar(this.context);
        this.listDays = this.calendar.getSevenDayAfterCurrentDate();
        String monthYear = this.calendar.getMonthIn7Days(this.listDays.get(0).getFullDate());

        final TextView findNext = (TextView) findViewById(R.id.iconApplyNext);
        final TextView findPrev = (TextView) findViewById(R.id.iconApplyPrev);


        Typeface fontFontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        findNext.setTypeface(fontFontAwesome);
        findPrev.setTypeface(fontFontAwesome);
        ((TextView) findViewById(R.id.txtIconCalendar)).setTypeface(fontFontAwesome);

        txtMonthAndYear = (TextView) findViewById(R.id.txtMonthAndYear);
        txtMonthAndYear.setText(monthYear);

        mRecyclerView = (RecyclerView) findViewById(R.id.cardView);
        this.adapterDays = new RVDaysWeek(this.context, this.listDays);
        this.mRecyclerView.setAdapter(this.adapterDays);
        this.mRecyclerView.setHasFixedSize(true);

        final LinearLayoutPagerManager MyLayoutManager = new LinearLayoutPagerManager(getApplication(), LinearLayoutManager.HORIZONTAL, false, 7);

        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(MyLayoutManager);
        this.adapterDays.notifyDataSetChanged();

        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePos = MyLayoutManager.findFirstVisibleItemPosition();
                updateMontYear(firstVisiblePos);
            }
        });


        findPrev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        findPrev.setBackgroundColor(ContextCompat.getColor(context, R.color.c4));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        findPrev.setBackgroundColor(ContextCompat.getColor(context, R.color.c1));
                        break;
                    }
                }
                return false;
            }
        });

        findPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countGeneratedDays > 7) {
                    countGeneratedDays -= 7;
                    updateMontYear(countGeneratedDays - 7);
                    mRecyclerView.smoothScrollToPosition(countGeneratedDays - 7);
                }
            }
        });

        findNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        findNext.setBackgroundColor(ContextCompat.getColor(context, R.color.c4));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        findNext.setBackgroundColor(ContextCompat.getColor(context, R.color.c1));
                        break;
                    }
                }
                return false;
            }
        });

        findNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countGeneratedDays == listDays.size()) {
                    listDays = calendar.getMoreSevenDays(listDays.get(listDays.size() - 1).getFullDate());
                    adapterDays.update(listDays);

                    mRecyclerView.smoothScrollToPosition(adapterDays.getItemCount() - 1);
                    updateMontYear(listDays.size() - 7);
                    countGeneratedDays = listDays.size();
                } else {
                    countGeneratedDays += 7;
                    updateMontYear(countGeneratedDays - 7);
                    mRecyclerView.smoothScrollToPosition(countGeneratedDays - 1);
                }
            }
        });
    }

    private void updateMontYear(int lengthList) {
        String monthYear = calendar.getMonthIn7Days(listDays.get(lengthList).getFullDate());
        txtMonthAndYear.setText(monthYear.toUpperCase());
    }

}
