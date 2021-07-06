package de.trundicho.timeclockstamper.application;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.trundicho.timeclockstamper.api.ClockTimeResponse;
import de.trundicho.timeclockstamper.domain.model.ClockTime;
import de.trundicho.timeclockstamper.service.TimeClockStamperService;

@RestController
@RequestMapping(value = "stamp")
public class TimeClockStamperController {

    private final TimeClockStamperService timeClockStamperService;

    @Autowired
    public TimeClockStamperController(TimeClockStamperService timeClockStamperService) {
        this.timeClockStamperService = timeClockStamperService;
    }

    @RequestMapping(value = "inOrOut", method = RequestMethod.POST, produces = "application/json")
    public ClockTimeResponse stampInOrOut() {
        return timeClockStamperService.stampInOrOut();
    }

    @RequestMapping(value = "time", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ClockTimeResponse time(@RequestBody LocalTime time) {
        return timeClockStamperService.stamp(time);
    }

    @RequestMapping(value = "state", method = RequestMethod.GET, produces = "application/json")
    public ClockTimeResponse getState() {
        return timeClockStamperService.getTimeClockResponse();
    }

    @RequestMapping(value = "state/{yearInt}/{monthInt}", method = RequestMethod.GET, produces = "text/plain")
    public String getStateByMonth(@PathVariable("yearInt") Integer yearInt, @PathVariable("monthInt") Integer monthInt) {
        return timeClockStamperService.getOvertimeMonth(yearInt, monthInt);
    }
}
