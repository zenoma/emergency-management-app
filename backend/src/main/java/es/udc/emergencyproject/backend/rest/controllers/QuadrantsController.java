package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantLocationDto;
import es.udc.emergencyproject.backend.rest.mappers.QuadrantMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuadrantsController implements QuadrantsApi {

  private final EmergencyManagementService emergencyManagementService;

  @Override
  public ResponseEntity<List<QuadrantDto>> getQuadrants(String scale) {

    List<QuadrantDto> quadrantDtos = new ArrayList<>();

    if (scale != null) {
      for (Quadrant quadrant : emergencyManagementService.findQuadrantsByEscala(scale)) {
        quadrantDtos.add(QuadrantMapper.toQuadrantDto(quadrant));
      }
    } else {
      for (Quadrant quadrant : emergencyManagementService.findAllQuadrants()) {
        quadrantDtos.add(QuadrantMapper.toQuadrantDto(quadrant));
      }
    }
    return ResponseEntity.ok(quadrantDtos);
  }

  @Override
  public ResponseEntity<QuadrantDto> getQuadrantById(Integer id) {
    final QuadrantDto quadrantDto = QuadrantMapper.toQuadrantDto(
        emergencyManagementService.findQuadrantById(id));

    return ResponseEntity.ok(quadrantDto);
  }


  @Override
  public ResponseEntity<List<QuadrantDto>> getQuadrantsWithActiveEmergency() {
    List<QuadrantDto> quadrantDtos = new ArrayList<>();

    for (Quadrant quadrant : emergencyManagementService.findQuadrantsWithActiveEmergency()) {
      quadrantDtos.add(QuadrantMapper.toQuadrantDto(quadrant));
    }

    return ResponseEntity.ok(quadrantDtos);
  }


  @Override
  public ResponseEntity<QuadrantLocationDto> getQuadrantByCoordinates(Double lon, Double lat) {

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(lon, lat);

    Optional<Quadrant> quadrant = emergencyManagementService.findQuadrantByLocation(
        geometryFactory.createPoint(coordinate));

    if (quadrant.isPresent()) {
      QuadrantLocationDto dto = new QuadrantLocationDto();
      dto.setId(quadrant.get().getId());
      dto.setNombre(quadrant.get().getNombre());
      return ResponseEntity.ok(dto);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
