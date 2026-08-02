package net.jpalayoor.muscletracker.ui.home;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.CalendarDay;

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

        LinearLayout calendarGrid = view.findViewById(R.id.calendarGrid);

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
            calendarGrid.removeAllViews();
            List<CalendarDay> days = viewModel.buildCalendarDays(year, month, sessionsByDay);

            LinearLayout currentRow = null;
            for (int j = 0; j < days.size(); j++) {
                if (j % 7 == 0) {
                    currentRow = new LinearLayout(requireContext());
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    calendarGrid.addView(currentRow);
                }

                CalendarDay day = days.get(j);
                View cell = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_day, currentRow, false);                TextView textCalendarDay = cell.findViewById(R.id.textCalendarDay);
                View dotCalendarDay = cell.findViewById(R.id.dotCalendarDay);

                if (day.isBlank) {
                    textCalendarDay.setText("");
                    dotCalendarDay.setVisibility(View.GONE);
                    cell.setOnClickListener(null);
                } else if (day.isHeader) {
                    textCalendarDay.setText(day.header);
                    dotCalendarDay.setVisibility(View.GONE);
                    cell.setOnClickListener(null);
                    textCalendarDay.setTypeface(null, Typeface.BOLD_ITALIC);
                    textCalendarDay.setTextColor(resolveThemeColor(requireContext(), android.R.attr.textColorSecondary));
                } else {
                    textCalendarDay.setText(String.valueOf(day.day));
                    boolean hasSession = !day.sessionIds.isEmpty();
                    dotCalendarDay.setVisibility(hasSession ? View.VISIBLE : View.GONE);
                    cell.setOnClickListener(hasSession ? v -> {
                        if (!day.sessionIds.isEmpty()) {
                            if (day.sessionIds.size() == 1) {
                                Bundle args = new Bundle();
                                args.putInt("sessionId", day.sessionIds.get(0));
                                Navigation.findNavController(view).navigate(R.id.action_home_to_session, args);
                            }
                            else {
                                List<String> sessionOptions = new ArrayList<>();
                                for (int i = 0; i < day.sessionIds.size(); i++) {
                                    sessionOptions.add(viewModel.getSessionById(day.sessionIds.get(i)));
                                }
                                CharSequence[] options = sessionOptions.toArray(new CharSequence[0]);
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Sessions recorded:")
                                        .setItems(options, (dialog, which) -> {
                                            int selectedSessionId = day.sessionIds.get(which);
                                            Bundle args = new Bundle();
                                            args.putInt("sessionId", selectedSessionId);
                                            Navigation.findNavController(view).navigate(R.id.action_home_to_session, args);
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        }
                    } : null);
                    textCalendarDay.setTypeface(null, Typeface.NORMAL);
                    textCalendarDay.setTextColor(resolveThemeColor(requireContext(), android.R.attr.textColorPrimary));
                }

                if (day.isToday) {
                    textCalendarDay.setBackgroundResource(R.drawable.today_ring);
                } else {
                    textCalendarDay.setBackgroundResource(0);
                }

                currentRow.addView(cell);
            }
        });
    }

    private int resolveThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        if (typedValue.resourceId != 0) {
            return ContextCompat.getColor(context, typedValue.resourceId);
        }
        return typedValue.data;
    }
}