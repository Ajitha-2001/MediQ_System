package hms.mapper;

import hms.dto.billing.BillDto;
import hms.entity.billing.Bill;
import hms.entity.patient.PatientP;

public final class BillingMapper {

    private BillingMapper() {}

    public static BillDto toDto(Bill b) {
        if (b == null) return null;

        PatientP p = b.getPatient();
        Long patientId = null;
        String patientCode = null;
        String first = null;
        String last  = null;

        if (p != null) {
            // These reads should be safe for views; controller uses DTOs only
            patientId  = p.getId();
            patientCode = String.valueOf(p.getPatientId());        // domain code
            first = p.getFirstName();
            last  = p.getLastName();
        }

        String full = ((first != null) ? first : "") +
                ((first != null && last != null) ? " " : "") +
                ((last  != null) ? last  : "");

        return new BillDto(
                b.getBillId(),
                b.getAmount(),
                b.getPaymentStatus(),
                b.getDateIssued(),
                b.getDueDate(),
                b.getItems(),
                patientId,
                patientCode,
                first,
                last,
                full.isBlank() ? null : full
        );
    }
}
