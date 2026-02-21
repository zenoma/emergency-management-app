package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.rest.dtos.ImageDto;

public class ImageMapper {

  private ImageMapper() {

  }

  public static ImageDto toImageDto(Image image) {

    return new ImageDto(image.getId(), image.getName(), image.getCreatedAt());
  }

}
