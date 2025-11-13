package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuadrantInfoConversor {

  private QuadrantInfoConversor() {

  }

  public static QuadrantInfoDto toQuadrantDto(Quadrant quadrant) {
    List<TeamResponseDto> teamResponseDtos = new ArrayList<>();
    if (quadrant.getTeamList() != null && !quadrant.getTeamList().isEmpty()) {
      for (Team team : quadrant.getTeamList()) {
        teamResponseDtos.add(TeamConversor.toTeamDtoWithoutQuadrantInfo(team));
      }
    }
    List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();
    if (quadrant.getVehicleList() != null && !quadrant.getVehicleList().isEmpty()) {
      for (Vehicle vehicle : quadrant.getVehicleList()) {
        vehicleResponseDtos.add(VehicleConversor.toVehicleDtoWithoutQuadrantInfo(vehicle));
      }
    }

    QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(),
        new ArrayList(Arrays.asList(quadrant.getGeom().getCoordinates())));

    quadrantInfoDto.setTeamList(teamResponseDtos);
    quadrantInfoDto.setVehicleList(vehicleResponseDtos);

    return quadrantInfoDto;
  }

  public static QuadrantInfoDto toQuadrantDtoWithoutTeamsAndVehicles(Quadrant quadrant) {
    return new QuadrantInfoDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(),
        new ArrayList(Arrays.asList(quadrant.getGeom().getCoordinates())));
  }

}
