package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.rest.dtos.OrganizationResponseDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class OrganizationConversor {


  private OrganizationConversor() {

  }

  public static Organization toOrganization(OrganizationResponseDto organizationResponseDto) {
    OrganizationType organizationType = new OrganizationType(organizationResponseDto.getOrganizationTypeId(), "");
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(organizationResponseDto.getLat(), organizationResponseDto.getLon());

    return new Organization(organizationResponseDto.getCode(),
        organizationResponseDto.getName(),
        organizationResponseDto.getHeadquartersAddress(),
        geometryFactory.createPoint(coordinate),
        organizationType);
  }


  public static OrganizationResponseDto toOrganizationDto(Organization organization) {
    return new OrganizationResponseDto(
        organization.getId(),
        organization.getCode(),
        organization.getName(),
        organization.getHeadquartersAddress(),
        organization.getLocation().getX(),
        organization.getLocation().getY(),
        organization.getCreatedAt(),
        organization.getOrganizationType().getId(),
        organization.getOrganizationType().getName());

  }
}
