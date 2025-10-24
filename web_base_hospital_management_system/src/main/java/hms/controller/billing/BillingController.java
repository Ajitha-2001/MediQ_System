package hms.controller.billing;

import hms.dto.billing.BillDto;
import hms.dto.billing.CreateOrUpdateBillRequest;
import hms.mapper.BillingMapper;
import hms.entity.billing.Bill;
import hms.repository.patient.PatientRepositoryP;
import hms.service.billing.BillingService;
import hms.service.billing.InvoicePdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/billing")
@CrossOrigin(origins = "http://localhost:3000")
public class BillingController {

    private final BillingService billingService;
    private final PatientRepositoryP patientRepo;
    private final InvoicePdfService pdfService;

    public BillingController(BillingService billingService,
                             PatientRepositoryP patientRepo,
                             InvoicePdfService pdfService) {
        this.billingService = billingService;
        this.patientRepo = patientRepo;
        this.pdfService = pdfService;
    }

    // -------- WEB (Thymeleaf) --------

    @GetMapping
    public String list(Model model) {
        var bills = billingService.listAll().stream()
                .map(BillingMapper::toDto)
                .toList();
        model.addAttribute("bills", bills);
        return "billing/billing_list";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("total", billingService.total());
        model.addAttribute("paid", billingService.paid());
        model.addAttribute("unpaid", billingService.unpaid());
        model.addAttribute("partial", billingService.partial());

        var recent = billingService.recent().stream()
                .map(BillingMapper::toDto)
                .toList();
        model.addAttribute("recent", recent);
        return "billing/dashboard";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("patients", patientRepo.findAll());
        return "billing/billing_form";
    }

    @PostMapping("/add")
    public String addSubmit(
            @RequestParam("patient.id") Long patientId,
            @RequestParam("amount") Double amount,
            @RequestParam("paymentStatus") String paymentStatus,
            @RequestParam("dateIssued") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssued,
            @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(value = "items", required = false) String items,
            Model model) {
        try {
            if (patientId == null || amount == null || amount < 0 || paymentStatus == null || dateIssued == null) {
                throw new IllegalArgumentException("Required fields are missing or invalid.");
            }
            billingService.create(
                    patientId,
                    amount,
                    normalizeStatus(paymentStatus),
                    dateIssued,
                    items,
                    dueDate
            );
            return "redirect:/billing";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create bill: " + e.getMessage());
            model.addAttribute("bill", new Bill());
            model.addAttribute("patients", patientRepo.findAll());
            return "billing/billing_form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        try {
            var bill = BillingMapper.toDto(billingService.get(id));
            model.addAttribute("bill", bill);
            model.addAttribute("patients", patientRepo.findAll());
            return "billing/billing_edit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Bill not found.");
            return "redirect:/billing";
        }
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(
            @PathVariable("id") Long id,
            @RequestParam("patient.id") Long patientId,
            @RequestParam("amount") Double amount,
            @RequestParam("paymentStatus") String paymentStatus,
            @RequestParam("dateIssued") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssued,
            @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(value = "items", required = false) String items,
            Model model) {
        try {
            if (patientId == null || amount == null || amount < 0 || paymentStatus == null || dateIssued == null) {
                throw new IllegalArgumentException("Required fields are missing or invalid.");
            }
            billingService.update(
                    id, patientId, amount, normalizeStatus(paymentStatus),
                    dateIssued, items, dueDate
            );
            return "redirect:/billing";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update bill: " + e.getMessage());
            var bill = BillingMapper.toDto(billingService.get(id));
            model.addAttribute("bill", bill);
            model.addAttribute("patients", patientRepo.findAll());
            return "billing/billing_edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        try {
            billingService.delete(id);
        } catch (IllegalArgumentException ignored) {}
        return "redirect:/billing";
    }

    @GetMapping("/{id}/invoice")
    public String invoice(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("bill", BillingMapper.toDto(billingService.get(id)));
            return "billing/invoice";
        } catch (IllegalArgumentException e) {
            return "redirect:/billing";
        }
    }

    @GetMapping("/{id}/invoice/pdf")
    @ResponseBody
    public byte[] invoicePdf(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            Bill bill = billingService.get(id);
            byte[] pdf = pdfService.generateInvoicePdf(bill);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice-" + bill.getBillId() + ".pdf");
            response.setContentLength(pdf.length);
            return pdf;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new byte[0];
        }
    }

    private String normalizeStatus(String s) {
        if (s == null) return "Unpaid";
        return switch (s.trim().toLowerCase()) {
            case "paid" -> "Paid";
            case "partial" -> "Partial";
            default -> "Unpaid";
        };
    }

    // -------- JSON API (DTO-based) --------

    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BillDto>> getAllBills() {
        var list = billingService.listAll().stream()
                .map(BillingMapper::toDto)
                .toList();
        return ResponseEntity.ok(list); // 200 with [] if empty
    }

    @GetMapping(value = "/api/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BillDto> getBillById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(BillingMapper.toDto(billingService.get(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createBill(@RequestBody CreateOrUpdateBillRequest body) {
        try {
            if (body.getPatientId() == null || body.getAmount() == null || body.getAmount() < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "patientId and a non-negative amount are required"));
            }

            var saved = billingService.create(
                    body.getPatientId(),
                    body.getAmount(),
                    normalizeStatus(body.getPaymentStatus()),
                    body.getDateIssued() != null ? body.getDateIssued() : LocalDate.now(),
                    body.getItems(),
                    body.getDueDate()
            );

            BillDto dto = BillingMapper.toDto(saved);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/billing/api/" + saved.getBillId()));
            return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName(), "message", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping(value = "/api/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateBill(@PathVariable Long id, @RequestBody CreateOrUpdateBillRequest body) {
        try {
            if (body.getPatientId() == null || body.getAmount() == null || body.getAmount() < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "patientId and a non-negative amount are required"));
            }

            var updated = billingService.update(
                    id,
                    body.getPatientId(),
                    body.getAmount(),
                    normalizeStatus(body.getPaymentStatus()),
                    body.getDateIssued() != null ? body.getDateIssued() : LocalDate.now(),
                    body.getItems(),
                    body.getDueDate()
            );

            return ResponseEntity.ok(BillingMapper.toDto(updated));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName(), "message", String.valueOf(e.getMessage())));
        }
    }

    @DeleteMapping(value = "/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        try {
            billingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/api/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BillDto>> getBillsByStatus(@PathVariable String status) {
        var list = billingService.findByStatus(status).stream()
                .map(BillingMapper::toDto)
                .toList();
        return ResponseEntity.ok(list); // 200 with [] if empty
    }

    @GetMapping(value = "/api/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BillDto>> getRecentBills() {
        var list = billingService.recent().stream()
                .map(BillingMapper::toDto)
                .toList();
        return ResponseEntity.ok(list); // 200 with [] if empty
    }

    @GetMapping(value = "/api/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        var recent = billingService.recent().stream()
                .map(BillingMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "total", billingService.total(),
                "paid", billingService.paid(),
                "unpaid", billingService.unpaid(),
                "partial", billingService.partial(),
                "recent", recent
        ));
    }

    // -------- Supporting: Patients for dropdown --------

    @GetMapping(value = "/api/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity.BodyBuilder getAllPatients() {
        var list = patientRepo.findAll().stream()
                .map(p -> Map.of(
                        "id", p.getId(),               // JPA id
                        "patientId", p.getPatientId(), // domain id / code
                        "firstName", p.getFirstName(),
                        "lastName", p.getLastName() != null ? p.getLastName() : "",
                        "gender", p.getGender() != null ? p.getGender() : ""
                ))
                .toList();

        return ResponseEntity.ok(); // 200 with [] if none
    }

    // Debug
    @PostMapping(value = "/api/debug/test-bill", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> testConnection(@RequestBody Map<String, Object> payload) {
        return Map.of("status", "ok", "echo", payload, "timestamp", LocalDate.now().toString());
    }
}
