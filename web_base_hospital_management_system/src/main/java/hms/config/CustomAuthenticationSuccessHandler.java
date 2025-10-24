package hms.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final List<String> ORDER = List.of(
            "ROLE_ADMIN",
            "ROLE_IT_ADMIN",
            "ROLE_RECEPTIONIST",
            "ROLE_DOCTOR",
            "ROLE_NURSE",
            "ROLE_PATIENT"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .min(Comparator.comparingInt(a -> {
                    int idx = ORDER.indexOf(a);
                    return idx < 0 ? Integer.MAX_VALUE : idx;
                }))
                .orElse(null);

        String redirect = switch (role) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_IT_ADMIN" -> "/itadmin/dashboard";
            case "ROLE_RECEPTIONIST" -> "/receptionist/dashboard";
            case "ROLE_DOCTOR" -> "/doctor/dashboard";
            case "ROLE_NURSE" -> "/nurse/dashboard";
            case "ROLE_PATIENT" -> "/patient/dashboard";
            default -> "/";
        };
                                                                 //Strategy Pattern
        response.sendRedirect(redirect);
    }
}
