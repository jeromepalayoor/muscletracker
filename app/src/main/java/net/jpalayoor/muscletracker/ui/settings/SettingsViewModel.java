package net.jpalayoor.muscletracker.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.SetLog;
import net.jpalayoor.muscletracker.data.TemplateExercise;
import net.jpalayoor.muscletracker.data.WorkoutSession;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> exportCsv = new MutableLiveData<>();
    private final MutableLiveData<String> importResult = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<String> getExportCsv() {
        return exportCsv;
    }

    public LiveData<String> getImportResult() {
        return importResult;
    }

    public void resetEverything() {
        executor.execute(() -> {
            db.setLogDao().deleteAll();
            db.workoutSessionDao().deleteAll();
            db.templateExerciseDao().deleteAll();
            db.workoutTemplateDao().deleteAll();
        });
    }

    public void resetLogsOnly() {
        executor.execute(() -> {
            db.setLogDao().deleteAll();
            db.workoutSessionDao().deleteAll();
        });
    }

    private String escapeCsv(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    public void buildExportCsv() {
        executor.execute(() -> {
            List<WorkoutTemplate> templates = db.workoutTemplateDao().getAll();
            List<TemplateExercise> templateExercises = db.templateExerciseDao().getAll();
            List<WorkoutSession> sessions = db.workoutSessionDao().getAll();
            List<SetLog> logs = db.setLogDao().getAll();

            StringBuilder sb = new StringBuilder();

            sb.append("#TEMPLATES\n").append("id,name\n");
            for (WorkoutTemplate t : templates) {
                sb.append(t.id).append(",").append(escapeCsv(t.name)).append("\n");
            }

            sb.append("\n#TEMPLATE_EXERCISES\n").append("id,templateId,exerciseId,exerciseOrder\n");
            for (TemplateExercise te : templateExercises) {
                sb.append(te.id).append(",").append(te.templateId).append(",")
                        .append(te.exerciseId).append(",").append(te.exerciseOrder).append("\n");
            }

            sb.append("\n#SESSIONS\n").append("id,templateId,templateName,startTime,endTime\n");
            for (WorkoutSession s : sessions) {
                sb.append(s.id).append(",").append(s.templateId).append(",")
                        .append(escapeCsv(s.templateName)).append(",").append(s.startTime).append(",")
                        .append(s.endTime != null ? s.endTime : "").append("\n");
            }

            sb.append("\n#LOGS\n").append("id,sessionId,exerciseId,weight,reps,setNumber,timestamp\n");
            for (SetLog l : logs) {
                sb.append(l.id).append(",").append(l.sessionId).append(",")
                        .append(l.exerciseId).append(",").append(l.weight).append(",")
                        .append(l.reps).append(",").append(l.setNumber).append(",")
                        .append(l.timestamp).append("\n");
            }

            exportCsv.postValue(sb.toString());
        });
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (insideQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        insideQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    insideQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields;
    }

    public void importCsv(String content) {
        executor.execute(() -> {
            try {
                Map<Integer, Integer> templateIdMap = new HashMap<>();
                Map<Integer, Integer> sessionIdMap = new HashMap<>();

                String currentSection = null;
                boolean expectHeader = false;

                for (String rawLine : content.split("\n")) {
                    String line = rawLine.replace("\r", "");
                    if (line.isEmpty()) continue;

                    if (line.startsWith("#")) {
                        currentSection = line.substring(1).trim();
                        expectHeader = true;
                        continue;
                    }
                    if (expectHeader) {
                        expectHeader = false;
                        continue;
                    }
                    if (currentSection == null) continue;

                    List<String> fields = parseCsvLine(line);

                    switch (currentSection) {
                        case "TEMPLATES": {
                            int oldId = Integer.parseInt(fields.get(0));
                            WorkoutTemplate t = new WorkoutTemplate();
                            t.name = fields.get(1);
                            long newId = db.workoutTemplateDao().insert(t);
                            templateIdMap.put(oldId, (int) newId);
                            break;
                        }
                        case "TEMPLATE_EXERCISES": {
                            Integer newTemplateId = templateIdMap.get(Integer.parseInt(fields.get(1)));
                            if (newTemplateId == null) continue;
                            TemplateExercise te = new TemplateExercise();
                            te.templateId = newTemplateId;
                            te.exerciseId = fields.get(2);
                            te.exerciseOrder = Integer.parseInt(fields.get(3));
                            db.templateExerciseDao().insert(te);
                            break;
                        }
                        case "SESSIONS": {
                            int oldId = Integer.parseInt(fields.get(0));
                            Integer newTemplateId = templateIdMap.get(Integer.parseInt(fields.get(1)));
                            WorkoutSession s = new WorkoutSession();
                            s.templateId = newTemplateId != null ? newTemplateId : 0;
                            s.templateName = fields.get(2);
                            s.startTime = Long.parseLong(fields.get(3));
                            String endTimeStr = fields.get(4);
                            s.endTime = (endTimeStr.isEmpty() || endTimeStr.equals("null"))
                                    ? null : Long.parseLong(endTimeStr);
                            long newId = db.workoutSessionDao().insert(s);
                            sessionIdMap.put(oldId, (int) newId);
                            break;
                        }
                        case "LOGS": {
                            Integer newSessionId = sessionIdMap.get(Integer.parseInt(fields.get(1)));
                            if (newSessionId == null) continue;
                            SetLog log = new SetLog();
                            log.sessionId = newSessionId;
                            log.exerciseId = fields.get(2);
                            log.weight = Float.parseFloat(fields.get(3));
                            log.reps = Integer.parseInt(fields.get(4));
                            log.setNumber = Integer.parseInt(fields.get(5));
                            log.timestamp = Long.parseLong(fields.get(6));
                            db.setLogDao().insert(log);
                            break;
                        }
                    }
                }

                importResult.postValue("Backup loaded successfully");
            } catch (Exception e) {
                importResult.postValue("Failed to load backup — file format not recognized");
            }
        });
    }
}