package hms;

import hms.entity.Role;
import hms.entity.User;
import hms.repository.RoleRepository;
import hms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication(
        // Avoid bean name clashes in large projects
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableScheduling // Enables scheduled tasks if @Scheduled methods are used
public class HmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> seedRolesAndUsers(roleRepository, userRepository, passwordEncoder);
    }

    @Transactional
    void seedRolesAndUsers(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        // ------------------- Seed Roles -------------------
        List<String> roleNames = List.of(
                "ADMIN", "IT_ADMIN", "RECEPTIONIST", "DOCTOR",
                "NURSE", "PHARMACIST", "PATIENT", "STAFF"
        );

        for (String name : roleNames) {
            roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(new Role(name)));
        }

        // ------------------- Seed Users -------------------
        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "admin", "admin123", "admin@hospital.com", "System", "Administrator", "ADMIN");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "itadmin", "itadmin123", "itadmin@hospital.com", "IT", "Administrator", "IT_ADMIN");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "drsmith", "doc123", "drsmith@hospital.com", "John", "Smith", "DOCTOR");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "nina", "nurse123", "nina@hospital.com", "Nina", "Williams", "NURSE");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "rachel", "rcp123", "rachel@hospital.com", "Rachel", "Green", "RECEPTIONIST");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "phil", "pharm123", "phil@hospital.com", "Phil", "Johnson", "PHARMACIST");

        createUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                "patty", "patient123", "patty@patient.com", "Patty", "Anderson", "PATIENT");
    }

    private void createUserIfNotExists(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder,
                                       String username,
                                       String rawPassword,
                                       String email,
                                       String firstName,
                                       String lastName,
                                       String roleName) {

        if (userRepository.existsByUsername(username)) {
            return; // User already exists
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setRoles(roles);

        userRepository.save(user);
    }
}
