package pl.coderslab.charityApp.user;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.charityApp.security.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmailIgnoringCase(String email);

    List<User> findAllByRoles(Role role);

    boolean existsByEmail(String email);
    Optional<User> findFirstByUuid(String uuid);
}
