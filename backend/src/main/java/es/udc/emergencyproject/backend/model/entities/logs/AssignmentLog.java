package es.udc.emergencyproject.backend.model.entities.logs;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
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
@Table(name = "assignment_log", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class AssignmentLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignment_id")
  private Assignment assignment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emergency_id")
  private Emergency emergency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quadrant_id")
  private Quadrant quadrant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private GeneralLogEventType eventType;

  @Column(name = "event_at", nullable = false)
  private LocalDateTime eventAt;

  @Column(name = "details", columnDefinition = "text")
  private String details;

  public AssignmentLog(Assignment assignment, Emergency emergency, Quadrant quadrant, Resource resource,
      GeneralLogEventType eventType, LocalDateTime eventAt, String details) {
    this.assignment = assignment;
    this.emergency = emergency;
    this.quadrant = quadrant;
    this.resource = resource;
    this.eventType = eventType;
    this.eventAt = eventAt;
    this.details = details;
  }

}
