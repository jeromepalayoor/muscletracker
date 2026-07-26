package net.jpalayoor.muscletracker.ui.record;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.WorkoutSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder>{
    public interface OnSessionClickListener {
        void onSessionClick(WorkoutSession workoutSession);
    }

    private List<WorkoutSession> sessions = new ArrayList<>();
    private final SessionAdapter.OnSessionClickListener listener;

    public SessionAdapter(SessionAdapter.OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<WorkoutSession> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionAdapter.SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionAdapter.SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionAdapter.SessionViewHolder holder, int position) {
        WorkoutSession session = sessions.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(session.startTime));
        holder.date.setText(formattedDate);
        holder.name.setText(session.templateName);
        holder.volume.setText("-");
        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView volume;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textSessionTemplateName);
            date = itemView.findViewById(R.id.textSessionDate);
            volume = itemView.findViewById(R.id.textSessionVolume);
        }
    }
}