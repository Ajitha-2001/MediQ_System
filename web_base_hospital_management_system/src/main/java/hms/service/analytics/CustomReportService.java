package hms.service.analytics;

import hms.dto.analytics.CustomReportDTO;
import hms.entity.appointment.AppointmentStatus;
import hms.repository.appointments.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CustomReportService {
    private final AppointmentRepository appointmentRepository;

    public CustomReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public CustomReportDTO generateAppointmentReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        long total = appointmentRepository.countByStartTimeBetween(startOfMonth, endOfMonth);
        long approved = appointmentRepository.countByStatusAndStartTimeBetween(AppointmentStatus.APPROVED, startOfMonth, endOfMonth);
        long canceled = appointmentRepository.countByStatusAndStartTimeBetween(AppointmentStatus.CANCELED, startOfMonth, endOfMonth);
        long pending = appointmentRepository.countByStatusAndStartTimeBetween(AppointmentStatus.PENDING, startOfMonth, endOfMonth)
                + appointmentRepository.countByStatusAndStartTimeBetween(AppointmentStatus.BOOKED, startOfMonth, endOfMonth);

        Map<String, Long> summaryStats = new LinkedHashMap<>();
        summaryStats.put("Total Appointments", total);
        summaryStats.put("Approved", approved);
        summaryStats.put("Canceled", canceled);
        summaryStats.put("Pending / Booked", pending);

        List<Object[]> dailyCountsResult = appointmentRepository.findDailyCountsInMonth(year, month);
        Map<Integer, Integer> dailyDataMap = dailyCountsResult.stream()
                .collect(Collectors.toMap(row -> (Integer) row[0], row -> ((Number) row[1]).intValue()));
        List<Integer> dailyData = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .map(day -> dailyDataMap.getOrDefault(day, 0))
                .boxed().collect(Collectors.toList());

        Map<String, Double> calculatedStats = calculateStats(dailyData);

        CustomReportDTO reportDTO = new CustomReportDTO();
        reportDTO.setTitle("Monthly Appointments Report");
        reportDTO.setPeriod(Month.of(month).name() + " " + year);
        reportDTO.setSummaryStats(summaryStats);
        reportDTO.setCalculatedStats(calculatedStats);
        reportDTO.setChartLabels(IntStream.rangeClosed(1, yearMonth.lengthOfMonth()).mapToObj(String::valueOf).collect(Collectors.toList()));
        reportDTO.setChartData(dailyData);
        return reportDTO;
    }

    private Map<String, Double> calculateStats(List<Integer> data) {
        Map<String, Double> stats = new HashMap<>();
        List<Double> doubleData = data.stream().map(Integer::doubleValue).sorted().collect(Collectors.toList());
        stats.put("Mean (per day)", doubleData.stream().mapToDouble(d -> d).average().orElse(0.0));
        stats.put("Max (per day)", doubleData.stream().mapToDouble(d -> d).max().orElse(0.0));
        stats.put("Min (per day)", doubleData.stream().mapToDouble(d -> d).min().orElse(0.0));
        double median = 0.0;
        if (!doubleData.isEmpty()) {
            int size = doubleData.size();
            median = (size % 2 == 0) ? (doubleData.get(size / 2 - 1) + doubleData.get(size / 2)) / 2.0 : doubleData.get(size / 2);
        }
        stats.put("Median (per day)", median);
        return stats;
    }
}