package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class OrganizationOM {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  public static Organization withDefaultValues() {

    return new Organization("ORG-01",
        "Centro de Coordinación Central",
        "Calle alguna", geometryFactory.createPoint(new Coordinate(-45, 45)),
        OrganizationTypeOM.withDefaultValues());
  }

  public static Organization withOrganizationTypeAndRandomNames(String name) {
    String randomString = usingUUID();
    return new Organization(randomString.substring(0, 4),
        randomString,
        "Calle alguna",
        geometryFactory.createPoint(new Coordinate(-45, 45)),
        OrganizationTypeOM.withNames(List.of(name)).stream().findFirst()
            .orElse(OrganizationTypeOM.withDefaultValues()));
  }

  public static Organization withInvalidValues() {
    return new Organization("",
        "",
        "", null,
        OrganizationTypeOM.withInvalidName());
  }

  public static List<Organization> withNames(List<String> names) {
    final GeometryFactory geoFactory = new GeometryFactory();
    final List<Organization> result = new ArrayList<>();
    Organization organization;
    int count = 0;

    for (String name : names) {
      organization = new Organization(name.substring(0, 3).toUpperCase() + "-" + count,
          name,
          "Calle alguna", geoFactory.createPoint(new Coordinate(-45, 45)),
          OrganizationTypeOM.withDefaultValues());
      result.add(organization);
      count++;
    }
    return result;

  }

  private static String usingUUID() {
    UUID randomUUID = UUID.randomUUID();
    return randomUUID.toString().replaceAll("-", "");
  }

}