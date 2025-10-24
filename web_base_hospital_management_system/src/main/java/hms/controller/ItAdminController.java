package hms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itadmin")
public class ItAdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        // renders src/main/resources/templates/itadmin-dashboard.html
        return "itadmin-dashboard";
    }
}
