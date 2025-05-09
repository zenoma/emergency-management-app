package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.rest.dtos.OrganizationDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class OrganizationConversor {


    private OrganizationConversor() {

    }

    public static Organization toOrganization(OrganizationDto organizationDto) {
        OrganizationType organizationType = new OrganizationType(organizationDto.getOrganizationTypeId(), "");
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
        Coordinate coordinate = new Coordinate(organizationDto.getLat(), organizationDto.getLon());


        return new Organization(organizationDto.getCode(),
                organizationDto.getName(),
                organizationDto.getHeadquartersAddress(),
                geometryFactory.createPoint(coordinate),
                organizationType);
    }


    public static OrganizationDto toOrganizationDto(Organization organization) {
        return new OrganizationDto(organization.getId(),
                organization.getCode(),
                organization.getName(),
                organization.getHeadquartersAddress(),
                organization.getLocation().getX(),
                organization.getLocation().getY(),
                organization.getCreatedAt(),
                organization.getOrganizationType().getName());

    }
}
