package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;

import java.util.ArrayList;
import java.util.List;

public class OrganizationTypeOM {
    public static OrganizationType withDefaultValues() {
        return new OrganizationType("Dummy Type Name");
    }

    public static OrganizationType withInvalidName() {
        OrganizationType organizationType = withDefaultValues();
        organizationType.setName(null);
        return organizationType;
    }

    public static List<OrganizationType> withNames(List<String> names) {
        final List<OrganizationType> result = new ArrayList<>();
        OrganizationType organization;
        int count = 0;

        for (String name : names) {
            organization = new OrganizationType(name);
            result.add(organization);
            count++;
        }
        return result;

    }
}
