package es.udc.emergencyproject.backend.model.entities.emergency;

import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "emergency", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Emergency {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "description", nullable = false)
  private String description;

  @jakarta.validation.constraints.NotNull
  @jakarta.persistence.ManyToOne(cascade = {jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.REFRESH})
  @jakarta.persistence.JoinColumn(name = "type_id")
  private EmergencyType emergencyType;


  @Enumerated(EnumType.STRING)
  @Column(name = "emergency_index", nullable = false)
  private EmergencyIndex emergencyIndex;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "extinguished_at")
  private LocalDateTime extinguishedAt;

  @OneToMany(mappedBy = "emergency",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<EmergencyQuadrant> emergencyQuadrants;

  @Column(name = "location", columnDefinition = "geometry(Point, 25829)")
  private Point location;


  @Transient
  public List<Quadrant> getQuadrantGids() {
    if (this.emergencyQuadrants == null) {
      return null;
    }
    return this.emergencyQuadrants.stream()
        .map(EmergencyQuadrant::getQuadrant)
        .filter(java.util.Objects::nonNull)
        .toList();
  }

}
