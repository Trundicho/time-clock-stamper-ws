package de.trundicho.timeclockstamper.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.trundicho.timeclockstamper.service.TimeClockStamperService;

@RestController
public class TimeClockStamperController {

    private final TimeClockStamperService timeClockStamperService;

    @Autowired
    public TimeClockStamperController(TimeClockStamperService timeClockStamperService) {
        this.timeClockStamperService = timeClockStamperService;
    }

    @RequestMapping(value = "/stamp/inOrOut", method = RequestMethod.POST, produces = "text/plain")
    public String stampInOrOut() {
        return timeClockStamperService.stampInOrOut();
    }

    @RequestMapping(value = "/stamp/current/state", method = RequestMethod.GET, produces = "text/plain")
    public String currentStampState() {
        return timeClockStamperService.currentStampState();
    }

    @RequestMapping(value = "/stamp/worked/today", method = RequestMethod.GET, produces = "text/plain")
    public String hoursWorkedToday() {
        return timeClockStamperService.hoursWorkedToday();
    }

    @RequestMapping(value = "/stamp/overtime/month", method = RequestMethod.GET, produces = "text/plain")
    public String overtimeMonth() {
        return timeClockStamperService.overtimeMonth();
    }
}
