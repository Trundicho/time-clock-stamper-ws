package de.trundicho.timeclockstamper.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.trundicho.timeclockstamper.api.ClockTimeResponse;
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

    @RequestMapping(value = "state", method = RequestMethod.GET, produces = "application/json")
    public ClockTimeResponse getState() {
        return timeClockStamperService.getTimeClockResponse();
    }

    @RequestMapping(value = "state/{yearInt}/{monthInt}", method = RequestMethod.GET, produces = "text/plain")
    public String getStateByMonth(@RequestParam("yearInt") Integer yearInt, @RequestParam("monthInt") Integer monthInt) {
        return timeClockStamperService.getOvertimeMonth(yearInt, monthInt);
    }
}
