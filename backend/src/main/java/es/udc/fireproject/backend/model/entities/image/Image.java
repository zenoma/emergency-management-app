package es.udc.fireproject.backend.model.entities.image;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Image {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "notice_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Notice notice;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public Image() {
  }

  public Image(Notice notice, String name) {
    this.notice = notice;
    this.name = name;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}
