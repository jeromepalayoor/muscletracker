package net.jpalayoor.muscletracker.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.Exercise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExercisePickerAdapter extends RecyclerView.Adapter<ExercisePickerAdapter.PickerViewHolder> {

    private List<Exercise> exercises = new ArrayList<>();
    private final Set<String> selectedIds = new HashSet<>();

    public void setExercises(List<Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged();
    }

    public Set<String> getSelectedIds() {
        return selectedIds;
    }

    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_picker, parent, false);
        return new PickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickerViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.textName.setText(exercise.name);

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(selectedIds.contains(exercise.exerciseId));

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(exercise.exerciseId);
            } else {
                selectedIds.remove(exercise.exerciseId);
            }
        });

        holder.itemView.setOnClickListener(v -> holder.checkbox.setChecked(!holder.checkbox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class PickerViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        CheckBox checkbox;

        PickerViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textPickerExerciseName);
            checkbox = itemView.findViewById(R.id.checkboxSelected);
        }
    }
}