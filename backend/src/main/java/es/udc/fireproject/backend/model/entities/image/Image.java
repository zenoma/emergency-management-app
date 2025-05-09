package es.udc.fireproject.backend.model.entities.image;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.notice.Notice;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
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
    this.createdAt = LocalDateTime.now();
  }

  public Notice getNotice() {
    return notice;
  }

  public void setNotice(Notice notice) {
    this.notice = notice;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Image image = (Image) o;
    return Objects.equals(notice, image.notice) && Objects.equals(name, image.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notice, name);
  }

  @Override
  public String toString() {
    return "Image{" +
        "notice=" + notice +
        ", name='" + name + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}
