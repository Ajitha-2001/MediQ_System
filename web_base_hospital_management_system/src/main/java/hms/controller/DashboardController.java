package hms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard"; //
    }

    @GetMapping("itadmin")
    public String itAdminDashboard() {
        return "itadmin/dashboard";
    }

    @GetMapping("receptionist-dashboard")
    public String receptionistDashboard() {
        return "receptionist-dashboard";
    }

    @GetMapping("/doctor-dashboard")
    public String doctorDashboard() {
        return "doctor-dashboard";
    }

    @GetMapping("nurse-dashboard")
    public String nurseDashboard() {
        return "nurse-dashboard";
    }

    @GetMapping("/patient-dashboard")
    public String patientDashboard() {
        return "patient-dashboard";
    }

    @GetMapping("/medical_records")
    public String medicalRecords() {
        return "medical_records";
    }
}
