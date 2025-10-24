package hms.service.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import hms.dto.analytics.CustomReportDTO;
import hms.entity.analytics.CustomReport;
import hms.repository.analytics.CustomReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class AutomatedReportService {
    private static final Logger log = LoggerFactory.getLogger(AutomatedReportService.class);
    private final CustomReportService customReportService;
    private final CustomReportRepository customReportRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AutomatedReportService(CustomReportService customReportService, CustomReportRepository customReportRepository) {
        this.customReportService = customReportService;
        this.customReportRepository = customReportRepository;
    }

    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateAndSaveMonthlyAppointmentReport() {
        try {
            log.info("Starting automated monthly report generation...");
            YearMonth lastMonth = YearMonth.from(LocalDate.now().minusMonths(1));

            CustomReportDTO reportDTO = customReportService.generateAppointmentReport(lastMonth.getYear(), lastMonth.getMonthValue());
            String reportJsonData = objectMapper.writeValueAsString(reportDTO);

            CustomReport savedReport = new CustomReport();
            savedReport.setTitle(reportDTO.getTitle());
            savedReport.setPeriod(reportDTO.getPeriod());
            savedReport.setType("Appointments");
            savedReport.setData(reportJsonData);

            customReportRepository.save(savedReport);
            log.info("Successfully saved monthly report for {}", lastMonth);
        } catch (Exception e) {
            log.error("Failed to generate automated monthly report", e);
        }
    }
}