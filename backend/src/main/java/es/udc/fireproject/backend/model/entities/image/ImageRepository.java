package es.udc.fireproject.backend.model.entities.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

  Image findByName(String name);
}
