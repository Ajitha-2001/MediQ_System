// src/main/java/hms/controller/appointment/HomeController.java
package hms.controller.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/appointments-home")
    public String home() {
        return "redirect:/appointments";
    }
}
