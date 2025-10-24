package hms.entity.prescription;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctors")
public class DoctorPP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber;   // unique license number

    @Column(nullable = false, length = 100)
    private String fullName;        // e.g. "Dr. John Smith"

    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;           // up to 15 chars

    @Column(length = 100)
    private String specialization;

    private String photoPath;       // store relative path (uploads/xxxx.jpg)

    // Default constructor
    public DoctorPP() {}

    // All-args constructor
    public DoctorPP(Long id, String licenseNumber, String fullName, String gender, LocalDate dateOfBirth, Integer age, String email, String phone, String specialization, String photoPath) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.photoPath = photoPath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
