package net.jpalayoor.muscletracker.ui.record;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.ArrayList;
import java.util.List;


public class LiveExerciseAdapter extends RecyclerView.Adapter<LiveExerciseAdapter.TemplateExerciseViewHolder> {
    public interface OnTemplateExerciseClickListener {
        void onTemplateClick(TemplateExerciseWithName templateExerciseWithName);
    }

    private List<TemplateExerciseWithName> templateExerciseWithNames = new ArrayList<>();
    private final LiveExerciseAdapter.OnTemplateExerciseClickListener listener;

    public LiveExerciseAdapter(OnTemplateExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<TemplateExerciseWithName> newItems) {
        this.templateExerciseWithNames = newItems;
        notifyDataSetChanged();
    }

    public List<TemplateExerciseWithName> getItems() {
        return this.templateExerciseWithNames;
    }

    @NonNull
    @Override
    public TemplateExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_exercise, parent, false);
        return new TemplateExerciseViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TemplateExerciseViewHolder holder, int position) {
        TemplateExerciseWithName templateExerciseWithName = templateExerciseWithNames.get(position);
        holder.liveExerciseName.setText(templateExerciseWithName.name);
    }

    @Override
    public int getItemCount() {
        return templateExerciseWithNames.size();
    }

    public static class TemplateExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView liveExerciseName;
        TextView liveSetCount;

        TemplateExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            liveExerciseName = itemView.findViewById(R.id.liveExerciseName);
            liveSetCount = itemView.findViewById(R.id.liveSetCount);
        }
    }
}