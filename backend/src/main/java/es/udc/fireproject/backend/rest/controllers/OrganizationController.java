package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.OrganizationResponseDto;
import es.udc.fireproject.backend.rest.dtos.conversors.OrganizationConversor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

  @Autowired
  private PersonalManagementService personalManagementService;


  @GetMapping("")
  public List<OrganizationResponseDto> findAll(@RequestAttribute Long userId,
      @RequestParam(required = false) String nameOrCode,
      @RequestParam(required = false) String organizationTypeName) {

    List<OrganizationResponseDto> organizationResponseDtos = new ArrayList<>();
    if (nameOrCode != null) {
      for (Organization organization : personalManagementService.findOrganizationByNameOrCode(nameOrCode)) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationDto(organization));
      }
    } else if (organizationTypeName != null) {
      for (Organization organization : personalManagementService.findOrganizationByOrganizationTypeName(
          organizationTypeName)) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationDto(organization));
      }
    } else {
      for (Organization organization : personalManagementService.findAllOrganizations()) {
        organizationResponseDtos.add(OrganizationConversor.toOrganizationDto(organization));
      }
    }
    return organizationResponseDtos;
  }

  @GetMapping("/{id}")
  public OrganizationResponseDto findById(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException {
    return OrganizationConversor.toOrganizationDto(personalManagementService.findOrganizationById(id));
  }


  @DeleteMapping("/{id}")
  public void deleteById(@RequestAttribute Long userId, @PathVariable Long id) {
    personalManagementService.deleteOrganizationById(id);
  }

  @PostMapping("")
  public OrganizationResponseDto create(@RequestAttribute Long userId,
      @RequestBody OrganizationResponseDto organizationResponseDto)
      throws InstanceNotFoundException {

    Organization organization = OrganizationConversor.toOrganization(organizationResponseDto);

    OrganizationType organizationType = personalManagementService.findOrganizationTypeById(
        organization.getOrganizationType().getId());
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    return OrganizationConversor.toOrganizationDto(organization);
  }

  @PutMapping("/{id}")
  public void update(@RequestAttribute Long userId,
      @RequestBody OrganizationResponseDto organizationResponseDto,
      @PathVariable Long id)
      throws InstanceNotFoundException {

    Organization organization = OrganizationConversor.toOrganization(organizationResponseDto);

    personalManagementService.updateOrganization(id, organization.getName(),
        organization.getCode(),
        organization.getHeadquartersAddress(),
        organization.getLocation());

  }


}
