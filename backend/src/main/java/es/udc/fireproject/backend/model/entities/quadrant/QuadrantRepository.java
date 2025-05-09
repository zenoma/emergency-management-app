package es.udc.fireproject.backend.model.entities.quadrant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuadrantRepository extends JpaRepository<Quadrant, Integer> {

    List<Quadrant> findByEscala(String escala);

    List<Quadrant> findByFireId(Long id);

    List<Quadrant> findByFireIdNotNull();

    @Query("SELECT SUM(ST_Area(geom) / 10000) FROM Quadrant q WHERE q.id IN :quadrantIds")
    Double findHectaresByQuadrantIds(@Param("quadrantIds") List<Integer> quadrantIds);
}
