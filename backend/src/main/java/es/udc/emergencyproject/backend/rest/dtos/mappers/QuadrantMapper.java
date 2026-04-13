package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import java.util.ArrayList;
import java.util.Arrays;

public class QuadrantMapper {

  private QuadrantMapper() {

  }

  public static QuadrantDto toQuadrantDto(Quadrant quadrant) {
    Long fireId = null;
    if (quadrant.getEmergency() != null) {
      fireId = quadrant.getEmergency().getId();
    }
    return new QuadrantDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(),
        quadrant.getFolla50(),
        quadrant.getFolla25(),
        quadrant.getFolla5(),
        fireId,
        quadrant.getLinkedAt(),
        new ArrayList(Arrays.asList(quadrant.getGeom().getCoordinates())));
  }
}
