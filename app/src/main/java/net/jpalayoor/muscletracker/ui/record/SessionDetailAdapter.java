package net.jpalayoor.muscletracker.ui.record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLogWithName;

import java.util.ArrayList;
import java.util.List;

public class SessionDetailAdapter extends RecyclerView.Adapter<SessionDetailAdapter.SessionDetailViewHolder>{
    private List<SetLogWithName> setLogs = new ArrayList<>();

    @NonNull
    @Override
    public SessionDetailAdapter.SessionDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_set, parent, false);
        return new SessionDetailAdapter.SessionDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionDetailAdapter.SessionDetailViewHolder holder, int position) {
        SetLogWithName setLog = setLogs.get(position);
        holder.textDetailSetExercise.setText(setLog.name);
        holder.textDetailSetNumber.setText(holder.itemView.getContext().getString(R.string.set_number_format, setLog.setNumber + 1));
        holder.textDetailSetWeight.setText(holder.itemView.getContext().getString(R.string.weight_kg_format, setLog.weight));
        holder.textDetailSetReps.setText(holder.itemView.getContext().getString(R.string.reps_format, setLog.reps));
    }

    @Override
    public int getItemCount() {
        return setLogs.size();
    }

    public void setItems(List<SetLogWithName> newSetLogs) {
        this.setLogs = newSetLogs;
        notifyDataSetChanged();
    }

    public static class SessionDetailViewHolder extends RecyclerView.ViewHolder {
        TextView textDetailSetExercise;
        TextView textDetailSetNumber;
        TextView textDetailSetWeight;
        TextView textDetailSetReps;

        SessionDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textDetailSetExercise = itemView.findViewById(R.id.textDetailSetExercise);
            textDetailSetNumber = itemView.findViewById(R.id.textDetailSetNumber);
            textDetailSetWeight = itemView.findViewById(R.id.textDetailSetWeight);
            textDetailSetReps = itemView.findViewById(R.id.textDetailSetReps);
        }
    }
}
