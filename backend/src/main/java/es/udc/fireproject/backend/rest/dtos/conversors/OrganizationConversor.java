package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.rest.dtos.CoordinatesDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationRequestDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationResponseDto;
import es.udc.fireproject.backend.rest.dtos.OrganizationUpdateRequestDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class OrganizationConversor {


  private OrganizationConversor() {

  }

  public static Organization toOrganization(OrganizationResponseDto organizationResponseDto) {
    OrganizationType organizationType = new OrganizationType(organizationResponseDto.getOrganizationTypeId(), "");
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate;
    coordinate = new Coordinate(organizationResponseDto.getCoordinates().getLat(),
        organizationResponseDto.getCoordinates().getLon());

    return new Organization(organizationResponseDto.getCode(),
        organizationResponseDto.getName(),
        organizationResponseDto.getHeadquartersAddress(),
        geometryFactory.createPoint(coordinate),
        organizationType);
  }


  public static OrganizationResponseDto toOrganizationResponseDto(Organization organization) {
    CoordinatesDto coordinatesDto = new CoordinatesDto();
    coordinatesDto.setLon(organization.getLocation().getX());
    coordinatesDto.setLat(organization.getLocation().getY());

    return new OrganizationResponseDto(
        organization.getId(),
        organization.getCode(),
        organization.getName(),
        organization.getHeadquartersAddress(),
        coordinatesDto,
        organization.getCreatedAt(),
        organization.getOrganizationType().getId(),
        organization.getOrganizationType().getName());

  }

  public static Organization toOrganization(OrganizationRequestDto organizationResponseDto) {
    OrganizationType organizationType = new OrganizationType(organizationResponseDto.getOrganizationTypeId(), "");
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(organizationResponseDto.getCoordinates().getLon(),
        organizationResponseDto.getCoordinates().getLat());

    return new Organization(organizationResponseDto.getCode(),
        organizationResponseDto.getName(),
        organizationResponseDto.getHeadquartersAddress(),
        geometryFactory.createPoint(coordinate),
        organizationType);
  }

  public static Organization toOrganization(OrganizationUpdateRequestDto organizationUpdateRequestDto) {
    OrganizationType organizationType = new OrganizationType(organizationUpdateRequestDto.getOrganizationTypeId(), "");

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

    Coordinate coordinate = new Coordinate(organizationUpdateRequestDto.getCoordinates().getLon(),
        organizationUpdateRequestDto.getCoordinates().getLat());

    return new Organization(organizationUpdateRequestDto.getCode(),
        organizationUpdateRequestDto.getName(),
        organizationUpdateRequestDto.getHeadquartersAddress(),
        geometryFactory.createPoint(coordinate),
        organizationType);
  }

}
