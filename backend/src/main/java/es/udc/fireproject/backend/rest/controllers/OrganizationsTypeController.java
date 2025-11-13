package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeResponseDto;
import es.udc.fireproject.backend.rest.dtos.conversors.OrganizationTypeConversor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationsTypeController implements OrganizationTypesApi {

  private final PersonalManagementService personalManagementService;

  @Override
  public ResponseEntity<List<OrganizationTypeResponseDto>> getOrganizationsType() {

    List<OrganizationType> organizationTypes = personalManagementService.findAllOrganizationTypes();

    return ResponseEntity.ok(OrganizationTypeConversor.toOrganizationTypeDtoList(organizationTypes));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> getOrganizationById(Long id) {
    OrganizationType organizationType = personalManagementService.findOrganizationTypeById(id);

    return ResponseEntity.ok(OrganizationTypeConversor.toOrganizationTypeDto(organizationType));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> postOrganization(
      OrganizationTypeRequestDto organizationTypeRequestDto) {

    OrganizationType organizationType = personalManagementService.createOrganizationType(
        organizationTypeRequestDto.getName());

    return ResponseEntity.ok(OrganizationTypeConversor.toOrganizationTypeDto(organizationType));
  }
}
