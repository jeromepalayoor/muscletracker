package net.jpalayoor.muscletracker.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.jpalayoor.muscletracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeViewModel viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView homeDate = view.findViewById(R.id.homeDate);
        TextView sinceLast = view.findViewById(R.id.sinceLast);
        TextView thisWeek = view.findViewById(R.id.thisWeek);
        TextView thisMonth = view.findViewById(R.id.thisMonth);
        TextView suggested = view.findViewById(R.id.suggested);

        viewModel.loadStats();
        viewModel.getDaysSinceLastText().observe(getViewLifecycleOwner(), sinceLast::setText);
        viewModel.getSessionsThisWeek().observe(getViewLifecycleOwner(),
                count -> thisWeek.setText(String.valueOf(count)));
        viewModel.getSessionsThisMonth().observe(getViewLifecycleOwner(),
                count -> thisMonth.setText(String.valueOf(count)));
        viewModel.getSuggested().observe(getViewLifecycleOwner(), suggested::setText);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String date = sdf.format(new Date(System.currentTimeMillis()));
        homeDate.setText(date);
    }
}