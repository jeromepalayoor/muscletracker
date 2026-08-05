package net.jpalayoor.muscletracker.ui.record;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.LiveSessionExercise;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.ArrayList;
import java.util.List;


public class LiveExerciseAdapter extends RecyclerView.Adapter<LiveExerciseAdapter.LiveExerciseViewHolder> {
    public interface OnTemplateExerciseClickListener {
        void onTemplateClick(TemplateExerciseWithName templateExerciseWithName);
    }

    private List<LiveSessionExercise> templateExerciseWithNames = new ArrayList<>();
    private final LiveExerciseAdapter.OnTemplateExerciseClickListener listener;

    public LiveExerciseAdapter(OnTemplateExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<LiveSessionExercise> newItems) {
        this.templateExerciseWithNames = newItems;
        notifyDataSetChanged();
    }

    public List<LiveSessionExercise> getItems() {
        return this.templateExerciseWithNames;
    }

    @NonNull
    @Override
    public LiveExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_exercise, parent, false);
        return new LiveExerciseViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull LiveExerciseViewHolder holder, int position) {
        LiveSessionExercise templateExerciseWithName = templateExerciseWithNames.get(position);
        holder.liveExerciseName.setText(templateExerciseWithName.name);
        String setCount = templateExerciseWithName.setCount + " Sets";
        holder.liveSetCount.setText(setCount);
    }

    @Override
    public int getItemCount() {
        return templateExerciseWithNames.size();
    }

    public static class LiveExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView liveExerciseName;
        TextView liveSetCount;

        LiveExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            liveExerciseName = itemView.findViewById(R.id.liveExerciseName);
            liveSetCount = itemView.findViewById(R.id.liveSetCount);
        }
    }
}