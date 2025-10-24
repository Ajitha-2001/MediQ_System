package hms.controller.patient;

import hms.dto.patient.PatientCreateRequest;
import hms.entity.patient.PatientP;
import hms.service.patient.PatientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

  private final PatientService service;

  public PatientApiController(PatientService service) {
    this.service = service;
  }

  /** CREATE: POST /api/patients -> 201 + Location header */
  @PostMapping
  public ResponseEntity<PatientP> create(@Valid @RequestBody PatientCreateRequest req) {
    PatientP created = service.register(req, "api");
    return ResponseEntity
            .created(URI.create("/api/patients/" + created.getExternalId()))
            .body(created);
  }

  /** UPDATE: PUT /api/patients/{id} -> 200 */
  @PutMapping("/{id}")
  public PatientP update(@PathVariable String id, @Valid @RequestBody PatientCreateRequest req) {
    return service.update(id, req, "api");
  }

  /** DELETE: DELETE /api/patients/{id} -> 204 No Content */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.delete(id, "api");
    return ResponseEntity.noContent().build();
  }

  /** LIST/SEARCH: GET /api/patients */
  @GetMapping
  public Page<PatientP> search(@RequestParam(required = false) String q,
                               @RequestParam(required = false) String gender,
                               @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobFrom,
                               @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobTo,
                               @RequestParam(required = false) Set<String> tags,
                               @RequestParam(defaultValue = "0") int page) {
    return service.search(q, gender, dobFrom, dobTo, tags, PageRequest.of(page, 10));
  }

  /**
   * READ: GET /api/patients/{id}
   */
  @GetMapping("/{id}")
  public Optional<PatientP> get(@PathVariable String id) {
    return Optional.ofNullable(service.find(id).orElseThrow(() -> new EntityNotFoundException("Patient not found")));
  }

  /* ---------- Error mapping ---------- */

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
    return ResponseEntity.status(404).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", 404,
            "error", "Not Found",
            "message", ex.getMessage() == null ? "Resource not found" : ex.getMessage(),
            "path", req.getRequestURI()
    ));
  }
}
