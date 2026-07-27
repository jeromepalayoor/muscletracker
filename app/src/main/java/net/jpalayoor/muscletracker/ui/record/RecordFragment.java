package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

public class RecordFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecordViewModel viewModel = new ViewModelProvider(this).get(RecordViewModel.class);

        SessionAdapter adapter = new SessionAdapter(session -> {
            Bundle args = new Bundle();
            args.putInt("sessionId", session.id);
            Navigation.findNavController(view).navigate(R.id.action_record_to_session, args);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllSessions().observe(getViewLifecycleOwner(), adapter::setSessions);

        Button btnStartWorkout = view.findViewById(R.id.btnStartWorkout);
        btnStartWorkout.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_record_to_start_workout);
        });
    }
}