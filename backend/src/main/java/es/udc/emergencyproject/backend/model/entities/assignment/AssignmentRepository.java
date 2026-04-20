package es.udc.emergencyproject.backend.model.entities.assignment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

  @Query("select a from Assignment a "
      + "where a.emergencyQuadrant.id = :emergencyQuadrantId and a.removed = false")
  List<Assignment> findByEmergencyQuadrantId(@Param("emergencyQuadrantId") Long emergencyQuadrantId);

  @Query("select a from Assignment a "
      + "where a.resource.id = :resourceId and a.removed = false")
  List<Assignment> findByResourceId(@Param("resourceId") Long resourceId);

  @Query("select a from Assignment a "
      + "where a.emergency.id = :emergencyId and a.removed = false")
  List<Assignment> findByEmergencyId(@Param("emergencyId") Long emergencyId);

  @Query("select a from Assignment a "
      + "where a.emergencyQuadrant.quadrant.id = :quadrantId and a.removed = false")
  List<Assignment> findByEmergencyQuadrantQuadrantId(@Param("quadrantId") Integer quadrantId);

  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.id = :id and a.removed = false")
  Optional<Assignment> findByIdWithRelations(@Param("id") Long id);

  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.removed = false")
  List<Assignment> findAllWithRelations();

}
