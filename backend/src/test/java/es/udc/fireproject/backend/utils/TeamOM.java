package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.team.Team;

public class TeamOM {

    public static Team withDefaultValues() {
        Organization organization = OrganizationOM.withDefaultValues();
        return new Team("TEAM-01", organization);
    }


}

