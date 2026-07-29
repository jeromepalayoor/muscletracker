package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionDetailViewModel viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);
        SessionDetailAdapter adapter = new SessionDetailAdapter();

        TextView textSessionDetailName = view.findViewById(R.id.textSessionDetailName);
        TextView textSessionDetailDate = view.findViewById(R.id.textSessionDetailDate);
        TextView textSessionDetailDuration = view.findViewById(R.id.textSessionDetailDuration);
        TextView textSessionDetailVolume = view.findViewById(R.id.textSessionDetailVolume);
        TextView textSessionDetailSets = view.findViewById(R.id.textSessionDetailSets);
        TextView textSessionDetailReps = view.findViewById(R.id.textSessionDetailReps);

        int sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        viewModel.loadSession(sessionId);
        viewModel.getSession().observe(getViewLifecycleOwner(), ws -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            String date = sdf.format(new Date(ws.startTime));
            long minutes = ((ws.endTime - ws.startTime) / 1000) / 60;
            String duration = "Duration: ";
            if (minutes >= 60) {
                duration += minutes / 60;
                duration += "h ";
                minutes %= 60;
            }
            duration += minutes + "min";
            textSessionDetailName.setText(ws.templateName);
            textSessionDetailDate.setText(date);
            textSessionDetailDuration.setText(duration);
        });
        viewModel.getSets().observe(getViewLifecycleOwner(), logSets -> {
            adapter.setItems(logSets);
            int reps = viewModel.getTotalReps(logSets);
            float volume = viewModel.getTotalVolume(logSets);
            textSessionDetailReps.setText(getString(R.string.total_reps_format, reps));
            textSessionDetailVolume.setText(getString(R.string.volume_format, volume));
            textSessionDetailSets.setText(getString(R.string.total_sets_format, logSets.size()));
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerDetailLoggedSets);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
}
