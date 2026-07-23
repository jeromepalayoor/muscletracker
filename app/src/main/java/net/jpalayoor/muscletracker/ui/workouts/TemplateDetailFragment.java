package net.jpalayoor.muscletracker.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.Objects;

public class TemplateDetailFragment extends Fragment {
    private int templateId;
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
        adapter = new TemplateExerciseAdapter(templateExercise -> {
            // click handling — later wave, reusing ExerciseDetailFragment
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
        });
        touchHelper.attachToRecyclerView(recyclerView);

        templateId = getArguments() != null ? getArguments().getInt("templateId") : -1;
        if (templateId != -1) {
            viewModel.getExercisesForTemplate(templateId).observe(getViewLifecycleOwner(), adapter::setItems);
        }

        String templateName = getArguments() != null ? getArguments().getString("templateName") : "";
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(templateName);

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
                return false;
            }
        }, getViewLifecycleOwner());
    }
}
