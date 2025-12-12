package es.udc.fireproject.backend.model.exceptions;


public class UserWithoutTeamException extends InstanceException {

  public UserWithoutTeamException(String name, Object key) {
    super(name, key);
  }

}
