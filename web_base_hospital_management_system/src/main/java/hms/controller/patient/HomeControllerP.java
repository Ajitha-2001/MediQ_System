// src/main/java/hms/controller/patient/HomeControllerP.java
package hms.controller.patient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerP {

  // the only "/" mapping in the app
  @GetMapping("/")
  public String home() {
    // your list controller is GET /appointments
    return "redirect:/appointments";
  }
}
