package es.udc.fireproject.backend.model.entities.vehicle;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "vehicle")
public class Vehicle extends BaseEntity {

  private static final long serialVersionUID = -6662567578161123656L;

  @Column(name = "vehicle_plate")
  @NotBlank
  private String vehiclePlate;

  @Column(name = "type")
  private String type;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @ManyToOne(optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;

  @Column(name = "deploy_at")
  private LocalDateTime deployAt;

  @Column(name = "dismantle_at")
  private LocalDateTime dismantleAt;


  public Vehicle() {
  }

  public Vehicle(String vehiclePlate, String type, Organization organization) {
    this.vehiclePlate = vehiclePlate;
    this.type = type;
    this.organization = organization;
    this.createdAt = LocalDateTime.now();
  }

  public String getVehiclePlate() {
    return vehiclePlate;
  }

  public void setVehiclePlate(String vehiclePlate) {
    this.vehiclePlate = vehiclePlate;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public Quadrant getQuadrant() {
    return quadrant;
  }

  public void setQuadrant(Quadrant quadrant) {
    this.quadrant = quadrant;
  }

  public LocalDateTime getDeployAt() {
    return deployAt;
  }

  public void setDeployAt(LocalDateTime deployAt) {
    this.deployAt = deployAt;
  }

  public LocalDateTime getDismantleAt() {
    return dismantleAt;
  }

  public void setDismantleAt(LocalDateTime dismantleAt) {
    this.dismantleAt = dismantleAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vehicle vehicle = (Vehicle) o;
    return Objects.equals(vehiclePlate, vehicle.vehiclePlate) && Objects.equals(type, vehicle.type) && Objects.equals(
        createdAt, vehicle.createdAt) && Objects.equals(organization, vehicle.organization) && Objects.equals(quadrant,
        vehicle.quadrant) && Objects.equals(deployAt, vehicle.deployAt) && Objects.equals(dismantleAt,
        vehicle.dismantleAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vehiclePlate, type, createdAt, organization, quadrant, deployAt, dismantleAt);
  }

  @Override
  public String toString() {
    return "Vehicle{" +
        "vehiclePlate='" + vehiclePlate + '\'' +
        ", type='" + type + '\'' +
        ", organization=" + organization +
        ", quadrant=" + quadrant +
        ", deployAt=" + deployAt +
        ", dismantleAt=" + dismantleAt +
        '}';
  }
}