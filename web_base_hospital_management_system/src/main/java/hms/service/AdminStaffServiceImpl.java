package hms.service;

import hms.dto.StaffForm;
import hms.entity.Role;
import hms.entity.User;
import hms.repository.RoleRepository;
import hms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional    //Observer Pattern
public class AdminStaffServiceImpl implements AdminStaffService {

    private static final Logger log = LoggerFactory.getLogger(AdminStaffServiceImpl.class);

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final MailService mail;

    public AdminStaffServiceImpl(UserRepository users,
                                 RoleRepository roles,
                                 PasswordEncoder encoder,
                                 MailService mail) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.mail = mail;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> listAll(String q) {
        String query = (q == null) ? "" : q.trim().toLowerCase();
        List<User> all = users.findAll();


        return all.stream()
                .filter(u -> u.getRoles() != null && u.getRoles().stream()
                        .anyMatch(r -> r != null && r.getName() != null && !r.getName().equalsIgnoreCase("PATIENT")))
                .filter(u -> query.isEmpty() ||
                        containsIgnoreCase(u.getUsername(), query) ||
                        containsIgnoreCase(u.getEmail(), query) ||
                        containsIgnoreCase(u.getFirstName(), query) ||
                        containsIgnoreCase(u.getLastName(), query) ||
                        containsIgnoreCase(u.getPhone(), query))
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    private boolean containsIgnoreCase(String s, String q) {
        return s != null && s.toLowerCase().contains(q);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return users.findById(id);
    }


    @Override
    public User create(StaffForm form) {
        validateCreate(form);

        if (users.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        final String rawPassword = form.getPassword();

        User u = new User();
        u.setUsername(form.getUsername());
        u.setPassword(encoder.encode(rawPassword));
        u.setEmail(form.getEmail());
        u.setFirstName(form.getFirstName());
        u.setLastName(form.getLastName());
        u.setPhone(form.getPhoneNumber());
        u.setEnabled(form.isEnabled());

        Set<Role> roleSet = fetchRoles(form.getRoles());
        u.setRoles(roleSet);

        User saved = users.save(u);


        try {
            String fullName = ((Objects.toString(saved.getFirstName(), "") + " " + Objects.toString(saved.getLastName(), "")).trim());
            if (fullName.isBlank()) fullName = saved.getUsername();
            mail.sendCredentials(saved.getEmail(), fullName, saved.getUsername(), rawPassword);
        } catch (Exception ex) {
            log.warn("Staff created but failed to send credentials email to {}: {}", saved.getEmail(), ex.getMessage());
        }

        return saved;
    }

    @Override
    public User update(Long id, StaffForm form) {
        User u = users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));


        if (!Objects.equals(u.getUsername(), form.getUsername()) && users.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!Objects.equals(u.getEmail(), form.getEmail()) && users.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!StringUtils.hasText(form.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(form.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }

        u.setUsername(form.getUsername());
        if (StringUtils.hasText(form.getPassword())) {
            u.setPassword(encoder.encode(form.getPassword()));
        }
        u.setEmail(form.getEmail());
        u.setFirstName(form.getFirstName());
        u.setLastName(form.getLastName());
        u.setPhone(form.getPhoneNumber()); 
        u.setEnabled(form.isEnabled());

        Set<Role> roleSet = fetchRoles(form.getRoles());
        u.setRoles(roleSet);

        return users.save(u);
    }

    @Override
    public void enable(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        u.setEnabled(true);
        users.save(u);
    }

    @Override
    public void disable(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        // Don't disable the last admin account
        if (isSoleAdmin(u)) {
            throw new IllegalStateException("Cannot disable the last ADMIN account.");
        }
        u.setEnabled(false);
        users.save(u);
    }

    @Override
    public void assignRole(Long userId, String roleName) {
        User u = users.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        Role r = roles.findByName(roleName).orElseThrow(() -> new NoSuchElementException("Role not found: " + roleName));
        Set<Role> set = u.getRoles() == null ? new HashSet<>() : new HashSet<>(u.getRoles());
        set.add(r);
        u.setRoles(set);
        users.save(u);
    }

    @Override
    public void removeRole(Long userId, String roleName) {
        User u = users.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        Role r = roles.findByName(roleName).orElseThrow(() -> new NoSuchElementException("Role not found: " + roleName));


        boolean removingAdmin = "ADMIN".equalsIgnoreCase(roleName) && u.getRoles().stream().anyMatch(x -> "ADMIN".equalsIgnoreCase(x.getName()));
        if (removingAdmin && countAdmins() <= 1) {
            throw new IllegalStateException("Cannot remove ADMIN role from the last admin.");
        }

        Set<Role> set = u.getRoles() == null ? new HashSet<>() : new HashSet<>(u.getRoles());
        set.removeIf(x -> Objects.equals(x.getId(), r.getId()) || roleName.equalsIgnoreCase(x.getName()));
        u.setRoles(set);
        users.save(u);
    }

    @Override
    public void delete(Long id, String currentUsername) {
        User u = users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

        if (u.getUsername() != null && currentUsername != null &&
                u.getUsername().equalsIgnoreCase(currentUsername)) {
            throw new IllegalStateException("You cannot delete your own account.");
        }

        if (isSoleAdmin(u)) {
            throw new IllegalStateException("Cannot delete the last ADMIN account.");
        }

        users.delete(u);
    }


    private void validateCreate(StaffForm form) {
        if (form == null) throw new IllegalArgumentException("Form is required");
        if (!StringUtils.hasText(form.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(form.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!StringUtils.hasText(form.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }

    }

    private Set<Role> fetchRoles(Set<String> names) {
        Set<String> requested = (names == null || names.isEmpty())
                ? new HashSet<>(Collections.singleton("RECEPTIONIST"))
                : names;

        return requested.stream()
                .map(n -> roles.findByName(n).orElseThrow(() -> new NoSuchElementException("Role not found: " + n)))
                .collect(Collectors.toSet());
    }

    private boolean isSoleAdmin(User u) {
        boolean isAdmin = u.getRoles() != null && u.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
        return isAdmin && countAdmins() <= 1;
    }

    private long countAdmins() {
        return users.countByRoles_Name("ADMIN");
    }
}
