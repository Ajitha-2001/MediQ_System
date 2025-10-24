package hms.controller.analytics;

import hms.dto.analytics.AppointmentReportDTO; // Assuming this DTO exists
import hms.dto.analytics.CustomReportDTO;
import hms.dto.analytics.RevenueReportDTO;
import hms.entity.analytics.CustomReport;
import hms.service.analytics.ChartService;
import hms.service.analytics.PdfService;
import hms.service.analytics.ReportGenerationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/analytics/reports")
public class ReportController {

    private final ReportGenerationService reportGenerationService;
    private final PdfService pdfService;
    private final ChartService chartService;

    public ReportController(ReportGenerationService rgs, PdfService ps, ChartService cs) {
        this.reportGenerationService = rgs;
        this.pdfService = ps;
        this.chartService = cs;
    }

    @GetMapping
    public String showReportDashboard() {
        return "analytics/reports-dashboard";
    }

    @GetMapping("/generate")
    public Object generateReport(
            @RequestParam String type,
            @RequestParam(defaultValue = "false") boolean downloadPdf,
            Model model) {

        Map<String, Object> reportData = new LinkedHashMap<>();
        String reportTitle;
        CustomReportDTO customReportData;

        switch (type.toLowerCase()) {
            case "appointments":
                customReportData = convertAppointmentToCustomReport(reportGenerationService.generateAppointmentReport());
                reportTitle = "Appointments Summary";
                break;
            case "revenue":
                customReportData = convertRevenueToCustomReport(reportGenerationService.generateRevenueReport());
                reportTitle = "Revenue Summary";
                break;
            default:
                return "redirect:/analytics/reports";
        }


        List<Integer> chartData = java.util.Arrays.asList(
                5, 8, 12, 10, 15, 13, 18, 20, 17, 22,
                25, 23, 28, 30, 27, 24, 26, 29, 31, 30,
                28, 32, 35, 33, 36, 38, 40, 37, 39, 42
        );
        String chartBase64 = chartService.createLineChartAsBase64(reportTitle, chartData);

        reportData.put("reportData", customReportData);
        reportData.put("report", new CustomReport());
        reportData.put("title", reportTitle);
        reportData.put("reportType", type);
        reportData.put("chartBase64", chartBase64);

        if (downloadPdf) {
            byte[] pdfBytes = pdfService.renderPdf("analytics/report-pdf-template", reportData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        }

        model.addAllAttributes(reportData);
        return "analytics/report-history-view";
    }


    private CustomReportDTO convertAppointmentToCustomReport(AppointmentReportDTO appointment) {
        CustomReportDTO dto = new CustomReportDTO();
        dto.setTitle("Appointment Report");
        dto.setPeriod("Current Period");

        Map<String, Long> summaryStats = new LinkedHashMap<>();
        // Example:
        // summaryStats.put("Total Appointments", appointment.getTotalAppointments());
        dto.setSummaryStats(summaryStats);

        Map<String, Double> calculatedStats = new LinkedHashMap<>();
        // Example:
        // calculatedStats.put("Average Duration", appointment.getAverageDuration());
        dto.setCalculatedStats(calculatedStats);

        dto.setChartLabels(java.util.Collections.emptyList());
        dto.setChartData(java.util.Collections.emptyList());

        return dto;
    }


    private CustomReportDTO convertRevenueToCustomReport(RevenueReportDTO revenue) {
        CustomReportDTO dto = new CustomReportDTO();
        dto.setTitle("Revenue Report");
        dto.setPeriod("Current Period");

        Map<String, Long> summaryStats = new LinkedHashMap<>();
        long totalRevenueRounded = revenue != null ? (long) revenue.getTotalRevenue() : 0L;
        summaryStats.put("Total Revenue", totalRevenueRounded);
        summaryStats.put("Number of Invoices", 0L);
        dto.setSummaryStats(summaryStats);

        Map<String, Double> calculatedStats = new LinkedHashMap<>();
        calculatedStats.put("Average Invoice", revenue != null ? revenue.getAverageInvoiceAmount() : 0.0);
        calculatedStats.put("Maximum Invoice", revenue != null ? revenue.getMaxInvoice() : 0.0);
        calculatedStats.put("Minimum Invoice", revenue != null ? revenue.getMinInvoice() : 0.0);
        dto.setCalculatedStats(calculatedStats);

        dto.setChartLabels(List.of("Min", "Average", "Max", "Total"));
        List<Double> chartData = (revenue != null)
                ? List.of(
                revenue.getMinInvoice(),
                revenue.getAverageInvoiceAmount(),
                revenue.getMaxInvoice(),
                revenue.getTotalRevenue()
        )
                : List.of(0.0, 0.0, 0.0, 0.0);
        dto.setChartData(chartData);

        return dto;
    }
}
