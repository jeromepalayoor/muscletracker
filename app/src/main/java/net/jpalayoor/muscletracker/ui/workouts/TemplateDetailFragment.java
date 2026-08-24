package net.jpalayoor.muscletracker.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.Objects;

public class TemplateDetailFragment extends Fragment {
    private int templateId;
    private String templateName;
    private TemplateExerciseAdapter adapter;
    private TemplateDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TemplateDetailViewModel.class);
        WorkoutsViewModel workoutsViewModel = new ViewModelProvider(this).get(WorkoutsViewModel.class);
        adapter = new TemplateExerciseAdapter(templateExercise -> {
            Bundle args = new Bundle();
            args.putString("exerciseId", templateExercise.exerciseId);
            Navigation.findNavController(view).navigate(R.id.action_template_detail_to_exercise_detail, args);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTemplateExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                adapter.moveItem(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                TemplateExerciseWithName item = adapter.getItems().get(position);
                viewModel.deleteById(item.id);
                adapter.deleteItem(position);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.updateOrder(adapter.getItems());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        adapter.setTouchHelper(touchHelper);

        templateId = getArguments() != null ? getArguments().getInt("templateId") : -1;
        if (templateId != -1) {
            viewModel.getExercisesForTemplate(templateId).observe(getViewLifecycleOwner(), exercises -> {
                adapter.setItems(exercises);
                view.findViewById(R.id.noExercisesText).setVisibility(exercises.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }

        View customTitleView = LayoutInflater.from(requireContext()).inflate(R.layout.actionbar_editable_title, (ViewGroup) view, false);
        TextView titleText = customTitleView.findViewById(R.id.customTitleText);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        activity.getSupportActionBar().setCustomView(customTitleView);

        viewModel.getTemplate(templateId).observe(getViewLifecycleOwner(), template -> {
            if (template != null) {
                templateName = template.name;
                titleText.setText(templateName);
            }
        });

        titleText.setOnClickListener(v -> {
            TextInputLayout inputLayout = (TextInputLayout) LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_add_template, null);
            TextInputEditText editText = inputLayout.findViewById(R.id.editTemplateName);
            editText.setText(templateName);
            editText.setSelection(editText.getText() != null ? editText.getText().length() : 0);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Rename workout")
                    .setView(inputLayout)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = editText.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            templateName = newName;
                            titleText.setText(newName);
                            workoutsViewModel.renameTemplate(templateId, newName);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.template_detail_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_add_exercise) {
                    Bundle args = new Bundle();
                    args.putInt("templateId", templateId);
                    args.putString("templateName", templateName);
                    Navigation.findNavController(view).navigate(R.id.action_template_detail_to_exercise_picker, args);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.action_delete_template) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete " + templateName + " workout?")
                            .setMessage("This cannot be undone.")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                workoutsViewModel.deleteTemplate(templateId);
                                Navigation.findNavController(view).popBackStack();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowCustomEnabled(false);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }
}
