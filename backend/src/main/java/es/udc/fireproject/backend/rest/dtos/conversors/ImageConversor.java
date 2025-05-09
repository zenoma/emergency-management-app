package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.rest.dtos.ImageDto;

public class ImageConversor {

    private ImageConversor() {

    }

    public static ImageDto toImageDto(Image image) {

        return new ImageDto(image.getId(), image.getName(), image.getCreatedAt());
    }

}
