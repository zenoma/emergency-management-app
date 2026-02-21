package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.rest.dtos.LinkFireRequestDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantLocationDto;
import es.udc.fireproject.backend.rest.dtos.mappers.QuadrantMapper;
import es.udc.fireproject.backend.rest.dtos.mappers.QuadrantInfoMapper;
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

  private final FireManagementService fireManagementService;

  @Override
  public ResponseEntity<List<QuadrantDto>> getQuadrants(String scale) {

    List<QuadrantDto> quadrantDtos = new ArrayList<>();

    if (scale != null) {
      for (Quadrant quadrant : fireManagementService.findQuadrantsByEscala(scale)) {
        quadrantDtos.add(QuadrantMapper.toQuadrantDto(quadrant));
      }
    } else {
      for (Quadrant quadrant : fireManagementService.findAllQuadrants()) {
        quadrantDtos.add(QuadrantMapper.toQuadrantDto(quadrant));
      }
    }
    return ResponseEntity.ok(quadrantDtos);
  }

  @Override
  public ResponseEntity<QuadrantInfoDto> getQuadrantById(Integer id) {
    final QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDto(
        fireManagementService.findQuadrantById(id));

    return ResponseEntity.ok(quadrantInfoDto);
  }


  @Override
  public ResponseEntity<List<QuadrantInfoDto>> getQuadrantsWithActiveFire() {
    List<QuadrantInfoDto> quadrantInfoDtos = new ArrayList<>();

    for (Quadrant quadrant : fireManagementService.findQuadrantsWithActiveFire()) {
      quadrantInfoDtos.add(QuadrantInfoMapper.toQuadrantDto(quadrant));
    }

    return ResponseEntity.ok(quadrantInfoDtos);
  }

  @Override
  public ResponseEntity<QuadrantInfoDto> postQuadrantLinkFire(Integer id, LinkFireRequestDto linkFireRequestDto) {

    Quadrant quadrant = fireManagementService.linkFire(id, linkFireRequestDto.getFireId());

    QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(quadrant);

    return ResponseEntity.ok(quadrantInfoDto);
  }

  @Override
  public ResponseEntity<QuadrantLocationDto> getQuadrantByCoordinates(Double lon, Double lat) {

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(lon, lat);

    Optional<Quadrant> quadrant = fireManagementService.findQuadrantByLocation(
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
