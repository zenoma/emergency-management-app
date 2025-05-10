package es.udc.fireproject.backend.model.entities.fire;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fire", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Fire extends BaseEntity {

  private static final long serialVersionUID = -4072358581910996914L;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "index", nullable = false)
  @Enumerated(EnumType.STRING)
  private FireIndex fireIndex;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "fire",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<Quadrant> quadrantGids;

  @Column(name = "extinguished_at")
  private LocalDateTime extinguishedAt;

  public Fire() {
  }

  public Fire(String description, String type, FireIndex fireIndex) {
    this.description = description;
    this.type = type;
    this.fireIndex = fireIndex;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}
