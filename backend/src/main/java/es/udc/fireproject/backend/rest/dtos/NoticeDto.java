package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoticeDto extends BaseDto {

    private static final long serialVersionUID = 965287827989720585L;

    @Id
    private Long id;

    private String body;

    private NoticeStatus status;


    @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;

    @NotNull(groups = {NoticeDto.AllValidations.class})
    private double lon;

    @NotNull(groups = {NoticeDto.AllValidations.class})
    private double lat;


    @JsonProperty("images")
    private List<ImageDto> imageList;

    public NoticeDto() {
    }

    public NoticeDto(Long id, String body, NoticeStatus status, LocalDateTime createdAt, UserDto user, double lon, double lat, List<ImageDto> imageList) {
        this.id = id;
        this.body = body;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
        this.lon = lon;
        this.lat = lat;
        this.imageList = imageList;
    }

    public NoticeDto(Long id, String body, NoticeStatus status, LocalDateTime createdAt, double lon, double lat, List<ImageDto> imageList) {
        this.id = id;
        this.body = body;
        this.status = status;
        this.createdAt = createdAt;
        this.lon = lon;
        this.lat = lat;
        this.imageList = imageList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(NoticeStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonProperty("coordinates")
    private void unpackNested(Map<String, Double> coordinates) {
        this.lon = coordinates.get("lon");
        this.lat = coordinates.get("lat");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeDto noticeDto = (NoticeDto) o;
        return Double.compare(noticeDto.lon, lon) == 0 && Double.compare(noticeDto.lat, lat) == 0 && Objects.equals(id, noticeDto.id) && Objects.equals(body, noticeDto.body) && status == noticeDto.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, status, lon, lat);
    }

    @Override
    public String toString() {
        return "NoticeDto{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", user=" + user +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }

    public interface AllValidations {
    }

    public interface UpdateValidations {
    }
}
