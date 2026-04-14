package es.udc.emergencyproject.backend.utils;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;

public class TeamOM {

  public static Team withDefaultValues() {
    Organization organization = OrganizationOM.withDefaultValues();
    return new Team("TEAM-01", organization);
  }


}

