package hms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nurse")
public class NurseController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "nurse-dashboard";
    }
}