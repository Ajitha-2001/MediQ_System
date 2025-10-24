package hms.service;

import hms.dto.StaffForm;
import hms.entity.User;

import java.util.List;
import java.util.Optional;

public interface AdminStaffService {
    List<User> listAll(String q);
    Optional<User> findById(Long id);
    User create(StaffForm form);
    User update(Long id, StaffForm form);
    void enable(Long id);
    void disable(Long id);
    void assignRole(Long userId, String roleName);
    void removeRole(Long userId, String roleName);
    void delete(Long id, String currentUsername);
}
