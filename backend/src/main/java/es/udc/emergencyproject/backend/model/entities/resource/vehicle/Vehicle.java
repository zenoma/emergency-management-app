package es.udc.emergencyproject.backend.model.entities.resource.vehicle;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.resource.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Vehicle extends Resource {

  @Column(name = "vehicle_plate")
  @NotBlank
  private String vehiclePlate;

  @Column(name = "type")
  private String type;


  public Vehicle() {
  }

  public Vehicle(String vehiclePlate, String type) {
    this.vehiclePlate = vehiclePlate;
    this.type = type;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

  public Vehicle(String vehiclePlate, String type, Organization organization) {
    this.vehiclePlate = vehiclePlate;
    this.type = type;
    this.organization = organization;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}
