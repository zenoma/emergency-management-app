package es.udc.fireproject.backend.model.exceptions;

public class NoticeUpdateStatusException extends RuntimeException {

  private String id;
  private String status;


  public NoticeUpdateStatusException(Long id, String status) {
    this.id = String.valueOf(id);
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }
}
