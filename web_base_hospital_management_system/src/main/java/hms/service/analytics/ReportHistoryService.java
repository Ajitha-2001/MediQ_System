package hms.service.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hms.dto.analytics.CustomReportDTO;
import hms.entity.analytics.CustomReport;
import hms.repository.analytics.CustomReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ReportHistoryService {

    private final CustomReportRepository reportRepository;
    private final ObjectMapper objectMapper;
    private final PdfService pdfService;
    private final ChartService chartService;

    public ReportHistoryService(CustomReportRepository reportRepository,
                                ObjectMapper objectMapper,
                                PdfService pdfService,
                                ChartService chartService) {
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
        this.pdfService = pdfService;
        this.chartService = chartService;
    }


    public CustomReport saveReport(CustomReportDTO reportData, String type, int year, int month) {
        CustomReport report = new CustomReport();
        report.setTitle(reportData.getTitle());
        report.setType(type);
        report.setPeriod(reportData.getPeriod());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        report.setStatus("ACTIVE");
        report.setCreatedBy("System");

        try {

            String chartBase64 = chartService.createLineChartAsBase64(reportData.getTitle(), reportData.getChartData());


            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("reportData", reportData);
            dataMap.put("chartBase64", chartBase64);

            String jsonData = objectMapper.writeValueAsString(dataMap);
            report.setData(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize report data", e);
        }

        return reportRepository.save(report);
    }


    public Page<CustomReport> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reportRepository.findAll(pageable);
    }


    public Page<CustomReport> findByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reportRepository.findByType(type, pageable);
    }


    public Page<CustomReport> findByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return reportRepository.findAll(pageable).map(report -> {
            if (report.getStatus().equals(status)) {
                return report;
            }
            return null;
        });
    }


    public CustomReport findById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }


    public CustomReport updateReport(Long id, String title, String description, String period) {
        CustomReport report = findById(id);

        report.setTitle(title);
        if (description != null && !description.isEmpty()) {
            report.setDescription(description);
        }
        if (period != null && !period.isEmpty()) {
            report.setPeriod(period);
        }
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }


    public CustomReport updateReportStatus(Long id, String status) {
        CustomReport report = findById(id);
        report.setStatus(status);
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }


    public CustomReport archiveReport(Long id) {
        CustomReport report = findById(id);
        report.markAsArchived(); // Assuming this method exists in entity
        report.setStatus("ARCHIVED");
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }


    public CustomReport restoreReport(Long id) {
        CustomReport report = findById(id);
        report.markAsActive(); // Assuming this method exists in entity
        report.setStatus("ACTIVE");
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }


    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with ID: " + id);
        }
        reportRepository.deleteById(id);
    }


    public int bulkDelete(Long[] reportIds) {
        int count = 0;
        for (Long id : reportIds) {
            if (reportRepository.existsById(id)) {
                reportRepository.deleteById(id);
                count++;
            }
        }
        return count;
    }


    public int deleteReportsOlderThan(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<CustomReport> oldReports = reportRepository.findByCreatedAtBefore(cutoffDate);
        int count = oldReports.size();
        reportRepository.deleteAll(oldReports);
        return count;
    }


    public Page<CustomReport> searchReports(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reportRepository.findByTitleContainingIgnoreCaseOrTypeContainingIgnoreCase(
                query, query, pageable);
    }


    public CustomReport duplicateReport(Long id, String newTitle) {
        CustomReport original = findById(id);

        CustomReport duplicate = new CustomReport();
        duplicate.setTitle(newTitle != null ? newTitle : original.getTitle() + " (Copy)");
        duplicate.setType(original.getType());
        duplicate.setPeriod(original.getPeriod());
        duplicate.setDescription(original.getDescription());
        duplicate.setData(original.getData());
        duplicate.setStatus("ACTIVE");
        duplicate.setCreatedAt(LocalDateTime.now());
        duplicate.setUpdatedAt(LocalDateTime.now());
        duplicate.setCreatedBy(original.getCreatedBy());

        return reportRepository.save(duplicate);
    }


    public CustomReportDTO parseReportData(CustomReport report) {
        try {
            Map<String, Object> dataMap = objectMapper.readValue(report.getData(), Map.class);
            Object reportDataObj = dataMap.get("reportData");

            // Convert back to CustomReportDTO
            return objectMapper.convertValue(reportDataObj, CustomReportDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse report data", e);
        }
    }


    public String extractChartFromData(String jsonData) {
        try {
            Map<String, Object> dataMap = objectMapper.readValue(jsonData, Map.class);
            return (String) dataMap.get("chartBase64");
        } catch (Exception e) {
            return null;
        }
    }


    public byte[] generatePdfFromSavedReport(CustomReport report) {
        CustomReportDTO reportData = parseReportData(report);
        String chartBase64 = extractChartFromData(report.getData());

        Map<String, Object> pdfModel = new HashMap<>();
        pdfModel.put("reportData", reportData);
        pdfModel.put("chartBase64", chartBase64);

        return pdfService.renderPdf("analytics/report-pdf-template", pdfModel);
    }


    public Map<String, Object> getReportStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", reportRepository.count());
        stats.put("reportsByType", getReportCountByType());
        stats.put("recentReports", reportRepository.findTop5ByOrderByCreatedAtDesc());
        return stats;
    }


    private Map<String, Long> getReportCountByType() {
        Map<String, Long> countMap = new HashMap<>();
        List<Object[]> results = reportRepository.countByType();

        for (Object[] result : results) {
            String type = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            countMap.put(type, count);
        }

        return countMap;
    }


    public boolean exists(Long id) {
        return reportRepository.existsById(id);
    }
}