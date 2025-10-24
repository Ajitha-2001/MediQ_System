package hms.service.analytics;

import hms.dto.analytics.AppointmentReportDTO;
import hms.dto.analytics.RevenueReportDTO;
import hms.entity.appointment.Appointment;
import hms.entity.appointment.AppointmentStatus;
import hms.entity.billing.Bill;
import hms.repository.appointments.AppointmentRepository;
import hms.repository.billing.BillRepository; // Assuming a 'billing' subpackage for the repo
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportGenerationService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public ReportGenerationService(AppointmentRepository appointmentRepository, BillRepository billRepository) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    public AppointmentReportDTO generateAppointmentReport() {
        List<Appointment> appointments = appointmentRepository.findAll();


        long approved = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.APPROVED).count();
        long canceled = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELED).count();
        long pending = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.BOOKED).count();


        Map<LocalDate, Long> countsPerDay = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getStartTime().toLocalDate(), Collectors.counting()));

        List<Double> dailyCounts = countsPerDay.values().stream().map(Long::doubleValue).sorted().collect(Collectors.toList());

        double mean = dailyCounts.stream().mapToDouble(val -> val).average().orElse(0.0);
        double median = calculateMedian(dailyCounts);
        double max = dailyCounts.stream().mapToDouble(val -> val).max().orElse(0.0);
        double min = dailyCounts.stream().mapToDouble(val -> val).min().orElse(0.0);

        AppointmentReportDTO dto = new AppointmentReportDTO();
        dto.setTotal(appointments.size());
        dto.setApproved(approved);
        dto.setCanceled(canceled);
        dto.setPending(pending);
        dto.setDailyStats(Map.of("mean", mean, "median", median, "max", max, "min", min));
        return dto;
    }

    public RevenueReportDTO generateRevenueReport() {
        List<Bill> bills = billRepository.findAll();
        List<Double> amounts = bills.stream().map(Bill::getAmount).collect(Collectors.toList());

        double totalRevenue = amounts.stream().mapToDouble(Double::doubleValue).sum();
        double avg = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double max = amounts.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double min = amounts.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

        RevenueReportDTO dto = new RevenueReportDTO();
        dto.setTotalRevenue(totalRevenue);
        dto.setAverageInvoiceAmount(avg);
        dto.setMaxInvoice(max);
        dto.setMinInvoice(min);
        return dto;
    }

    private double calculateMedian(List<Double> numbers) {
        if (numbers.isEmpty()) return 0.0;
        int size = numbers.size();
        if (size % 2 == 0) {
            return (numbers.get(size / 2 - 1) + numbers.get(size / 2)) / 2.0;
        } else {
            return numbers.get(size / 2);
        }
    }
}