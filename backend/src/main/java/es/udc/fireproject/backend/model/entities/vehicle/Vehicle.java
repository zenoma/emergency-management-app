package es.udc.fireproject.backend.model.entities.vehicle;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Vehicle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

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
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}