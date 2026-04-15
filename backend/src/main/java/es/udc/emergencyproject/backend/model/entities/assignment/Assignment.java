package es.udc.emergencyproject.backend.model.entities.assignment;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant;
import es.udc.emergencyproject.backend.model.entities.resource.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "assignment", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Assignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emergency_quadrant_id", nullable = true)
  private EmergencyQuadrant emergencyQuadrant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emergency_id", nullable = true)
  private Emergency emergency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  private Resource resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AssignmentStatus status;

  @Column(name = "notes")
  private String notes;

  @Column(name = "assigned_at")
  private LocalDateTime assignedAt;

  @Column(name = "accepted_at")
  private LocalDateTime acceptedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "removed", nullable = false)
  private Boolean removed = Boolean.FALSE;

}
