package de.trundicho.timeclockstamper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.trundicho.timeclockstamper.api.ClockType;
import de.trundicho.timeclockstamper.api.ClockTimeResponse;
import de.trundicho.timeclockstamper.domain.model.ClockTime;
import de.trundicho.timeclockstamper.domain.ports.ClockTimePersistencePort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TimeClockStamperService {

    private static final int EIGHT_HOURS_IN_MINUTES = 480;
    private final ClockTimePersistencePort clockTimePersistencePort;
    @Value("${timeclockstamper.default.pause.in.minutes}")
    private Integer defaultPause;

    @Autowired
    public TimeClockStamperService(ClockTimePersistencePort clockTimePersistencePort) {
        this.clockTimePersistencePort = clockTimePersistencePort;
    }

    public ClockTimeResponse stampInOrOut() {
        return stamp(clockNow());
    }

    public ClockTimeResponse getTimeClockResponse() {
        List<ClockTime> clockTimes = clockTimePersistencePort.read();
        return createClockTimeResponse(clockTimes);
    }

    private ClockTimeResponse createClockTimeResponse(List<ClockTime> clockTimes) {
        return new ClockTimeResponse(ClockType.valueOf(currentStampState(clockTimes)), hoursWorkedToday(clockTimes),
                overtimeMonth(clockTimes));
    }

    public ClockTimeResponse addPause() {
        return stamp(clockNowWithPause(defaultPause));
    }

    private String currentStampState(List<ClockTime> clockTimes) {
        return getCurrentClockType(clockTimes).toString();
    }

    private String hoursWorkedToday(List<ClockTime> clockTimes) {
        List<ClockTime> todayClockTimes = getClocksAndPausesOn(today());
        int overallWorkedMinutes = 0;
        if (getCurrentClockType(clockTimes) == ClockType.CLOCK_IN) {
            //add fake clockOut
            todayClockTimes.add(clockNow());
        }
        if (!todayClockTimes.isEmpty()) {
            overallWorkedMinutes = getOverallMinutes(todayClockTimes);
        }
        return toHoursAndMinutes(overallWorkedMinutes) + ". Left to 8 hours: " + toHoursAndMinutes(
                EIGHT_HOURS_IN_MINUTES - overallWorkedMinutes);
    }

    private String overtimeMonth(List<ClockTime> clockTimes) {
        List<ClockTime> allClocksThisMonth = new ArrayList<>(clockTimes);
        ClockTime now = clockNow();
        if (getCurrentClockType(clockTimes) == ClockType.CLOCK_IN) {
            //add fake clockOut
            allClocksThisMonth.add(now);
        }
        int dayOfMonth = now.getDate().getDayOfMonth();
        int overallWorkedMinutes = 0;
        for (int i = 1; i <= dayOfMonth; i++) {
            final int dom = i;
            List<ClockTime> clocksAtDay = allClocksThisMonth.stream()
                                                            .filter(clockTime -> clockTime.getDate().getDayOfMonth() == dom)
                                                            .collect(Collectors.toList());
            if (clocksAtDay.isEmpty()) {
                overallWorkedMinutes += EIGHT_HOURS_IN_MINUTES;
            } else {
                overallWorkedMinutes += getOverallMinutes(clocksAtDay);
            }
        }
        int minutesToWorkUntilToday = dayOfMonth * EIGHT_HOURS_IN_MINUTES;
        return toHoursAndMinutes(overallWorkedMinutes - minutesToWorkUntilToday);
    }

    private int getOverallMinutes(List<ClockTime> todayClockTimes) {
        Integer allPausesOnDay = todayClockTimes.stream()
                                                .filter(c -> c.getPause() != null)
                                                .map(ClockTime::getPause)
                                                .mapToInt(Integer::intValue)
                                                .sum();
        List<ClockTime> todayClocksReverse = new ArrayList<>(
                todayClockTimes.stream().filter(c -> c.getPause() == null).collect(Collectors.toList()));
        Collections.reverse(todayClocksReverse);
        if (todayClocksReverse.size() % 2 == 1) {
            log.error("Not correct clocked day: " + todayClocksReverse + " assuming 8 hours of work");
            return EIGHT_HOURS_IN_MINUTES;
        }
        if (todayClocksReverse.isEmpty()) {
            log.info("Not clocked on this day, assuming 8 hours of work");
            return EIGHT_HOURS_IN_MINUTES;
        }
        LocalDateTime lastClock = todayClocksReverse.get(0).getDate();
        int overallWorkedMinutes = 0;
        for (int i = 1; i < todayClocksReverse.size(); i++) {
            if (i % 2 == 0) {
                lastClock = todayClocksReverse.get(i).getDate();
                continue;
            }
            ClockTime clockTime = todayClocksReverse.get(i);
            int hour = lastClock.getHour();
            int minute = lastClock.getMinute();
            lastClock = clockTime.getDate();
            int minutes1 = toMinutes(hour, minute);
            int minutes2 = toMinutes(lastClock.getHour(), lastClock.getMinute());
            overallWorkedMinutes += minutes1 - minutes2;
        }
        return overallWorkedMinutes - allPausesOnDay;
    }

    private ClockTime clockNowWithPause(Integer pause) {
        return clockNow().setPause(pause);
    }

    private ClockTime clockNow() {
        return new ClockTime().setDate(LocalDateTime.now());
    }

    private ClockType getCurrentClockType(List<ClockTime> clockTimes) {
        List<ClockTime> clockTimesWithoutPause = clockTimes.stream().filter(c -> c.getPause() == null).collect(Collectors.toList());
        if (clockTimesWithoutPause.size() % 2 == 0) {
            return ClockType.CLOCK_OUT;
        }
        return ClockType.CLOCK_IN;
    }

    private String toHoursAndMinutes(int overallWorkedMinutes) {
        return (overallWorkedMinutes / 60) + "h " + overallWorkedMinutes % 60 + "m";
    }

    private int toMinutes(int hour, int minute) {
        return hour * 60 + minute;
    }

    private LocalDateTime today() {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
    }

    private List<ClockTime> getClocksAndPausesOn(LocalDateTime day) {
        return new ArrayList<>(clockTimePersistencePort.read().stream().filter(c -> c.getDate().isAfter(day)).collect(Collectors.toList()));
    }

    private ClockTimeResponse stamp(ClockTime clockTime) {
        List<ClockTime> clockTimeDb = new ArrayList<>(clockTimePersistencePort.read());
        clockTimeDb.add(clockTime);
        clockTimePersistencePort.write(clockTimeDb);
        return createClockTimeResponse(clockTimeDb);
    }
}
