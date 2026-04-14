package es.udc.emergencyproject.backend.model.exceptions;


public class DuplicateInstanceException extends InstanceException {

  public DuplicateInstanceException(String name, Object key) {
    super(name, key);
  }

}
