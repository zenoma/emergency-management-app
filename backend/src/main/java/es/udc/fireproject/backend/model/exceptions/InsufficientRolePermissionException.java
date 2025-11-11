package es.udc.fireproject.backend.model.exceptions;

public class InsufficientRolePermissionException extends RuntimeException {

  public InsufficientRolePermissionException(Long id, Long targetId) {
    super("User with " + id + " has not the needed permissions to update" + targetId);
  }
}
