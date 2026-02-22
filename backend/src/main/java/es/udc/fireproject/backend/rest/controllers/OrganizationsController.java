package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.exceptions.DomainException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonaManagementFacade;
import es.udc.fireproject.backend.rest.dtos.OrganizationRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationResponseDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationUpdateRequestDto;
import es.udc.fireproject.backend.rest.dtos.mappers.OrganizationMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationsController implements OrganizationsApi {

  private final PersonaManagementFacade personaManagementFacade;

  @Override
  public ResponseEntity<List<OrganizationResponseDto>> getOrganizations(String nameOrCode,
      String organizationTypeName) {

    List<OrganizationResponseDto> organizationResponseDtos = new ArrayList<>();
    if (nameOrCode != null) {
      for (Organization organization : personaManagementFacade.findOrganizationByNameOrCode(nameOrCode)) {
        organizationResponseDtos.add(OrganizationMapper.toOrganizationResponseDto(organization));
      }
    } else if (organizationTypeName != null) {
      for (Organization organization : personaManagementFacade.findOrganizationByOrganizationTypeName(
          organizationTypeName)) {
        organizationResponseDtos.add(OrganizationMapper.toOrganizationResponseDto(organization));
      }
    } else {
      for (Organization organization : personaManagementFacade.findAllOrganizations()) {
        organizationResponseDtos.add(OrganizationMapper.toOrganizationResponseDto(organization));
      }
    }
    return ResponseEntity.ok(organizationResponseDtos);
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> getOrganizationById(Long id) {

    final OrganizationResponseDto organizationResponseDto =
        OrganizationMapper.toOrganizationResponseDto(personaManagementFacade.findOrganizationById(id));

    return ResponseEntity.ok(organizationResponseDto);
  }

  @Override
  public ResponseEntity<Void> deleteByOrganizationId(Long id) {
    try {
      personaManagementFacade.deleteOrganizationById(id);
    } catch (DataIntegrityViolationException e) {
      throw new DomainException(e.getMessage(), String.valueOf(id));
    }

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> postOrganization(OrganizationRequestDto organizationRequestDto) {

    Organization organization = OrganizationMapper.toOrganization(organizationRequestDto);

    OrganizationType organizationType = personaManagementFacade.findOrganizationTypeById(
        organization.getOrganizationType().getId());
    organization.setOrganizationType(organizationType);
    organization = personaManagementFacade.createOrganization(organization);

    OrganizationResponseDto organizationResponseDto = OrganizationMapper.toOrganizationResponseDto(organization);
    return ResponseEntity.ok(organizationResponseDto);
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> putOrganizationById(Long id,
      OrganizationUpdateRequestDto organizationUpdateRequestDto) {

    Organization organization = OrganizationMapper.toOrganization(organizationUpdateRequestDto);

    final Organization updatedOrganization = personaManagementFacade.updateOrganization(id, organization.getName(),
        organization.getCode(),
        organization.getHeadquartersAddress(),
        organization.getLocation());

    return ResponseEntity.ok(OrganizationMapper.toOrganizationResponseDto(updatedOrganization));
  }


}
