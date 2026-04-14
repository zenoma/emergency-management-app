package es.udc.emergencyproject.backend.model.entities.quadrant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "quadrants", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Quadrant implements Serializable {

  private static final long serialVersionUID = 4848346612436497001L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "gid", nullable = false)
  private Integer id;

  @Column(name = "escala", length = 50)
  private String escala;

  @Column(name = "nombre", length = 50)
  private String nombre;

  @Column(name = "folla50", length = 50)
  private String folla50;

  @Column(name = "folla25", length = 50)
  private String folla25;

  @Column(name = "folla5", length = 50)
  private String folla5;

  @Column(name = "geom", columnDefinition = "geometry(MultiPolygon, 25829)")
  private MultiPolygon geom;


}
