package es.udc.fireproject.backend.model.exceptions;

public class DomainException extends RuntimeException {

  private String name;
  private String id;

  protected DomainException(String message) {
    super(message);
  }

  public DomainException(String name, String id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }
}
