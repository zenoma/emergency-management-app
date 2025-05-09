package es.udc.fireproject.backend.model.entities.quadrant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuadrantRepository extends JpaRepository<Quadrant, Integer> {

  List<Quadrant> findByEscala(String escala);

  List<Quadrant> findByFireId(Long id);

  List<Quadrant> findByFireIdNotNull();

  @Query(value = "SELECT SUM(ST_Area(geom) / 10000) FROM quadrant WHERE id IN (:quadrantIds)", nativeQuery = true)
  Double findHectaresByQuadrantIds(@Param("quadrantIds") List<Integer> quadrantIds);
}
