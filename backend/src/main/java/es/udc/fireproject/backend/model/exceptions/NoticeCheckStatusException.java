package es.udc.fireproject.backend.model.exceptions;

public class NoticeCheckStatusException extends Exception {

    private String id;
    private String status;


    public NoticeCheckStatusException(Long id, String status) {
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
