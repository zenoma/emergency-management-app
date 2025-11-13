package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeResponseDto;
import java.util.ArrayList;
import java.util.List;

public class OrganizationTypeConversor {

  private OrganizationTypeConversor() {

  }

  public static OrganizationTypeResponseDto toOrganizationTypeDto(OrganizationType organizationType) {
    return new OrganizationTypeResponseDto(organizationType.getId(), organizationType.getName());
  }


  public static OrganizationType toOrganizationType(OrganizationTypeRequestDto organizationDto) {
    return new OrganizationType(organizationDto.getName());
  }


  public static List<OrganizationTypeResponseDto> toOrganizationTypeDtoList(List<OrganizationType> organizationTypes) {
    List<OrganizationTypeResponseDto> responseDtos = new ArrayList<>();
    for (OrganizationType organizationType : organizationTypes) {
      responseDtos.add(toOrganizationTypeDto(organizationType));
    }
    return responseDtos;
  }
}
