package es.udc.fireproject.backend.rest.dtos;

import java.util.Objects;

public class NoticeStatusDto extends BaseDto {

    private static final long serialVersionUID = 1641437225518108070L;

    private String status;

    NoticeStatusDto() {

    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeStatusDto that = (NoticeStatusDto) o;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "NoticeStatusDto{" +
                "status='" + status + '\'' +
                '}';
    }
}
