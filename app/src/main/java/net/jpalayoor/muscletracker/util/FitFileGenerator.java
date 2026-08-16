package net.jpalayoor.muscletracker.util;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.DateTime;
import com.garmin.fit.Event;
import com.garmin.fit.EventMesg;
import com.garmin.fit.EventType;
import com.garmin.fit.File;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.Fit;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SubSport;

import net.jpalayoor.muscletracker.data.SetLogWithName;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FitFileGenerator {

    public static void generate(java.io.File outputFile, long startTimeMillis, long endTimeMillis,
                                List<SetLogWithName> sets) throws IOException {
        FileEncoder encoder = new FileEncoder(outputFile, Fit.ProtocolVersion.V2_0);

        DateTime startTime = new DateTime(new Date(startTimeMillis));
        DateTime endTime = new DateTime(new Date(endTimeMillis));
        float elapsedSeconds = (endTimeMillis - startTimeMillis) / 1000f;

        FileIdMesg fileIdMesg = new FileIdMesg();
        fileIdMesg.setType(File.ACTIVITY);
        fileIdMesg.setManufacturer(Manufacturer.DEVELOPMENT);
        fileIdMesg.setProduct(1);
        fileIdMesg.setSerialNumber(1L);
        fileIdMesg.setTimeCreated(startTime);
        encoder.write(fileIdMesg);

        EventMesg startEvent = new EventMesg();
        startEvent.setTimestamp(startTime);
        startEvent.setEvent(Event.TIMER);
        startEvent.setEventType(EventType.START);
        encoder.write(startEvent);

        for (int i = 0; i <= (int) elapsedSeconds; i += 30) {
            RecordMesg recordMesg = new RecordMesg();
            recordMesg.setTimestamp(new DateTime(new Date(startTimeMillis + i * 1000L)));
            encoder.write(recordMesg);
        }

        for (SetLogWithName set : sets) {
            FitSetHelper.writeSet(encoder, startTimeMillis, set.trackingType,
                    set.weight, set.reps, set.durationSeconds,
                    set.fitCategoryValue, set.fitSubtypeValue);
        }

        LapMesg lapMesg = new LapMesg();
        lapMesg.setTimestamp(endTime);
        lapMesg.setStartTime(startTime);
        lapMesg.setTotalElapsedTime(elapsedSeconds);
        lapMesg.setTotalTimerTime(elapsedSeconds);
        lapMesg.setSport(Sport.TRAINING);
        lapMesg.setSubSport(SubSport.STRENGTH_TRAINING);
        encoder.write(lapMesg);

        SessionMesg sessionMesg = new SessionMesg();
        sessionMesg.setTimestamp(endTime);
        sessionMesg.setStartTime(startTime);
        sessionMesg.setTotalElapsedTime(elapsedSeconds);
        sessionMesg.setTotalTimerTime(elapsedSeconds);
        sessionMesg.setSport(Sport.TRAINING);
        sessionMesg.setSubSport(SubSport.STRENGTH_TRAINING);
        sessionMesg.setFirstLapIndex(0);
        sessionMesg.setNumLaps(1);
        encoder.write(sessionMesg);

        EventMesg stopEvent = new EventMesg();
        stopEvent.setTimestamp(endTime);
        stopEvent.setEvent(Event.TIMER);
        stopEvent.setEventType(EventType.STOP_ALL);
        encoder.write(stopEvent);

        ActivityMesg activityMesg = new ActivityMesg();
        activityMesg.setTimestamp(endTime);
        activityMesg.setNumSessions(1);
        activityMesg.setType(com.garmin.fit.Activity.MANUAL);
        activityMesg.setEvent(Event.ACTIVITY);
        activityMesg.setEventType(EventType.STOP);
        TimeZone tz = TimeZone.getDefault();
        long offsetSeconds = tz.getOffset(endTimeMillis) / 1000;
        activityMesg.setLocalTimestamp(endTime.getTimestamp() + offsetSeconds);
        encoder.write(activityMesg);

        encoder.close();
    }
}