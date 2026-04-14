package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.image.Image;
import es.udc.emergencyproject.backend.rest.dtos.ImageDto;

public class ImageMapper {

  private ImageMapper() {

  }

  public static ImageDto toImageDto(Image image) {

    return new ImageDto(image.getId(), image.getName(), image.getCreatedAt());
  }

}
