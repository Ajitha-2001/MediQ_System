package hms.controller.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import hms.dto.analytics.CustomReportDTO;
import hms.entity.analytics.CustomReport;
import hms.repository.analytics.CustomReportRepository;
import hms.service.analytics.ChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportDashboardController {
    private final CustomReportRepository customReportRepository;
    private final ChartService chartService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportDashboardController(CustomReportRepository cpr, ChartService cs) {
        this.customReportRepository = cpr;
        this.chartService = cs;
    }

    @GetMapping
    public String showDashboard(Model model) {
        List<CustomReport> savedReports = customReportRepository.findAll();
        model.addAttribute("savedReports", savedReports);
        return "analytics/main-reports-dashboard";
    }

    @GetMapping("/view/{id}")
    public String viewSavedReport(@PathVariable Long id, Model model) {
        CustomReport savedReport = customReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + id));
        try {
            CustomReportDTO reportData = objectMapper.readValue(savedReport.getData(), CustomReportDTO.class);
            String chartBase64 = chartService.createLineChartAsBase64(reportData.getTitle(), reportData.getChartData());
            model.addAttribute("report", savedReport);
            model.addAttribute("reportData", reportData);
            model.addAttribute("chartBase64", chartBase64);
            return "analytics/report-history-view";
        } catch (Exception e) {
            return "redirect:/reports?error=true";
        }
    }
}
