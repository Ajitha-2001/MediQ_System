package hms.service.billing;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import hms.entity.billing.Bill;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Service
public class InvoicePdfService {

    private final TemplateEngine templateEngine;

    public InvoicePdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Generates the invoice PDF bytes from the Thymeleaf template.
     * Note:
     * - We set a baseUrl so relative URLs in the HTML can be resolved.
     * - Avoid external CDNs in your invoice template for PDF (use inline styles or local assets).
     */
    public byte[] generateInvoicePdf(Bill bill) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Prepare Thymeleaf context
            Context ctx = new Context();
            ctx.setVariable("bill", bill);

            // Render the HTML from the Thymeleaf template.
            // If your "billing/invoice.html" uses external CDNs, consider creating a lighter
            // "billing/invoice-pdf.html" version with inline CSS or local assets.
            String html = templateEngine.process("billing/invoice", ctx);

            // Establish a base URL so relative paths in the template can be resolved.
            // This points at /templates/ inside the classpath.
            URL base = new ClassPathResource("templates/").getURL();
            String baseUrl = base.toExternalForm(); // e.g. jar:file:.../templates/

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, baseUrl);

            // Optional: register a font that has wide glyph coverage (include the TTF in resources/fonts)
            // If you don't have the font, this try-block is silently skipped.
            try {
                ClassPathResource fontRes = new ClassPathResource("fonts/DejaVuSans.ttf");
                if (fontRes.exists()) {
                    try (InputStream fontStream = fontRes.getInputStream()) {
                        builder.useFont(() -> fontStream, "DejaVuSans");
                    }
                    // Tell the renderer this is the default font family used by your template (optional)
                    builder.useDefaultPageSize(8.27f, 11.69f, PdfRendererBuilder.PageSizeUnits.INCHES); // A4 (optional)
                }
            } catch (Exception ignored) {
                // No font available; renderer will use system defaults
            }

            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed generating invoice PDF: " + e.getMessage(), e);
        }
    }
}
