package es.udc.emergencyproject.backend.model.entities.emergency;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyRepository extends JpaRepository<Emergency, Long> {

  List<Emergency> findAllByOrderByResolvedAtDescIdAsc();
}
