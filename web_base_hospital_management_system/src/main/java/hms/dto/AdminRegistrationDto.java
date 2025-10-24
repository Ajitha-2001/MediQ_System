package hms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminRegistrationDto {

    @NotBlank @Size(min = 3, max = 50)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName == null ? null : firstName.trim(); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName == null ? null : lastName.trim(); }
}
