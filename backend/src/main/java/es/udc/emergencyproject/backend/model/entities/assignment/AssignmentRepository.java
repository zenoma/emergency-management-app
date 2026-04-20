package es.udc.emergencyproject.backend.model.entities.assignment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {


  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.resource.id = :resourceId and a.removed = false "
      + "and (a.status is null or a.status <> 'COMPLETED')")
  List<Assignment> findByResourceId(@Param("resourceId") Long resourceId);

  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.emergency.id = :emergencyId and a.removed = false "
      + "and (a.status is null or a.status <> 'COMPLETED')")
  List<Assignment> findByEmergencyId(@Param("emergencyId") Long emergencyId);

  @Query("select a from Assignment a "
      + "left join fetch a.resource r "
      + "left join fetch a.emergency e "
      + "left join fetch a.emergencyQuadrant eq "
      + "left join fetch eq.quadrant q "
      + "where a.emergencyQuadrant is not null and a.emergencyQuadrant.quadrant.id = :quadrantId and a.removed = false "
      + "and (a.status is null or a.status <> 'COMPLETED')")
  List<Assignment> findByEmergencyQuadrantQuadrantId(@Param("quadrantId") Integer quadrantId);

  @Query(value = "select a.* from assignment a "
      + "join emergency_quadrant eq on a.emergency_quadrant_id = eq.id "
      + "where eq.quadrant_gid = :quadrantGid and a.removed = false",
      nativeQuery = true)
  List<Assignment> findByQuadrantGid(@Param("quadrantGid") Integer quadrantGid);

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
      + "where a.removed = false "
      + "and (:quadrantId is null or q.id = :quadrantId) "
      + "and (:emergencyId is null or e.id = :emergencyId) "
      + "and (:resourceId is null or r.id = :resourceId)")
  List<Assignment> findByFilters(@Param("quadrantId") Integer quadrantId,
      @Param("emergencyId") Long emergencyId, @Param("resourceId") Long resourceId);


}
