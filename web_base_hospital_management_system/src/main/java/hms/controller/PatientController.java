
package hms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientController {

    @GetMapping("/patient/dashboard")
    public String patientDashboard() {
        return "patient-dashboard"; // renders templates/patient-dashboard.html
    }
}
