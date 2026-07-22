package net.jpalayoor.muscletracker.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    private List<Exercise> exercises = new ArrayList<>();
    private final OnExerciseClickListener listener;

    public ExerciseAdapter(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setExercises(List<Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.textName.setText(exercise.name);
        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textExerciseName);
        }
    }
}