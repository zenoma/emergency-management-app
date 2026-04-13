package es.udc.emergencyproject.backend.model.entities.resource;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "created_at", nullable = false)
  protected LocalDateTime createdAt;

  @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "organization_id", nullable = false)
  protected Organization organization;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  protected Quadrant quadrant;

  @Column(name = "deploy_at")
  protected LocalDateTime deployAt;

  @Column(name = "dismantle_at")
  protected LocalDateTime dismantleAt;

  @Column(name = "removed", nullable = false)
  protected Boolean removed = Boolean.FALSE;

  @Column(name = "status")
  protected String status;
}
