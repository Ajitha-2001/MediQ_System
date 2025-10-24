package hms.config.appointment;

import hms.entity.appointment.Doctor;
import hms.entity.appointment.DoctorSchedule;
import hms.entity.patient.PatientP;
import hms.repository.appointments.DoctorRepository;
import hms.repository.appointments.DoctorScheduleRepository;
import hms.repository.patient.PatientRepositoryP;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCoreData(DoctorRepository doctors,
                                   DoctorScheduleRepository schedules,
                                   PatientRepositoryP patients) {
        return args -> {
            // ---- Seed one demo doctor ----
            Doctor demoDoctor = doctors.findAll().stream().findFirst().orElse(null);
            if (demoDoctor == null) {
                demoDoctor = new Doctor();
                demoDoctor.setName("Dr. Demo");
                demoDoctor.setSpecialization("General Medicine");
                demoDoctor = doctors.save(demoDoctor);
            }

            // ---- Seed Mon–Fri schedule for the demo doctor ----
            Doctor finalDemoDoctor = demoDoctor;
            boolean hasAnySchedule = schedules.findAll().stream()
                    .anyMatch(sc -> sc.getDoctor().getId().equals(finalDemoDoctor.getId()));

            if (!hasAnySchedule) {
                for (DayOfWeek day : EnumSet.of(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                    DoctorSchedule sc = new DoctorSchedule();
                    sc.setDoctor(demoDoctor);
                    sc.setDayOfWeek(day);
                    sc.setStartTime(LocalTime.of(9, 0));
                    sc.setEndTime(LocalTime.of(17, 0));
                    sc.setSlotMinutes(30);
                    schedules.save(sc);
                }
            }

            // ---- Seed demo patients ----
            if (patients.count() == 0) {
                PatientP p1 = new PatientP();
                p1.setFirstName("John");
                p1.setLastName("Doe");
                p1.setGender("Male");
                p1.setDateOfBirth(LocalDate.of(1985, 5, 10));
                patients.save(p1);

                PatientP p2 = new PatientP();
                p2.setFirstName("Jane");
                p2.setLastName("Smith");
                p2.setGender("Female");
                p2.setDateOfBirth(LocalDate.of(1990, 8, 20));
                patients.save(p2);
            }
        };
    }
}
