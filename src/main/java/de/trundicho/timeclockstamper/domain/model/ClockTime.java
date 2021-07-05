package de.trundicho.timeclockstamper.domain.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class ClockTime implements Comparable<ClockTime> {
    private LocalDateTime date;
    private Integer pause;

    @Override
    public int compareTo(ClockTime o) {
        return this.date.compareTo(o.date);
    }
}
