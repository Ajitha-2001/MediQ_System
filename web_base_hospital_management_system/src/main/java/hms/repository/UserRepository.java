package hms.repository;

import hms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    long countByRoles_Name(String admin);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("""
           select u from User u
           where lower(u.username) like :q
              or lower(u.email) like :q
              or lower(coalesce(u.firstName,'')) like :q
              or lower(coalesce(u.lastName,'')) like :q
           order by u.id desc
           """)
    List<User> searchByUsernameEmailOrName(@Param("q") String q);
}
