package net.jpalayoor.muscletracker.ui.home;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.CalendarDayViewHolder>{
    public CalendarDayAdapter(OnDayClickListener listener) {
        this.listener = listener;
    }

    public void setCalendarDays(List<CalendarDay> calendarDays) {
        this.calendarDays = calendarDays;
        notifyDataSetChanged();
    }

    public interface OnDayClickListener {
        void onDayClick(CalendarDay calendarDay);
    }

    private List<CalendarDay> calendarDays = new ArrayList<>();
    private final CalendarDayAdapter.OnDayClickListener listener;

    @NonNull
    @Override
    public CalendarDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarDayAdapter.CalendarDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDayViewHolder holder, int position) {
        CalendarDay calendarDay = calendarDays.get(position);

        if (calendarDay.isBlank) {
            holder.textCalendarDay.setText("");
            holder.dotCalendarDay.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        } else if (calendarDay.isHeader){
            holder.textCalendarDay.setText(calendarDay.header);
            holder.dotCalendarDay.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
            holder.textCalendarDay.setTypeface(null, Typeface.BOLD_ITALIC);
        }
        else {
            holder.textCalendarDay.setText(String.valueOf(calendarDay.day));
            boolean hasSession = !calendarDay.sessionIds.isEmpty();
            holder.dotCalendarDay.setVisibility(hasSession ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(hasSession ? v -> listener.onDayClick(calendarDay) : null);
        }

        if (calendarDay.isToday) {
            holder.textCalendarDay.setBackgroundResource(R.drawable.today_ring);
        }
        else {
            holder.textCalendarDay.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    public static class CalendarDayViewHolder extends RecyclerView.ViewHolder {
        TextView textCalendarDay;
        View dotCalendarDay;

        CalendarDayViewHolder(@NonNull View itemView) {
            super(itemView);
            textCalendarDay = itemView.findViewById(R.id.textCalendarDay);
            dotCalendarDay = itemView.findViewById(R.id.dotCalendarDay);
        }
    }
}
