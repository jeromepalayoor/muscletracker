package net.jpalayoor.muscletracker.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.ArrayList;
import java.util.List;


public class TemplateExerciseAdapter extends RecyclerView.Adapter<TemplateExerciseAdapter.TemplateExerciseViewHolder> {
    public interface OnTemplateExerciseClickListener {
        void onTemplateClick(TemplateExerciseWithName templateExerciseWithName);
    }

    private List<TemplateExerciseWithName> templateExerciseWithNames = new ArrayList<>();
    private final TemplateExerciseAdapter.OnTemplateExerciseClickListener listener;

    public TemplateExerciseAdapter(OnTemplateExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<TemplateExerciseWithName> newItems) {
        this.templateExerciseWithNames = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TemplateExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template_exercise, parent, false);
        return new TemplateExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateExerciseViewHolder holder, int position) {
        // TODO 5: get the item at this position, set the TextView's text to its name,
        //         set the itemView's click listener to call listener.onTemplateClick(...)
        TemplateExerciseWithName templateExerciseWithName = templateExerciseWithNames.get(position);
        holder.textName.setText(templateExerciseWithName.name);
        holder.itemView.setOnClickListener(v -> listener.onTemplateClick(templateExerciseWithName));
    }

    @Override
    public int getItemCount() {
        return templateExerciseWithNames.size();
    }

    public static class TemplateExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView dragHandle;

        TemplateExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textTemplateExerciseName);
            dragHandle = itemView.findViewById(R.id.dragHandle);
        }
    }
}