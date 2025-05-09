package es.udc.fireproject.backend.model.exceptions;

public class EntityException extends Exception {

    private String name;
    private String id;

    protected EntityException(String message) {
        super(message);
    }

    public EntityException(String name, String id) {
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
