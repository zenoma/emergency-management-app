package es.udc.emergencyproject.backend.model.entities.assignment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

  List<Assignment> findByEmergencyQuadrantId(Long emergencyQuadrantId);

  List<Assignment> findByResourceId(Long resourceId);

  List<Assignment> findByEmergencyId(Long emergencyId);

  List<Assignment> findByEmergencyQuadrantQuadrantId(Integer quadrantId);

  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.id = :id")
  Optional<Assignment> findByIdWithRelations(@Param("id") Long id);

}
