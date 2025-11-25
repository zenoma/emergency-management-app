package es.udc.fireproject.backend.rest.exceptions;

public class ImageRequiredException extends RuntimeException {

  public ImageRequiredException() {
    super("Image must be not empty");
  }

}
