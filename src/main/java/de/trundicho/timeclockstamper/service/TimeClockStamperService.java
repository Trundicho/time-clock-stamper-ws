package de.trundicho.timeclockstamper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.trundicho.timeclockstamper.domain.model.ClockTime;
import de.trundicho.timeclockstamper.domain.model.ClockType;
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

    public String stampInOrOut() {
        ClockTime clockTime = clockNow();
        List<ClockTime> clockTimeDb = new ArrayList<>(clockTimePersistencePort.read());
        clockTimeDb.add(clockTime);
        clockTimePersistencePort.write(clockTimeDb);
        return getCurrentClockType().name() + clockTime;
    }

    public String currentStampState() {
        List<ClockTime> read = clockTimePersistencePort.read();
        if (!read.isEmpty()) {
            return getCurrentClockType().toString() + " Last: " + read.get(read.size() - 1);
        }
        return getCurrentClockType().toString();
    }

    public String hoursWorkedToday() {
        List<ClockTime> todayClockTimes = getClocksOn(today());
        int overallWorkedMinutes = 0;
        if (getCurrentClockType() == ClockType.CLOCK_IN) {
            //add fake clockOut
            todayClockTimes.add(clockNow());
        }
        if (!todayClockTimes.isEmpty()) {
            overallWorkedMinutes = getOverallMinusPauseMinutesIfOnlyOneStampIn(todayClockTimes);
        }
        return toHoursAndMinutes(overallWorkedMinutes) + ". Left to 8 hours: " + toHoursAndMinutes(
                EIGHT_HOURS_IN_MINUTES - overallWorkedMinutes);
    }

    public String overtimeMonth() {
        List<ClockTime> allClocksThisMonth = clockTimePersistencePort.read();
        ClockTime now = clockNow();
        if (getCurrentClockType() == ClockType.CLOCK_IN) {
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
                overallWorkedMinutes += getOverallMinusPauseMinutesIfOnlyOneStampIn(clocksAtDay);
            }
        }
        int minutesToWorkUntilToday = dayOfMonth * EIGHT_HOURS_IN_MINUTES;
        return toHoursAndMinutes(overallWorkedMinutes - minutesToWorkUntilToday);
    }

    private int getOverallMinusPauseMinutesIfOnlyOneStampIn(List<ClockTime> todayClockTimes) {
        List<ClockTime> todayClocksReverse = new ArrayList<>(todayClockTimes);
        Collections.reverse(todayClocksReverse);
        if (todayClocksReverse.size() % 2 == 1) {
            log.error("Not correct clocked day: " + todayClocksReverse + " assuming 8 hours of work");
            return EIGHT_HOURS_IN_MINUTES;
        }
        if (todayClocksReverse.isEmpty()) {
            log.info("Not clocked on this day, assuming 8 hours of work");
            return EIGHT_HOURS_IN_MINUTES;
        }
        boolean oneStampInAndOneStampOut = todayClockTimes.size() == 2;
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
        if (oneStampInAndOneStampOut) {
            overallWorkedMinutes = overallWorkedMinutes - defaultPause;
        }
        return overallWorkedMinutes;
    }

    private ClockTime clockNow() {
        return new ClockTime().setDate(LocalDateTime.now());
    }

    private ClockType getCurrentClockType() {
        if (clockTimePersistencePort.read().size() % 2 == 0) {
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

    private List<ClockTime> getClocksOn(LocalDateTime day) {
        return new ArrayList<>(clockTimePersistencePort.read().stream().filter(c -> c.getDate().isAfter(day)).collect(Collectors.toList()));
    }
}
