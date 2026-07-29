package net.jpalayoor.muscletracker.ui.record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLog;

import java.util.ArrayList;
import java.util.List;

public class LoggedSetAdapter extends RecyclerView.Adapter<LoggedSetAdapter.LoggedSetViewHolder>{
    private List<SetLog> setLogs = new ArrayList<>();

    @NonNull
    @Override
    public LoggedSetAdapter.LoggedSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_logged_set, parent, false);
        return new LoggedSetAdapter.LoggedSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoggedSetAdapter.LoggedSetViewHolder holder, int position) {
        SetLog setLog = setLogs.get(position);
        holder.textSetNumber.setText(holder.itemView.getContext().getString(R.string.set_number_format, setLog.setNumber + 1));
        holder.textSetWeight.setText(holder.itemView.getContext().getString(R.string.weight_kg_format, setLog.weight));
        holder.textSetReps.setText(holder.itemView.getContext().getString(R.string.reps_format, setLog.reps));
    }

    @Override
    public int getItemCount() {
        return setLogs.size();
    }

    public void setItems(List<SetLog> newSetLogs) {
        this.setLogs = newSetLogs;
        notifyDataSetChanged();
    }

    public static class LoggedSetViewHolder extends RecyclerView.ViewHolder {
        TextView textSetNumber;
        TextView textSetWeight;
        TextView textSetReps;

        LoggedSetViewHolder(@NonNull View itemView) {
            super(itemView);
            textSetNumber = itemView.findViewById(R.id.textSetNumber);
            textSetWeight = itemView.findViewById(R.id.textSetWeight);
            textSetReps = itemView.findViewById(R.id.textSetReps);
        }
    }
}
