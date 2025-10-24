package hms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String baseUrl;

    public MailService(@Nullable JavaMailSender mailSender,
                       @Value("${hms.base-url:#{null}}") String baseUrlFromProps) {
        this.mailSender = mailSender;
        String env = System.getenv("HMS_BASE_URL");
        this.baseUrl = (env != null && !env.isBlank())
                ? env
                : (baseUrlFromProps != null && !baseUrlFromProps.isBlank())
                ? baseUrlFromProps
                : "http://localhost:8080";
    }

    public void sendCredentials(String to, String fullName, String username, String rawPasswordOrNote) {
        String safeName = (fullName == null || fullName.isBlank()) ? "there" : fullName;
        String body = String.format("""
Hello %s,

Your account on HMS has been created.

Username: %s
Password: %s

Staff login:   %s/login
Patient login: %s/patientP/login

Please sign in and change your password after the first login.

— MediQ
""", safeName, username, rawPasswordOrNote, baseUrl, baseUrl);

        if (mailSender == null) {

            log.info("[DEV MODE] Would send email to {}:\nSubject: {}\n{}\n",
                    to, "Your HMS account credentials", body);
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Your HMS account credentials");
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception ex) {

            log.warn("Email send failed to {}: {}", to, ex.getMessage());
        }
    }
}
