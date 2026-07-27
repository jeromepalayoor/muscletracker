package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;

public class LiveSessionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LiveSessionViewModel viewModel = new ViewModelProvider(this).get(LiveSessionViewModel.class);

        int sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Cancel workout?")
                        .setMessage("This workout won't be saved.")
                        .setPositiveButton("Yes, cancel", (dialog, which) -> {
                            viewModel.cancelSession(sessionId);
                            Navigation.findNavController(requireView()).popBackStack(R.id.navigation_record, false);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}