package es.udc.emergencyproject.backend.utils;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;

public class EmergencyOM {

  public static Emergency withDefaultValues() {

    Emergency e = new Emergency();
    e.setDescription("Description");
    e.setType("Type 1");
    e.setEmergencyIndex(EmergencyIndex.UNO);
    return e;
  }
}
