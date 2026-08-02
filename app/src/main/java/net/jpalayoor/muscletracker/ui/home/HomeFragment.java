package net.jpalayoor.muscletracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

        CalendarDayAdapter adapter = new CalendarDayAdapter(calendarDay -> {
            if (!calendarDay.sessionIds.isEmpty()) {
                if (calendarDay.sessionIds.size() == 1) {
                    Bundle args = new Bundle();
                    args.putInt("sessionId", calendarDay.sessionIds.get(0));
                    Navigation.findNavController(view).navigate(R.id.action_home_to_session, args);
                }
                else {
                    List<String> sessionOptions = new ArrayList<>();
                    for (int i = 0; i < calendarDay.sessionIds.size(); i++) {
                        sessionOptions.add(viewModel.getSessionById(calendarDay.sessionIds.get(i)));
                    }
                    CharSequence[] options = sessionOptions.toArray(new CharSequence[0]);
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Sessions recorded:")
                            .setItems(options, (dialog, which) -> {
                                int selectedSessionId = calendarDay.sessionIds.get(which);
                                Bundle args = new Bundle();
                                args.putInt("sessionId", selectedSessionId);
                                Navigation.findNavController(view).navigate(R.id.action_home_to_session, args);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCalendar);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        TextView homeDate = view.findViewById(R.id.homeDate);
        TextView sinceLast = view.findViewById(R.id.sinceLast);
        TextView thisWeek = view.findViewById(R.id.thisWeek);
        TextView thisMonth = view.findViewById(R.id.thisMonth);
        TextView suggested = view.findViewById(R.id.suggested);
        TextView currentMonth = view.findViewById(R.id.currentMonth);

        viewModel.loadStats();
        viewModel.getDaysSinceLastText().observe(getViewLifecycleOwner(), sinceLast::setText);
        viewModel.getSessionsThisWeek().observe(getViewLifecycleOwner(),
                count -> thisWeek.setText(String.valueOf(count)));
        viewModel.getSessionsThisMonth().observe(getViewLifecycleOwner(),
                count -> thisMonth.setText(String.valueOf(count)));
        viewModel.getSuggested().observe(getViewLifecycleOwner(), suggested::setText);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM d, yyyy", Locale.getDefault());
        String date = sdf.format(new Date(System.currentTimeMillis()));
        homeDate.setText(date);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        currentMonth.setText(monthFormat.format(new Date(System.currentTimeMillis())));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        viewModel.groupSessionsByDay(year, month);
        viewModel.getSessionsByDay().observe(getViewLifecycleOwner(), sessionsByDay -> {
            adapter.setCalendarDays(viewModel.buildCalendarDays(year, month, sessionsByDay));
        });
    }
}