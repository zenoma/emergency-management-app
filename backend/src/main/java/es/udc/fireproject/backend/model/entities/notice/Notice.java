package es.udc.fireproject.backend.model.entities.notice;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.locationtech.jts.geom.Point;

@Entity
public class Notice extends BaseEntity {

  private static final long serialVersionUID = 1801683026003370692L;


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

  @Column(name = "location", columnDefinition = "geometry(Point, 25828)")
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
    this.createdAt = LocalDateTime.now();
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public NoticeStatus getStatus() {
    return status;
  }

  public void setStatus(NoticeStatus status) {
    this.status = status;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<Image> getImageList() {
    return imageList;
  }

  public void setImageList(List<Image> imageList) {
    this.imageList = imageList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Notice notice = (Notice) o;
    return Objects.equals(body, notice.body) &&
        Objects.equals(status, notice.status) &&
        Objects.equals(location, notice.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, status, location);
  }

  @Override
  public String toString() {
    return "Notice{" +
        "id=" + getId() +
        ", body='" + body + '\'' +
        ", status=" + status +
        ", createdAt=" + createdAt +
        ", user=" + user +
        ", location=" + location +
        '}';
  }
}
