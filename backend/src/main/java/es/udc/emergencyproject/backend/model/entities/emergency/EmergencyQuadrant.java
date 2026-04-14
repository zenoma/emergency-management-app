package es.udc.emergencyproject.backend.model.entities.emergency;

import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "emergency_quadrant", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class EmergencyQuadrant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emergency_id", nullable = false)
  private Emergency emergency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;

  @Column(name = "linked_at")
  private LocalDateTime linkedAt;

}
