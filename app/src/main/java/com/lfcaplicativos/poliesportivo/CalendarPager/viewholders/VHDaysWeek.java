package com.lfcaplicativos.poliesportivo.CalendarPager.viewholders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lfcaplicativos.poliesportivo.R;

/**
 * Created by adao on 8/10/17.
 */

public class VHDaysWeek extends RecyclerView.ViewHolder {
    public TextView dayWeek, fullDayWeek;
    public CardView card_view;
    private Context context;

    public VHDaysWeek(final View itemView, Context context) {
        super(itemView);
        dayWeek = itemView.findViewById(R.id.dayWeek);
        fullDayWeek = itemView.findViewById(R.id.fullWeek);
        card_view = itemView.findViewById(R.id.card_view);
        this.context = context;
    }
}
