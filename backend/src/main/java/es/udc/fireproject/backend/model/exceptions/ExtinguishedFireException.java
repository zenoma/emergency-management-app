package es.udc.fireproject.backend.model.exceptions;

public class ExtinguishedFireException extends EntityException {

    public ExtinguishedFireException(String name, String id) {
        super(name, id);
    }
}
