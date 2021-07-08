package de.trundicho.timeclockstamper.api;

import java.util.List;

import de.trundicho.timeclockstamper.domain.model.ClockTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class ClockTimeResponse {

    private final ClockType currentState;
    private final String hoursWorkedToday;
    private final String overtimeMonth;
    private final List<ClockTime> clockTimes;
}
