package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeDto;

public class OrganizationTypeConversor {

    private OrganizationTypeConversor() {

    }

    public static OrganizationTypeDto toOrganizationTypeDto(OrganizationType organizationType) {
        return new OrganizationTypeDto(organizationType.getId(), organizationType.getName());
    }

    public static OrganizationType toOrganizationType(OrganizationTypeDto organizationDto) {
        return new OrganizationType(organizationDto.getName());
    }
}
