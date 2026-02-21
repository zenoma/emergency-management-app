package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.rest.dtos.FireRequestDto;
import es.udc.fireproject.backend.rest.dtos.FireResponseDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantFireRequestDto;
import es.udc.fireproject.backend.rest.dtos.mappers.FireMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequiredArgsConstructor
public class FiresController implements FiresApi {


  private final FireManagementService fireManagementService;


  @Override
  public ResponseEntity<List<FireResponseDto>> getAllFires() {

    List<FireResponseDto> fireResponseDtos = new ArrayList<>();

    for (Fire fire : fireManagementService.findAllFires()) {
      fireResponseDtos.add(FireMapper.toFireDto(fire));
    }
    return ResponseEntity.ok(fireResponseDtos);
  }


  @Override
  public ResponseEntity<FireResponseDto> postFire(FireRequestDto fireRequestDto) {

    Fire fire = FireMapper.toFire(fireRequestDto);

    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(fire.getId()).toUri();

    return ResponseEntity.created(location).body(FireMapper.toFireDto(fire));

  }

  @Override
  public ResponseEntity<FireResponseDto> putFire(Long id, FireRequestDto fireRequestDto) {

    Fire fire = FireMapper.toFire(fireRequestDto);

    fire = fireManagementService.updateFire(id, fire.getDescription(), fire.getType(), fire.getFireIndex());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(fire.getId()).toUri();

    return ResponseEntity.created(location).body(FireMapper.toFireDto(fire));

  }

  @Override
  public ResponseEntity<FireResponseDto> getFireById(Long id) {
    final FireResponseDto fireResponseDto = FireMapper.toFireDto(fireManagementService.findFireById(id));
    return ResponseEntity.ok(fireResponseDto);
  }

  @PostMapping("/{id}/extinguishFire")
  public FireResponseDto extinguishFire(@RequestAttribute Long userId,
      @PathVariable Long id)

      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
    return FireMapper.toFireDto(fireManagementService.extinguishFire(id));
  }

  @Override
  public ResponseEntity<FireResponseDto> postExtinguishQuadrant(Long id,
      QuadrantFireRequestDto quadrantFireRequestDto) throws ExtinguishedFireException, AlreadyDismantledException {

    final FireResponseDto fireResponseDto = FireMapper.toFireDto(
        fireManagementService.extinguishQuadrantByFireId(id, quadrantFireRequestDto.getQuadrantId()));

    return ResponseEntity.ok(fireResponseDto);
  }

  @Override
  public ResponseEntity<FireResponseDto> postExtinguishFire(Long id) {

    final Fire fire = fireManagementService.extinguishFire(id);

    final FireResponseDto fireResponseDto = FireMapper.toFireDto(fire);

    return ResponseEntity.ok(fireResponseDto);
  }


}
