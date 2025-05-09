package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class TeamQuadrantLogDto extends BaseDto {
    private static final long serialVersionUID = -5703692848678170759L;

    private TeamDto teamDto;

    private QuadrantInfoDto quadrantInfoDto;

    @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
    private LocalDateTime deployAt;

    @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
    private LocalDateTime retractAt;

    public TeamQuadrantLogDto() {
    }

    public TeamQuadrantLogDto(TeamDto teamDto, QuadrantInfoDto quadrantInfoDto, LocalDateTime deployAt, LocalDateTime retractAt) {
        this.teamDto = teamDto;
        this.quadrantInfoDto = quadrantInfoDto;
        this.deployAt = deployAt;
        this.retractAt = retractAt;
    }

    public TeamDto getTeamDto() {
        return teamDto;
    }

    public void setTeamDto(TeamDto teamDto) {
        this.teamDto = teamDto;
    }

    public QuadrantInfoDto getQuadrantInfoDto() {
        return quadrantInfoDto;
    }

    public void setQuadrantInfoDto(QuadrantInfoDto quadrantInfoDto) {
        this.quadrantInfoDto = quadrantInfoDto;
    }

    public LocalDateTime getDeployAt() {
        return deployAt;
    }

    public void setDeployAt(LocalDateTime deployAt) {
        this.deployAt = deployAt;
    }

    public LocalDateTime getRetractAt() {
        return retractAt;
    }

    public void setRetractAt(LocalDateTime retractAt) {
        this.retractAt = retractAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamQuadrantLogDto that = (TeamQuadrantLogDto) o;
        return Objects.equals(teamDto, that.teamDto) && Objects.equals(quadrantInfoDto, that.quadrantInfoDto) && Objects.equals(deployAt, that.deployAt) && Objects.equals(retractAt, that.retractAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamDto, quadrantInfoDto, deployAt, retractAt);
    }

    @Override
    public String toString() {
        return "TeamQuadrantLogDto{" +
                "teamDto=" + teamDto +
                ", quadrantInfoDto=" + quadrantInfoDto +
                ", deployAt=" + deployAt +
                ", retractAt=" + retractAt +
                '}';
    }
}
