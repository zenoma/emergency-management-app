package es.udc.fireproject.backend.model.entities.user;

import es.udc.fireproject.backend.model.entities.team.Team;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(exclude = "password")
@ToString(exclude = "password")
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Email(message = "Email should be valid")
  private String email;

  @Size(min = 8, message
      = "Password must contain at least 8 characters")
  private String password;

  @NotBlank
  @Column(name = "first_name")
  private String firstName;

  @NotBlank
  @Column(name = "last_name")
  private String lastName;

  @Size(min = 9, max = 9, message
      = "DNI must have 9 characters")
  private String dni;

  @Positive
  @Column(name = "phone_number")
  @Digits(integer = 9, fraction = 0)
  private Integer phoneNumber;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "user_role")
  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  public User() {
  }

  public User(String email,
      String password,
      String firstName,
      String lastName,
      String dni,
      Integer phoneNumber) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dni = dni;
    this.phoneNumber = phoneNumber;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    this.userRole = UserRole.USER;
  }


}


