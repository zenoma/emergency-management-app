package es.udc.fireproject.backend.model.entities;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 293432302962143319L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

}
