package net.jpalayoor.muscletracker.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PastSetAdapter extends RecyclerView.Adapter<PastSetAdapter.PastSetViewHolder>{
    private List<SetLog> setLogs = new ArrayList<>();

    @NonNull
    @Override
    public PastSetAdapter.PastSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_set, parent, false);
        return new PastSetAdapter.PastSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastSetAdapter.PastSetViewHolder holder, int position) {
        SetLog setLog = setLogs.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String date = dateFormat.format(new Date(setLog.timestamp));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeFormat.format(new Date(setLog.timestamp));
        holder.textDetailSetDate.setText(date);
        holder.textDetailSetTime.setText(time);
        holder.textDetailSetWeight.setText(holder.itemView.getContext().getString(R.string.weight_kg_format, setLog.weight));
        holder.textDetailSetReps.setText(holder.itemView.getContext().getString(R.string.reps_format, setLog.reps));
    }

    @Override
    public int getItemCount() {
        return setLogs.size();
    }

    public void setItems(List<SetLog> newSetLogs) {
        this.setLogs = newSetLogs;
        notifyDataSetChanged();
    }

    public static class PastSetViewHolder extends RecyclerView.ViewHolder {
        TextView textDetailSetDate;
        TextView textDetailSetTime;
        TextView textDetailSetWeight;
        TextView textDetailSetReps;

        PastSetViewHolder(@NonNull View itemView) {
            super(itemView);
            textDetailSetDate = itemView.findViewById(R.id.textDetailSetDate);
            textDetailSetTime = itemView.findViewById(R.id.textDetailSetTime);
            textDetailSetWeight = itemView.findViewById(R.id.textDetailSetWeight);
            textDetailSetReps = itemView.findViewById(R.id.textDetailSetReps);
        }
    }
}
