package hms.service;

import hms.dto.AdminRegistrationDto;
import hms.dto.RegistrationDto;
import hms.dto.UserRegistrationDto;
import hms.entity.Role;
import hms.entity.User;
import hms.repository.RoleRepository;
import hms.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final MailService mail;

    public UserService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, @Nullable MailService mail) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.mail = mail;
    }


    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private void ensureUnique(User u) {
        users.findByUsername(u.getUsername()).ifPresent(x -> { throw new IllegalArgumentException("Username already exists"); });
        users.findByEmail(u.getEmail()).ifPresent(x -> { throw new IllegalArgumentException("Email already exists"); });
    }

    private void sendWelcome(User saved, String rawPasswordOrNote) {
        String name = (saved.getFirstName() == null ? "" : saved.getFirstName() + " ") +
                (saved.getLastName() == null ? "" : saved.getLastName());
        try {
            if (mail != null) {
                mail.sendCredentials(saved.getEmail(), name.trim(), saved.getUsername(), rawPasswordOrNote);
            } else {
                log.info("[DEV MODE] Would email {}: user={} pw={}", saved.getEmail(), saved.getUsername(), rawPasswordOrNote);
            }
        } catch (Exception ex) {
            log.warn("Email send failed (non-fatal): {}", ex.getMessage());
        }
    }




    public boolean hasAnyAdmin() {
        return users.countByRoles_Name("ADMIN") > 0;
    }


    @Transactional
    public void bootstrapFirstAdmin(@Valid AdminRegistrationDto form) {
        if (hasAnyAdmin()) {
            throw new IllegalStateException("Admin already exists.");
        }

        Optional<User> byUsername = users.findByUsername(form.getUsername());
        Optional<User> byEmail = users.findByEmail(form.getEmail());

        User target;
        if (byUsername.isPresent() && byEmail.isPresent()) {
            if (!byUsername.get().getId().equals(byEmail.get().getId())) {
                throw new IllegalArgumentException("Username and Email belong to different accounts. Use a unique combination.");
            }
            target = byUsername.get();
        } else if (byUsername.isPresent()) {
            target = byUsername.get();
        } else if (byEmail.isPresent()) {
            target = byEmail.get();
        } else {
            target = new User();
        }

        Role admin = roles.findByName("ADMIN")
                .orElseThrow(() -> new NoSuchElementException("Role ADMIN missing"));

        target.setUsername(form.getUsername());
        target.setEmail(form.getEmail());
        target.setFirstName(form.getFirstName());
        target.setLastName(form.getLastName());
        target.setEnabled(true); // ✅ must be enabled to login
        target.setPassword(encoder.encode(form.getPassword()));

        Set<Role> rs = target.getRoles() == null ? new java.util.HashSet<>() : new java.util.HashSet<>(target.getRoles());
        rs.add(admin);
        target.setRoles(rs);

        users.save(target);
        sendWelcome(target, "(use the password you set)");
    }

    // ----------------- Admin / Staff / Patient creators -----------------

    @Transactional
    public User createAdmin(User form) {
        ensureUnique(form);
        Role admin = roles.findByName("ADMIN").orElseThrow(() -> new IllegalStateException("ADMIN role missing"));
        String raw = generateTempPassword();
        form.setPassword(encoder.encode(raw));
        form.setRoles(Set.of(admin));
        form.setEnabled(true); // ✅
        User saved = users.save(form);
        sendWelcome(saved, raw);
        return saved;
    }

    @Transactional
    public User createStaff(User form) {
        ensureUnique(form);
        Role role = roles.findByName("RECEPTIONIST")
                .orElseThrow(() -> new IllegalStateException("RECEPTIONIST role missing"));
        String raw = generateTempPassword();
        form.setPassword(encoder.encode(raw));
        form.setRoles(Set.of(role));
        form.setEnabled(true); // ✅
        User saved = users.save(form);
        sendWelcome(saved, raw);
        return saved;
    }

    @Transactional
    public User createPatient(User form) {
        ensureUnique(form);
        Role patient = roles.findByName("PATIENT").orElseThrow(() -> new IllegalStateException("PATIENT role missing"));
        String raw = generateTempPassword();
        form.setPassword(encoder.encode(raw));
        form.setRoles(Set.of(patient));
        form.setEnabled(true); // ✅
        User saved = users.save(form);
        sendWelcome(saved, raw);
        return saved;
    }

    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) return false;
        return users.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) return false;
        return users.findByEmail(email).isPresent();
    }

    // ----------------- Public self-registration (always PATIENT) -----------------

    @Transactional
    public void register(@Valid RegistrationDto dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getContactNumber());

        ensureUnique(u);

        Role patient = roles.findByName("PATIENT")
                .orElseThrow(() -> new IllegalStateException("PATIENT role missing"));

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? dto.getPassword()
                : generateTempPassword();

        u.setPassword(encoder.encode(rawPassword));
        u.setRoles(Set.of(patient));
        u.setEnabled(true); // ✅ allow login

        User saved = users.save(u);

        String emailPasswordLine = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? "(use the password you set during registration)"
                : rawPassword;

        sendWelcome(saved, emailPasswordLine);
    }

    @Transactional
    public void registerNewUser(@Valid UserRegistrationDto registrationDto) {
        User u = new User();
        u.setUsername(registrationDto.getUsername());
        u.setFirstName(registrationDto.getFirstName());
        u.setLastName(registrationDto.getLastName());
        u.setEmail(registrationDto.getEmail());
        u.setPhone(registrationDto.getPhone());

        ensureUnique(u);

        Role patient = roles.findByName("PATIENT")
                .orElseThrow(() -> new IllegalStateException("PATIENT role missing"));

        String rawPassword;
        if (registrationDto.getPassword() != null && !registrationDto.getPassword().isBlank()) {
            rawPassword = registrationDto.getPassword();
            u.setPassword(encoder.encode(rawPassword));
        } else {
            rawPassword = generateTempPassword();
            u.setPassword(encoder.encode(rawPassword));
        }

        u.setRoles(Set.of(patient));
        u.setEnabled(true); // ✅ allow login

        User saved = users.save(u);

        String emailPasswordLine = (registrationDto.getPassword() != null && !registrationDto.getPassword().isBlank())
                ? "(use the password you set during registration)"
                : rawPassword;

        sendWelcome(saved, emailPasswordLine);
    }
}
