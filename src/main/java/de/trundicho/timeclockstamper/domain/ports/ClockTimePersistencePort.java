package de.trundicho.timeclockstamper.domain.ports;

import java.util.List;

import de.trundicho.timeclockstamper.domain.model.ClockTime;

public interface ClockTimePersistencePort {

    void write(List<ClockTime> clockTimes);

    List<ClockTime> read();
}
