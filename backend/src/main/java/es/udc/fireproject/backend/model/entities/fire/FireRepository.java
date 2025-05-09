package es.udc.fireproject.backend.model.entities.fire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FireRepository extends JpaRepository<Fire, Long> {

    List<Fire> findAllByOrderByExtinguishedAtDescIdAsc();
}
