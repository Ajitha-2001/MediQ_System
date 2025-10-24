package hms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class StaffForm {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;


    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;


    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9 .()\\-]{7,20}$",
            message = "Enter a valid phone number"
    )
    private String phoneNumber;

    private boolean enabled = true;


    private Set<String> roles;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.trim(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? null : password.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName == null ? null : firstName.trim(); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName == null ? null : lastName.trim(); }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim(); }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
