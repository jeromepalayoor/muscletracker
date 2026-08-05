package net.jpalayoor.muscletracker.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.WorkoutTemplateWithCount;

import java.util.ArrayList;
import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {
    public interface OnTemplateClickListener {
        void onTemplateClick(WorkoutTemplateWithCount workoutTemplate);
    }

    private List<WorkoutTemplateWithCount> templates = new ArrayList<>();
    private final TemplateAdapter.OnTemplateClickListener listener;

    public TemplateAdapter(TemplateAdapter.OnTemplateClickListener listener) {
        this.listener = listener;
    }

    public void setTemplates(List<WorkoutTemplateWithCount> newTemplates) {
        this.templates = newTemplates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        WorkoutTemplateWithCount workoutTemplate = templates.get(position);
        holder.textName.setText(workoutTemplate.name);
        holder.itemView.setOnClickListener(v -> listener.onTemplateClick(workoutTemplate));
        String count;
        if (workoutTemplate.count == 1) {
            count = "1 Exercise";
        }
        else {
            count = workoutTemplate.count + " Exercises";
        }
        holder.textTemplateCount.setText(count);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textTemplateCount;

        TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textTemplateName);
            textTemplateCount = itemView.findViewById(R.id.textTemplateCount);
        }
    }
}
