package hms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // Optional
    @Size(max = 30, message = "Contact number must be 30 characters or less")
    private String contactNumber;

    /**
     * Selected role from the form (e.g., ADMIN, DOCTOR, PATIENT, ...).
     * Field kept as "role"; aliases provided via getRoleName()/setRoleName().
     */
    private String role;

    public UserRegistrationDto() {}

    // -------- Getters & Setters (with safe trimming) --------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = trimToNull(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = trim(password); // allow spaces inside, just trim ends
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = trim(confirmPassword);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = trimToNull(email);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = trimToNull(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = trimToNull(lastName);
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = trim(contactNumber);
    }

    /** Primary role accessors */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = trimToNull(role);
    }

    /** Aliases so code expecting roleName still works */
    public String getRoleName() {
        return role;
    }

    public void setRoleName(String roleName) {
        this.role = trimToNull(roleName);
    }

    /** Convenience alias used by services (e.g., UserService.registerNewUser) */
    public String getPhone() {
        return contactNumber;
    }

    public void setPhone(String phone) {
        this.contactNumber = trim(phone);
    }


    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
