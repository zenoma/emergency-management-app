package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.rest.dtos.OrganizationTypeRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.OrganizationTypeResponseDto;
import es.udc.emergencyproject.backend.rest.mappers.OrganizationTypeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationsTypeController implements OrganizationTypesApi {

  private final PersonalManagementFacade personalManagementFacade;

  @Override
  public ResponseEntity<List<OrganizationTypeResponseDto>> getOrganizationsType() {

    List<OrganizationType> organizationTypes = personalManagementFacade.findAllOrganizationTypes();

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDtoList(organizationTypes));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> getOrganizationById(Long id) {
    OrganizationType organizationType = personalManagementFacade.findOrganizationTypeById(id);

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDto(organizationType));
  }

  @Override
  public ResponseEntity<OrganizationTypeResponseDto> postOrganization(
      OrganizationTypeRequestDto organizationTypeRequestDto) {

    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        organizationTypeRequestDto.getName());

    return ResponseEntity.ok(OrganizationTypeMapper.toOrganizationTypeDto(organizationType));
  }
}
