package de.trundicho.timeclockstamper.application;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import de.trundicho.timeclockstamper.api.ClockType;
import de.trundicho.timeclockstamper.api.ClockTimeResponse;
import de.trundicho.timeclockstamper.domain.model.ClockTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ApplicationTests {

    @Value("${persistence.file}")
    private String persistenceFile;
    @Autowired
    TimeClockStamperController timeClockStamperController;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws IOException {
        objectMapper.writeValue(new File(createFileName()), Collections.emptyList());
    }

    @Test
    void whenClockingInOrOut_thenStateChanges() {
        assertThat(timeClockStamperController.getState().getCurrentState()).isEqualTo(ClockType.CLOCK_OUT);
        assertThat(timeClockStamperController.stampInOrOut().getCurrentState()).isEqualTo(ClockType.CLOCK_IN);
        assertThat(timeClockStamperController.stampInOrOut().getCurrentState()).isEqualTo(ClockType.CLOCK_OUT);
        assertThat(timeClockStamperController.stampInOrOut().getCurrentState()).isEqualTo(ClockType.CLOCK_IN);
    }

    @Test
    void whenPauseExists_thenItIsSubstracted() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        ClockTime stamp1 = createClockTime(now, 9, 0);
        ClockTime stamp2 = createClockTime(now, 17, 0);
        ClockTime stamp3 = createClockTime(now, 17, 0).setPause(30);
        objectMapper.writeValue(new File(createFileName()), List.of(stamp1, stamp2, stamp3));
        ClockTimeResponse clockTimeResponse = timeClockStamperController.getState();
        assertThat(clockTimeResponse.getHoursWorkedToday()).isEqualTo("7h 30m. Left to 8 hours: 0h 30m");
    }

    @Test
    void whenMoreThanTwoStamping_thenGetHoursWorkedToday() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        ClockTime stamp1 = createClockTime(now, 9, 0);
        ClockTime stamp2 = createClockTime(now, 12, 0);
        ClockTime stamp3 = createClockTime(now, 13, 0);
        ClockTime stamp4 = createClockTime(now, 17, 0);
        objectMapper.writeValue(new File(createFileName()), List.of(stamp1, stamp2, stamp3, stamp4));
        ClockTimeResponse clockTimeResponse = timeClockStamperController.getState();
        assertThat(clockTimeResponse.getHoursWorkedToday()).isEqualTo("7h 0m. Left to 8 hours: 1h 0m");
    }

    private ClockTime createClockTime(LocalDateTime now, int hour, int minute) {
        return new ClockTime().setDate(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hour, minute));
    }

    private String createFileName() {
        LocalDateTime now = LocalDateTime.now();
        Month currentMonth = now.getMonth();
        int year = now.getYear();
        return year + "-" + currentMonth + "-" + persistenceFile;
    }

}
