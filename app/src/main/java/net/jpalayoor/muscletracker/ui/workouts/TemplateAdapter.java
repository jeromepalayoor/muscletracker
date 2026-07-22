package net.jpalayoor.muscletracker.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;

import java.util.ArrayList;
import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {
    public interface OnTemplateClickListener {
        void onTemplateClick(WorkoutTemplate workoutTemplate);
    }

    private List<WorkoutTemplate> templates = new ArrayList<>();
    private final TemplateAdapter.OnTemplateClickListener listener;

    public TemplateAdapter(TemplateAdapter.OnTemplateClickListener listener) {
        this.listener = listener;
    }

    public void setTemplates(List<WorkoutTemplate> newTemplates) {
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
        WorkoutTemplate workoutTemplate = templates.get(position);
        holder.textName.setText(workoutTemplate.name);
        holder.itemView.setOnClickListener(v -> listener.onTemplateClick(workoutTemplate));
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textTemplateName);
        }
    }
}
