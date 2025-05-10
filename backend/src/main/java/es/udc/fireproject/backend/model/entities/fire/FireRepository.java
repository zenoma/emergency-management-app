package es.udc.fireproject.backend.model.entities.fire;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireRepository extends JpaRepository<Fire, Long> {

  List<Fire> findAllByOrderByExtinguishedAtDescIdAsc();
}
