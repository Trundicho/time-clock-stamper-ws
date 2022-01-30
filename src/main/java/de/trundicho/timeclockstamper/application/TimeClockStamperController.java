package de.trundicho.timeclockstamper.application;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.trundicho.timeclockstamper.core.adapters.api.ClockTimeDataDto;
import de.trundicho.timeclockstamper.core.adapters.api.TimeClockStamperApi;
import de.trundicho.timeclockstamper.core.adapters.persistence.FilePersistence;

@RestController
@RequestMapping(value = "stamp")
public class TimeClockStamperController {

    private final TimeClockStamperApi timeClockStamperService;

    public TimeClockStamperController(@Value("${time.zone}") String timeZone, @Value("${persistence.folder}") String persistenceFolder,
            @Value("${persistence.file}") String persistenceFile) {
        this.timeClockStamperService = new TimeClockStamperApi(timeZone, new FilePersistence(persistenceFolder, persistenceFile, timeZone));
    }

    @RequestMapping(value = "inOrOut", method = RequestMethod.POST, produces = "application/json")
    public ClockTimeDataDto stampInOrOut() {
        return timeClockStamperService.stampInOrOut();
    }

    @RequestMapping(value = "today", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ClockTimeDataDto today(@RequestBody ClockTimeDataDto ClockTimeDataDto) {
        return timeClockStamperService.setToday(ClockTimeDataDto);
    }

    @RequestMapping(value = "time", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ClockTimeDataDto time(@RequestBody LocalTime time) {
        return timeClockStamperService.stamp(time);
    }

    @RequestMapping(value = "state", method = RequestMethod.GET, produces = "application/json")
    public ClockTimeDataDto getState() {
        return timeClockStamperService.getTimeClockResponse();
    }

    @RequestMapping(value = "state/{yearInt}/{monthInt}", method = RequestMethod.GET, produces = "text/plain")
    public String getStateByMonth(@PathVariable("yearInt") Integer yearInt, @PathVariable("monthInt") Integer monthInt) {
        return timeClockStamperService.getOvertimeMonth(yearInt, monthInt);
    }
}
