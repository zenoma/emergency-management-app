package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import java.util.ArrayList;
import java.util.Arrays;

public class QuadrantMapper {

  private QuadrantMapper() {

  }

  public static QuadrantDto toQuadrantDto(Quadrant quadrant) {
    return new QuadrantDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(),
        quadrant.getFolla50(),
        quadrant.getFolla25(),
        quadrant.getFolla5(),
        new ArrayList(Arrays.asList(quadrant.getGeom().getCoordinates())));
  }
}
