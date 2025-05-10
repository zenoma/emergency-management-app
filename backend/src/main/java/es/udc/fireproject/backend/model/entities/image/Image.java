package es.udc.fireproject.backend.model.entities.image;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Image extends BaseEntity {

  private static final long serialVersionUID = 1801683026003370692L;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "notice_id", nullable = false)
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
