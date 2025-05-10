package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;

public class VehicleOM {

  public static Vehicle withDefaultValues() {
    Organization organization = OrganizationOM.withDefaultValues();
    String vehiclePlate = "12345ABC";
    String type = "Car";
    return new Vehicle(vehiclePlate, type, organization);
  }


}

