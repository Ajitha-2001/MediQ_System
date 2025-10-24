package hms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class RegistrationDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;


    @NotBlank(message = "Phone number is required")
    @Size(max = 30, message = "Phone number must be 30 characters or less")
    private String phoneNumber;


    @Size(max = 30, message = "Contact number must be 30 characters or less")
    private String contactNumber;



    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = trimToNull(username); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = trim(password); }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = trim(confirmPassword); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = trimToNull(email); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = trimToNull(firstName); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = trimToNull(lastName); }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = trim(phoneNumber); }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = trim(contactNumber); }


    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
