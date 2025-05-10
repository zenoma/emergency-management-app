package es.udc.fireproject.backend.model.entities.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByOrderByDniAsc();


  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

}
