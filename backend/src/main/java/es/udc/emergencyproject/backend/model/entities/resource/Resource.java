package es.udc.emergencyproject.backend.model.entities.resource;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

  @Column(name = "deploy_at")
  protected LocalDateTime deployAt;

  @Column(name = "dismantle_at")
  protected LocalDateTime dismantleAt;

  @Column(name = "removed", nullable = false)
  protected Boolean removed = Boolean.FALSE;

  @Column(name = "dismantled", nullable = false)
  protected Boolean dismantled = Boolean.FALSE;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  protected ResourceStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "resource_type")
  protected ResourceType resourceType;
}
