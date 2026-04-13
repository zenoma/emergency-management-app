package es.udc.emergencyproject.backend.utils;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;

public class VehicleOM {

  public static Vehicle withDefaultValues() {
    Organization organization = OrganizationOM.withDefaultValues();
    String vehiclePlate = "12345ABC";
    String type = "Car";
    return new Vehicle(vehiclePlate, type, organization);
  }


}

