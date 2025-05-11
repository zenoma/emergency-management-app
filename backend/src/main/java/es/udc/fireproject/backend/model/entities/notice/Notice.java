package es.udc.fireproject.backend.model.entities.notice;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.user.User;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class Notice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;


  @Column(name = "body")
  private String body;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private NoticeStatus status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "location", columnDefinition = "geometry(Point, 25829)")
  @NotNull
  private Point location;

  @OneToMany(
      mappedBy = "notice",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<Image> imageList;


  public Notice() {
  }

  public Notice(String body, NoticeStatus status, Point location) {
    this.body = body;
    this.status = status;
    this.location = location;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
