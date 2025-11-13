package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.rest.dtos.QuadrantDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.conversors.QuadrantConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.QuadrantInfoConversor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
        quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
      }
    } else {
      for (Quadrant quadrant : fireManagementService.findAllQuadrants()) {
        quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
      }
    }
    return ResponseEntity.ok(quadrantDtos);
  }

  @GetMapping("/{gid}")
  public QuadrantInfoDto findQuadrantById(@PathVariable Integer gid)
      throws InstanceNotFoundException {
    return QuadrantInfoConversor.toQuadrantDto(fireManagementService.findQuadrantById(gid));
  }


  @GetMapping("/active")
  public List<QuadrantDto> findQuadrantsWithActiveFire() {
    List<QuadrantDto> quadrantDtos = new ArrayList<>();

    for (Quadrant quadrant : fireManagementService.findQuadrantsWithActiveFire()) {
      quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
    }

    return quadrantDtos;
  }

  @PostMapping("/{gid}/linkFire")
  public void linkFire(@RequestAttribute Long userId, @PathVariable Integer gid,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException {

    fireManagementService.linkFire(gid, Long.valueOf(jsonParams.get("fireId")));
  }


}
