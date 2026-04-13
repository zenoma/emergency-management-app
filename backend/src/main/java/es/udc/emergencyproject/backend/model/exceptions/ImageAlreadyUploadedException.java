package es.udc.emergencyproject.backend.model.exceptions;

public class ImageAlreadyUploadedException extends DomainException {

  public ImageAlreadyUploadedException(String name, String id) {
    super(name, id);
  }
}
