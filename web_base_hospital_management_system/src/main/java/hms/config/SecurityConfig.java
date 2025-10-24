
package hms.config;

import hms.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationSuccessHandler successHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          AuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/", "/index",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/assets/**",
                                "/login",
                                "/patient/login",
                                "/loginP",
                                "/getstart", "/get-start",
                                "/register", "/registration-success",
                                "/bootstrap-admin/**",
                                "/h2-console/**",
                                "/appointments/**",
                                "/appointments-home",
                                "/api/**",
                                "/billing/api/**"
                        ).permitAll()


                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/itadmin/**").hasRole("IT_ADMIN")
                        .requestMatchers("/receptionist/**").hasRole("RECEPTIONIST")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/nurse/**").hasRole("NURSE")
                        .requestMatchers("/patient/**").hasRole("PATIENT")


                        .requestMatchers("/medical/records/**").authenticated()


                        .anyRequest().authenticated()
                )


                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )


                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))


                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**",
                        "/api/**",
                        "/billing/api/**"
                ))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))


                .rememberMe(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }   // Factory Pattern

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
