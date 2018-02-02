package com.lfcaplicativos.poliesportivo.CalendarPager.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lfcaplicativos.poliesportivo.CalendarPager.AnimationUtils;
import com.lfcaplicativos.poliesportivo.CalendarPager.DaysList;
import com.lfcaplicativos.poliesportivo.CalendarPager.viewholders.VHDaysWeek;
import com.lfcaplicativos.poliesportivo.R;

import java.util.List;

/**
 * Created by adao on 8/10/17.
 */

public class RVDaysWeek extends RecyclerView.Adapter<VHDaysWeek> {

    Context mctx;
    private boolean isLoaded = false;
    private int activeCell = -1;
    private List<DaysList> mList;
    private VHDaysWeek h;
    private int previousPosition = 0;
    private AnimationUtils AnimationUtil;
    private final static int ITEMS_PER_PAGE = 7;

    public RVDaysWeek(Context ctx, List<DaysList> list) {
        this.mctx = ctx;
        this.mList = list;
        //AnimationUtil = new
    }

    @Override
    public VHDaysWeek onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int itemWidth = viewGroup.getWidth() / ITEMS_PER_PAGE;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.days_list, viewGroup, false);

        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        itemView.setLayoutParams(layoutParams);
        return new VHDaysWeek(itemView, this.mctx);
    }

    @Override
    public void onBindViewHolder(final VHDaysWeek holder, int position) {
        DaysList days = mList.get(position);
        holder.dayWeek.setText(days.getDay());
        holder.fullDayWeek.setText(days.getDayFull());
        //Log.d("DUQUE-POSITION", "Position: " + position);

        if (position == activeCell) {
            //Log.d("DUQUE-POSITION", "ACTIVE CELL: " + activeCell);
            holder.dayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c3));
            holder.fullDayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c5));
            holder.card_view.setBackgroundResource(R.drawable.corners);
            h = holder;
        } else if (!isLoaded) {
            activeCell = 0;
            isLoaded = true;
            holder.dayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c3));
            holder.fullDayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c5));
            holder.card_view.setBackgroundResource(R.drawable.corners);
            h = holder;
        } else if (position >= (activeCell + 7) || position <= (activeCell - 7)) {
            activeCell = position;
            holder.dayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c3));
            holder.fullDayWeek.setTextColor(ContextCompat.getColor(this.mctx, R.color.c5));
            holder.card_view.setBackgroundResource(R.drawable.corners);

            h.card_view.setBackgroundResource(0);
            h.dayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));
            h.fullDayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));

            h = holder;

        } else {
            holder.card_view.setBackgroundResource(0);
            holder.dayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));
            holder.fullDayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));
        }

        if (position > previousPosition) { // We are scrolling DOWN

            AnimationUtil.animate(holder, true);

        } else { // We are scrolling UP

            AnimationUtil.animate(holder, false);


        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.card_view.setBackgroundResource(0);
                h.dayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));
                h.fullDayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c2));

                holder.dayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c3));
                holder.fullDayWeek.setTextColor(ContextCompat.getColor(mctx, R.color.c5));
                holder.card_view.setBackgroundResource(R.drawable.corners);
                activeCell = holder.getLayoutPosition();

                h = holder;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void update(List<DaysList> data) {
        if (data != null && data.size() > 0) {
            //Log.d("DUQUE-POSITION", "ACTIVE: " + activeCell);
            mList = data;
            notifyDataSetChanged();
        }
    }
}
