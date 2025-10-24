package hms.controller.analytics;

import hms.entity.analytics.CustomReport;
import hms.service.analytics.ReportHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/analytics/report-history")
public class ReportHistoryController {

    private final ReportHistoryService reportHistoryService;

    public ReportHistoryController(ReportHistoryService reportHistoryService) {
        this.reportHistoryService = reportHistoryService;
    }

    @GetMapping
    public String listReports(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) String status,
                              Model model) {

        Page<CustomReport> pageObj;

        try {
            if (type != null && !type.isEmpty()) {
                pageObj = reportHistoryService.findByType(type, page, size);
                model.addAttribute("selectedType", type);
            } else if (status != null && !status.isEmpty()) {
                pageObj = reportHistoryService.findByStatus(status, page, size);
                model.addAttribute("selectedStatus", status);
            } else {
                pageObj = reportHistoryService.findAll(page, size);
            }


            model.addAttribute("reports", pageObj != null ? pageObj.getContent() : Collections.emptyList());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pageObj != null ? pageObj.getTotalPages() : 1);
            model.addAttribute("totalItems", pageObj != null ? pageObj.getTotalElements() : 0);


            model.addAttribute("reportStats", reportHistoryService.getReportStatistics());
            model.addAttribute("searchQuery", null);
            model.addAttribute("success", model.getAttribute("success"));
            model.addAttribute("error", model.getAttribute("error"));

        } catch (Exception e) {
            // Fail-safe so Thymeleaf never crashes during parsing
            model.addAttribute("reports", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalItems", 0);
            model.addAttribute("reportStats", Collections.emptyMap());
            model.addAttribute("searchQuery", null);
            model.addAttribute("error", "Failed to load reports: " + e.getMessage());
        }

        return "analytics/report-history-list";
    }


    @GetMapping("/{id}")
    public String viewReport(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomReport report = reportHistoryService.findById(id);
            model.addAttribute("report", report);
            model.addAttribute("reportData", reportHistoryService.parseReportData(report));
            model.addAttribute("chartBase64", reportHistoryService.extractChartFromData(report.getData()));
            model.addAttribute("success", model.getAttribute("success"));
            model.addAttribute("error", model.getAttribute("error"));
            return "analytics/report-history-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Report not found: " + e.getMessage());
            return "redirect:/analytics/report-history";
        }
    }


    @GetMapping("/create")
    public String createReportForm(Model model) {
        model.addAttribute("report", new CustomReport());
        return "analytics/report-create";
    }


    @GetMapping("/{id}/edit")
    public String editReportForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomReport report = reportHistoryService.findById(id);
            model.addAttribute("report", report);
            return "analytics/report-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Report not found");
            return "redirect:/analytics/report-history";
        }
    }


    @PostMapping("/{id}/update")
    public String updateReport(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) String period,
                               RedirectAttributes redirectAttributes) {
        try {
            reportHistoryService.updateReport(id, title, description, period);
            redirectAttributes.addFlashAttribute("success", "Report updated successfully");
            return "redirect:/analytics/report-history/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update report: " + e.getMessage());
            return "redirect:/analytics/report-history/" + id + "/edit";
        }
    }


    @PostMapping("/{id}/archive")
    public String archiveReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reportHistoryService.archiveReport(id);
            redirectAttributes.addFlashAttribute("success", "Report archived successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to archive report: " + e.getMessage());
        }
        return "redirect:/analytics/report-history";
    }


    @PostMapping("/{id}/restore")
    public String restoreReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            CustomReport report = reportHistoryService.findById(id);
            if (report != null) {
                report.markAsActive();
                reportHistoryService.updateReportStatus(id, "ACTIVE");
                redirectAttributes.addFlashAttribute("success", "Report restored successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Report not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to restore report: " + e.getMessage());
        }
        return "redirect:/analytics/report-history/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reportHistoryService.deleteReport(id);
            redirectAttributes.addFlashAttribute("success", "Report deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete report: " + e.getMessage());
        }
        return "redirect:/analytics/report-history";
    }


    @PostMapping("/bulk-delete")
    public String bulkDeleteReports(@RequestParam(value = "reportIds", required = false) Long[] reportIds,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (reportIds == null || reportIds.length == 0) {
                redirectAttributes.addFlashAttribute("error", "No reports selected");
                return "redirect:/analytics/report-history";
            }

            int deletedCount = reportHistoryService.bulkDelete(reportIds);
            redirectAttributes.addFlashAttribute("success", deletedCount + " report(s) deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete reports: " + e.getMessage());
        }
        return "redirect:/analytics/report-history";
    }


    @PostMapping("/cleanup")
    public String cleanupOldReports(@RequestParam int daysOld, RedirectAttributes redirectAttributes) {
        try {
            int deletedCount = reportHistoryService.deleteReportsOlderThan(daysOld);
            redirectAttributes.addFlashAttribute("success", deletedCount + " old report(s) cleaned up successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cleanup failed: " + e.getMessage());
        }
        return "redirect:/analytics/report-history";
    }


    @GetMapping("/search")
    public String searchReports(@RequestParam String query,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        try {
            Page<CustomReport> pageObj = reportHistoryService.searchReports(query, page, size);

            model.addAttribute("reports", pageObj != null ? pageObj.getContent() : Collections.emptyList());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pageObj != null ? pageObj.getTotalPages() : 1);
            model.addAttribute("totalItems", pageObj != null ? pageObj.getTotalElements() : 0);

            model.addAttribute("reportStats", reportHistoryService.getReportStatistics());
            model.addAttribute("searchQuery", query);
            model.addAttribute("success", model.getAttribute("success"));
            model.addAttribute("error", model.getAttribute("error"));

        } catch (Exception e) {
            model.addAttribute("reports", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalItems", 0);
            model.addAttribute("reportStats", Collections.emptyMap());
            model.addAttribute("searchQuery", query);
            model.addAttribute("error", "Search failed: " + e.getMessage());
        }
        return "analytics/report-history-list";
    }


    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<byte[]> downloadSavedReportPdf(@PathVariable Long id) {
        try {
            CustomReport report = reportHistoryService.findById(id);
            byte[] pdfBytes = reportHistoryService.generatePdfFromSavedReport(report);

            String filename = (report.getType() != null ? report.getType() : "report")
                    + "-report-" + report.getId() + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{id}/duplicate")
    public String duplicateReport(@PathVariable Long id,
                                  @RequestParam(required = false) String newTitle,
                                  RedirectAttributes redirectAttributes) {
        try {
            CustomReport duplicated = reportHistoryService.duplicateReport(id, newTitle);
            redirectAttributes.addFlashAttribute("success", "Report duplicated successfully");
            return "redirect:/analytics/report-history/" + duplicated.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to duplicate report: " + e.getMessage());
            return "redirect:/analytics/report-history/" + id;
        }
    }
}
