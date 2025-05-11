package es.udc.fireproject.backend.model.entities.organization;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "organization", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Organization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @NotBlank
  private String code;

  @NotBlank
  private String name;

  @NotBlank
  @Column(name = "headquarters_address")
  private String headquartersAddress;

  @Column(name = "location", columnDefinition = "geometry(Point, 25829)")
  private Point location;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "organization_type_id")
  private OrganizationType organizationType;

  public Organization() {
  }

  public Organization(String code, String name, String headquartersAddress, Point location,
      OrganizationType organizationType) {
    this.code = code;
    this.name = name;
    this.headquartersAddress = headquartersAddress;
    this.location = location;
    this.organizationType = organizationType;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
