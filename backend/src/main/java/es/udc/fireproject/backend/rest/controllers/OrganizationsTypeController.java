package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonaManagementFacade;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeResponseDto;
import es.udc.fireproject.backend.rest.dtos.mappers.OrganizationTypeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationsTypeController implements OrganizationTypesApi {

  private final PersonaManagementFacade personaManagementFacade;

  @Override
  public ResponseEntity<List<OrganizationTypeResponseDto>> getOrganizationsType() {

    List<OrganizationType> organizationTypes = personaManagementFacade.findAllOrganizationTypes();

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDtoList(organizationTypes));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> getOrganizationById(Long id) {
    OrganizationType organizationType = personaManagementFacade.findOrganizationTypeById(id);

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDto(organizationType));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> postOrganization(
      OrganizationTypeRequestDto organizationTypeRequestDto) {

    OrganizationType organizationType = personaManagementFacade.createOrganizationType(
        organizationTypeRequestDto.getName());

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDto(organizationType));
  }
}
