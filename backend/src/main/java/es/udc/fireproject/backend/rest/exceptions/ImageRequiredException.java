package es.udc.fireproject.backend.rest.exceptions;

public class ImageRequiredException extends Exception {

    public ImageRequiredException() {
        super("Image must be not empty");
    }

}
