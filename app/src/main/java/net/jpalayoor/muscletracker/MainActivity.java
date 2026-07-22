package net.jpalayoor.muscletracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.Exercise;
import net.jpalayoor.muscletracker.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        binding.navView.setOnApplyWindowInsetsListener(null);
        binding.navView.setPadding(0, 0, 0, 0);

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {
            if (db.exerciseDao().getAll().isEmpty()) {
                try {
                    List<Exercise> exercises = loadExercisesFromAssets(this);
                    db.exerciseDao().insertAll(exercises);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error importing exercises", e);
                }
            }
        }).start();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_workouts, R.id.navigation_record,
                R.id.navigation_exercises, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_exercises) {
                navController.popBackStack(R.id.navigation_exercises, false);
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_exercise_detail) {
                binding.navView.getMenu().findItem(R.id.navigation_exercises).setChecked(true);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private List<Exercise> loadExercisesFromAssets(Context context) throws IOException, JSONException {
        InputStream is = context.getAssets().open("exercises.json");
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            byteStream.write(buffer, 0, length);
        }
        is.close();
        String json = byteStream.toString("UTF-8");

        JSONArray jsonArray = new JSONArray(json);
        List<Exercise> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            Exercise ex = new Exercise();
            ex.exerciseId = obj.getString("exerciseId");
            ex.name = obj.getString("name");
            ex.muscleGroupsPrimary = joinArray(obj.getJSONArray("muscleGroupsPrimary"));
            ex.muscleGroupsSecondary = joinArray(obj.getJSONArray("muscleGroupsSecondary"));
            ex.specificMusclesPrimary = joinArray(obj.getJSONArray("specificMusclesPrimary"));
            ex.specificMusclesSecondary = joinArray(obj.getJSONArray("specificMusclesSecondary"));
            ex.anatomicalRegionsPrimary = joinArray(obj.getJSONArray("anatomicalRegionsPrimary"));
            ex.anatomicalRegionsSecondary = joinArray(obj.getJSONArray("anatomicalRegionsSecondary"));
            ex.splitCategory = safeString(obj, "splitCategory");
            ex.upperLower = safeString(obj, "upperLower");
            ex.force = safeString(obj, "force");
            ex.mechanic = safeString(obj, "mechanic");
            ex.equipment = safeString(obj, "equipment");
            ex.level = safeString(obj, "level");
            ex.exerciseType = safeString(obj, "exerciseType");
            ex.instructions = joinArray(obj.getJSONArray("instructions"));

            JSONArray images = obj.getJSONArray("images");
            ex.image1 = images.length() > 0 ? images.getString(0) : null;
            ex.image2 = images.length() > 1 ? images.getString(1) : null;

            result.add(ex);
        }
        return result;
    }

    private String joinArray(JSONArray arr) throws JSONException {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            items.add(arr.getString(i));
        }
        return String.join("|", items);
    }

    private String safeString(JSONObject obj, String key) throws JSONException {
        return obj.isNull(key) ? "" : obj.getString(key);
    }
}