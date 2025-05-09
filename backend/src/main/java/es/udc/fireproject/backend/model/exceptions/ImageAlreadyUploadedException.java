package es.udc.fireproject.backend.model.exceptions;

public class ImageAlreadyUploadedException extends EntityException {

    public ImageAlreadyUploadedException(String name, String id) {
        super(name, id);
    }
}
