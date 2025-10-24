package hms.entity.appointment;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules")
public class DoctorSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="doctor_id", nullable=false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name="day_of_week", nullable=false, length=12)
    private DayOfWeek dayOfWeek;

    @Column(name="start_time", nullable=false)
    private LocalTime startTime;

    @Column(name="end_time", nullable=false)
    private LocalTime endTime;

    @Column(name="slot_minutes", nullable=false)
    private int slotMinutes = 30;


    public Long getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getSlotMinutes() { return slotMinutes; }
    public void setSlotMinutes(int slotMinutes) { this.slotMinutes = slotMinutes; }
}
