package net.jpalayoor.muscletracker.util;

import com.garmin.fit.DateTime;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.SetMesg;
import com.garmin.fit.SetType;

public class FitSetHelper {

    public static void writeSet(FileEncoder encoder, long timestampMillis, String trackingType,
                                float weight, int reps, Integer durationSeconds,
                                Integer fitCategoryValue, Integer fitSubtypeValue) {
        SetMesg setMesg = new SetMesg();
        setMesg.setTimestamp(new DateTime(new java.util.Date(timestampMillis)));
        setMesg.setSetType(SetType.ACTIVE);

        if (fitCategoryValue != null) {
            setMesg.setCategory(0, fitCategoryValue);
            if (fitSubtypeValue != null) {
                setMesg.setCategorySubtype(0, fitSubtypeValue);
            }
        }

        if ("time".equals(trackingType)) {
            setMesg.setDuration(durationSeconds != null ? (float) durationSeconds : 0f);
        } else if ("reps".equals(trackingType)) {
            setMesg.setRepetitions(reps);
        } else {
            setMesg.setWeight(weight);
            setMesg.setRepetitions(reps);
        }

        try {
            encoder.write(setMesg);
        } catch (Exception ignored) {}
    }
}