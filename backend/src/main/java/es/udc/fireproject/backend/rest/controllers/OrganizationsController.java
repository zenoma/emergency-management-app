package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.OrganizationRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationResponseDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationUpdateRequestDto;
import es.udc.fireproject.backend.rest.dtos.conversors.OrganizationConversor;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationsController implements OrganizationsApi {

  private final PersonalManagementService personalManagementService;

  @Override
  public ResponseEntity<List<OrganizationResponseDto>> getOrganizations(String nameOrCode,
      String organizationTypeName) {

    List<OrganizationResponseDto> organizationResponseDtos = new ArrayList<>();
    if (nameOrCode != null) {
      for (Organization organization : personalManagementService.findOrganizationByNameOrCode(nameOrCode)) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationResponseDto(organization));
      }
    } else if (organizationTypeName != null) {
      for (Organization organization : personalManagementService.findOrganizationByOrganizationTypeName(
          organizationTypeName)) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationResponseDto(organization));
      }
    } else {
      for (Organization organization : personalManagementService.findAllOrganizations()) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationResponseDto(organization));
      }
    }
    return ResponseEntity.ok(organizationResponseDtos);
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> getOrganizationById(Long id) {

    final OrganizationResponseDto organizationResponseDto =
        OrganizationConversor.toOrganizationResponseDto(personalManagementService.findOrganizationById(id));

    return ResponseEntity.ok(organizationResponseDto);
  }

  @Override
  public ResponseEntity<Void> deleteByOrganizationId(Long id) {
    personalManagementService.deleteOrganizationById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> postOrganization(OrganizationRequestDto organizationRequestDto) {

    Organization organization = OrganizationConversor.toOrganization(organizationRequestDto);

    OrganizationType organizationType = personalManagementService.findOrganizationTypeById(
        organization.getOrganizationType().getId());
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    OrganizationResponseDto organizationResponseDto = OrganizationConversor.toOrganizationResponseDto(organization);
    return ResponseEntity.ok(organizationResponseDto);
  }

  @Override
  public ResponseEntity<OrganizationResponseDto> putOrganizationById(Long id,
      OrganizationUpdateRequestDto organizationUpdateRequestDto) {

    Organization organization = OrganizationConversor.toOrganization(organizationUpdateRequestDto);

    final Organization updatedOrganization = personalManagementService.updateOrganization(id, organization.getName(),
        organization.getCode(),
        organization.getHeadquartersAddress(),
        organization.getLocation());

    return ResponseEntity.ok(OrganizationConversor.toOrganizationResponseDto(updatedOrganization));
  }


}
