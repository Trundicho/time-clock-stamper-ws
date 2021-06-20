package de.trundicho.timeclockinandout.domain.ports;

import java.util.List;

import de.trundicho.timeclockinandout.domain.model.ClockTime;

public interface ClockTimePersistencePort {

    void write(List<ClockTime> clockTimes);

    List<ClockTime> read();
}
