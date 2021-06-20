package de.trundicho.timeclockinandout.adapters.persistence;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.trundicho.timeclockinandout.domain.model.ClockTime;
import de.trundicho.timeclockinandout.domain.ports.ClockTimePersistencePort;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClockTimeFileReaderAndWriter implements ClockTimePersistencePort {

    private final ObjectMapper objectMapper;

    @Value("${persistence.file}")
    private String persistenceFile;

    @Autowired
    public ClockTimeFileReaderAndWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(List<ClockTime> clockTimes) {
        try {
            objectMapper
                    .writerWithDefaultPrettyPrinter().writeValue(new File(createFileName()), clockTimes);
        } catch (IOException e) {
            log.error("Can not write to file " + e.getMessage());
        }
    }

    private String createFileName() {
        Month currentMonth = getCurrentMonth();
        return currentMonth + "-" + persistenceFile;
    }

    private Month getCurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.getMonth();
    }

    public List<ClockTime> read() {
        try {
            return objectMapper.readValue(new File(createFileName()), new TypeReference<>() {

            });
        } catch (IOException e) {
            log.error("Can not read from file " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
