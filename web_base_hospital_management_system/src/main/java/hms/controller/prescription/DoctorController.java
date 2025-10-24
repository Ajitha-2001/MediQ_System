package hms.controller.prescription;

import hms.service.prescription.DoctorService;
import hms.entity.prescription.DoctorPP;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService service;

    public DoctorController(DoctorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorPP create(@RequestBody DoctorPP d) {
        return service.create(d);
    }

    @GetMapping
    public List<DoctorPP> list() {
        return service.list();
    }
}