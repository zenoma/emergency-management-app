package es.udc.emergencyproject.backend.model.entities.mobiledevice;

import es.udc.emergencyproject.backend.model.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mobile_device", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(exclude = "user")
@AllArgsConstructor
@NoArgsConstructor
public class MobileDevice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fcm_token", nullable = false, unique = true)
  private String fcmToken;

  @Column(name = "last_seen_at")
  private LocalDateTime lastSeenAt;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
