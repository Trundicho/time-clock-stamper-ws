package de.trundicho.timeclockstamper.application;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ApplicationTests {

    @Value("${persistence.file}")
    private String persistenceFile;
    @Autowired
    TimeClockStamperController timeClockStamperController;

    @BeforeEach
    void setup() throws IOException {
        new ObjectMapper().writeValue(new File(createFileName()), Collections.emptyList());
    }

    @Test
    void whenClockingInOrOut_thenStateChanges() {
        assertThat(timeClockStamperController.currentStampState()).isEqualTo("CLOCK_OUT");
        timeClockStamperController.stampInOrOut();
        assertThat(timeClockStamperController.currentStampState()).isEqualTo("CLOCK_IN");
        timeClockStamperController.stampInOrOut();
        assertThat(timeClockStamperController.currentStampState()).isEqualTo("CLOCK_OUT");
        timeClockStamperController.stampInOrOut();
        assertThat(timeClockStamperController.currentStampState()).isEqualTo("CLOCK_IN");
    }

    private String createFileName() {
        Month currentMonth = getCurrentMonth();
        return currentMonth + "-" + persistenceFile;
    }

    private Month getCurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.getMonth();
    }

}
