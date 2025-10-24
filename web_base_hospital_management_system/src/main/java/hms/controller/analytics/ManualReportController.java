package hms.controller.analytics;

import hms.dto.analytics.CustomReportDTO;
import hms.dto.analytics.PatientReportDTO;
import hms.dto.analytics.RevenueReportDTO;
import hms.entity.analytics.CustomReport;
import hms.service.analytics.ChartService;
import hms.service.analytics.CustomReportService;
import hms.service.analytics.PdfService;
import hms.service.analytics.PatientReportService;
import hms.service.analytics.ReportGenerationService;
import hms.service.analytics.ReportHistoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/analytics/manual-reports")
public class ManualReportController {

    private final CustomReportService customReportService;
    private final ReportGenerationService reportGenerationService;
    private final PatientReportService patientReportService;
    private final ChartService chartService;
    private final PdfService pdfService;
    private final ReportHistoryService reportHistoryService;

    public ManualReportController(CustomReportService customReportService,
                                  ReportGenerationService reportGenerationService,
                                  PatientReportService patientReportService,
                                  ChartService chartService,
                                  PdfService pdfService,
                                  ReportHistoryService reportHistoryService) {
        this.customReportService = customReportService;
        this.reportGenerationService = reportGenerationService;
        this.patientReportService = patientReportService;
        this.chartService = chartService;
        this.pdfService = pdfService;
        this.reportHistoryService = reportHistoryService;
    }

    @GetMapping
    public String showReportForm(Model model) {
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        return "analytics/manual-report-form";
    }

    @PostMapping("/generate")
    public String generateReport(@RequestParam String reportType,
                                 @RequestParam int year,
                                 @RequestParam int month,
                                 @RequestParam(defaultValue = "false") boolean saveReport,
                                 Model model) {

        CustomReportDTO reportData = null;
        String chartBase64 = "";
        String typeName = "";

        try {
            switch (reportType.toLowerCase()) {
                case "appointments":
                    reportData = customReportService.generateAppointmentReport(year, month);
                    chartBase64 = chartService.createLineChartAsBase64(reportData.getTitle(), reportData.getChartData());
                    typeName = "Appointments";
                    break;
                case "revenue":
                    reportData = convertRevenueToCustomReport(reportGenerationService.generateRevenueReport(), year, month);
                    chartBase64 = chartService.createLineChartAsBase64("Revenue Metrics", reportData.getChartData());
                    typeName = "Revenue";
                    break;
                case "patients":
                    reportData = convertPatientToCustomReport(patientReportService.generatePatientReport(), year, month);
                    chartBase64 = chartService.createLineChartAsBase64("Patient Age Distribution", reportData.getChartData());
                    typeName = "Patients";
                    break;
                default:
                    model.addAttribute("error", "Invalid report type selected.");
                    return "analytics/manual-report-form";
            }

            CustomReport savedReport = reportHistoryService.saveReport(reportData, typeName, year, month);
            model.addAttribute("report", savedReport);
            model.addAttribute("reportData", reportData);
            model.addAttribute("chartBase64", chartBase64);
            model.addAttribute("success", "Report generated successfully!");
            model.addAttribute("error", null);

            return "analytics/report-history-view";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate report: " + e.getMessage());
            return "analytics/manual-report-form";
        }
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam String reportType,
                                              @RequestParam int year,
                                              @RequestParam int month) {

        CustomReportDTO reportData;
        String chartBase64;
        Map<String, Object> pdfModel = new HashMap<>();

        switch (reportType.toLowerCase()) {
            case "appointments":
                reportData = customReportService.generateAppointmentReport(year, month);
                chartBase64 = chartService.createLineChartAsBase64(reportData.getTitle(), reportData.getChartData());
                break;
            case "revenue":
                reportData = convertRevenueToCustomReport(reportGenerationService.generateRevenueReport(), year, month);
                chartBase64 = chartService.createLineChartAsBase64("Revenue Metrics", reportData.getChartData());
                break;
            case "patients":
                reportData = convertPatientToCustomReport(patientReportService.generatePatientReport(), year, month);
                chartBase64 = chartService.createLineChartAsBase64("Patient Age Distribution", reportData.getChartData());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        pdfModel.put("reportData", reportData);
        pdfModel.put("chartBase64", chartBase64);
        byte[] pdfBytes = pdfService.renderPdf("analytics/report-pdf-template", pdfModel);

        String filename = reportType + "-report-" + year + "-" + month + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private CustomReportDTO convertRevenueToCustomReport(RevenueReportDTO revenue, int year, int month) {
        CustomReportDTO dto = new CustomReportDTO();
        dto.setTitle("Revenue Report");
        dto.setPeriod(getMonthName(month) + " " + year);

        Map<String, Long> summaryStats = new LinkedHashMap<>();
        summaryStats.put("Total Revenue", (revenue != null) ? (long) revenue.getTotalRevenue() : 0L);
        summaryStats.put("Number of Invoices", 0L);
        dto.setSummaryStats(summaryStats);

        Map<String, Double> calculatedStats = new LinkedHashMap<>();
        calculatedStats.put("Average Invoice", (revenue != null) ? revenue.getAverageInvoiceAmount() : 0.0);
        calculatedStats.put("Maximum Invoice", (revenue != null) ? revenue.getMaxInvoice() : 0.0);
        calculatedStats.put("Minimum Invoice", (revenue != null) ? revenue.getMinInvoice() : 0.0);
        dto.setCalculatedStats(calculatedStats);

        dto.setChartLabels(List.of("Min", "Average", "Max", "Total"));
        dto.setChartData((revenue != null)
                ? List.of(revenue.getMinInvoice(), revenue.getAverageInvoiceAmount(),
                revenue.getMaxInvoice(), revenue.getTotalRevenue())
                : List.of(0.0, 0.0, 0.0, 0.0));
        return dto;
    }

    private CustomReportDTO convertPatientToCustomReport(PatientReportDTO patient, int year, int month) {
        CustomReportDTO dto = new CustomReportDTO();
        dto.setTitle("Patient Analytics Report");
        dto.setPeriod(getMonthName(month) + " " + year);

        Map<String, Long> summaryStats = new LinkedHashMap<>();
        if (patient != null) {
            summaryStats.put("Total Patients", patient.getTotalPatients());
            summaryStats.put("Active Patients", patient.getActivePatients());
            summaryStats.put("New This Month", patient.getNewPatientsThisMonth());
            if (patient.getGenderDistribution() != null)
                patient.getGenderDistribution().forEach((g, c) -> summaryStats.put("Gender: " + g, c));
        }
        dto.setSummaryStats(summaryStats);

        Map<String, Double> calculatedStats = new LinkedHashMap<>();
        if (patient != null && patient.getTotalPatients() > 0) {
            calculatedStats.put("Active Rate %", (patient.getActivePatients() * 100.0) / patient.getTotalPatients());
            calculatedStats.put("New Patient Rate %", (patient.getNewPatientsThisMonth() * 100.0) / patient.getTotalPatients());
        }
        dto.setCalculatedStats(calculatedStats);

        if (patient != null && patient.getAgeGroups() != null && !patient.getAgeGroups().isEmpty()) {
            dto.setChartLabels(patient.getAgeGroups().keySet().stream().collect(Collectors.toList()));
            dto.setChartData(patient.getAgeGroups().values().stream()
                    .map(v -> v == null ? 0.0 : v.doubleValue()).collect(Collectors.toList()));
        } else {
            dto.setChartLabels(List.of());
            dto.setChartData(List.of());
        }
        return dto;
    }

    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return (month < 1 || month > 12) ? "Unknown" : months[month];
    }
}
