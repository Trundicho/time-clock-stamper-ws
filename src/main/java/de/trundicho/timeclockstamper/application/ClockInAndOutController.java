package de.trundicho.timeclockstamper.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.trundicho.timeclockstamper.service.ClockInAndOutService;

@RestController
public class ClockInAndOutController {

    private final ClockInAndOutService clockInAndOutService;

    @Autowired
    public ClockInAndOutController(ClockInAndOutService clockInAndOutService) {
        this.clockInAndOutService = clockInAndOutService;
    }

    @RequestMapping(value = "/clock/inOrOut", method = RequestMethod.POST, produces = "text/plain")
    public String clockInOrOut() {
        return clockInAndOutService.clockInOrOut();
    }

    @RequestMapping(value = "/clock/current/state", method = RequestMethod.GET, produces = "text/plain")
    public String currentClockState() {
        return clockInAndOutService.currentClockState();
    }

    @RequestMapping(value = "/clock/worked/today", method = RequestMethod.GET, produces = "text/plain")
    public String hoursWorkedToday() {
        return clockInAndOutService.hoursWorkedToday();
    }

    @RequestMapping(value = "/clock/overtime/month", method = RequestMethod.GET, produces = "text/plain")
    public String overtimeMonth() {
        return clockInAndOutService.overtimeMonth();
    }
}
