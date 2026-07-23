package net.jpalayoor.muscletracker.ui.workouts;

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


public class TemplateExerciseAdapter extends RecyclerView.Adapter<TemplateExerciseAdapter.TemplateExerciseViewHolder> {
    public interface OnTemplateExerciseClickListener {
        void onTemplateClick(TemplateExerciseWithName templateExerciseWithName);
    }

    private List<TemplateExerciseWithName> templateExerciseWithNames = new ArrayList<>();
    private final TemplateExerciseAdapter.OnTemplateExerciseClickListener listener;
    private ItemTouchHelper touchHelper;

    public TemplateExerciseAdapter(OnTemplateExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    public void setItems(List<TemplateExerciseWithName> newItems) {
        this.templateExerciseWithNames = newItems;
        notifyDataSetChanged();
    }

    public List<TemplateExerciseWithName> getItems() {
        return this.templateExerciseWithNames;
    }

    public void moveItem(int from, int to) {
        TemplateExerciseWithName item = this.templateExerciseWithNames.remove(from);
        this.templateExerciseWithNames.add(to, item);
        notifyItemMoved(from, to);
    }

    public void deleteItem(int position) {
        this.templateExerciseWithNames.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public TemplateExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template_exercise, parent, false);
        return new TemplateExerciseViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TemplateExerciseViewHolder holder, int position) {
        TemplateExerciseWithName templateExerciseWithName = templateExerciseWithNames.get(position);
        holder.textName.setText(templateExerciseWithName.name);
        holder.itemView.setOnClickListener(v -> listener.onTemplateClick(templateExerciseWithName));
        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder);
            }
            return false;
        });
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