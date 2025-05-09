package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamDto;
import es.udc.fireproject.backend.rest.dtos.VehicleDto;
import java.util.ArrayList;
import java.util.List;

public class QuadrantInfoConversor {

  private QuadrantInfoConversor() {

  }

  public static QuadrantInfoDto toQuadrantDto(Quadrant quadrant) {
    List<TeamDto> teamDtoList = new ArrayList<>();
    if (quadrant.getTeamList() != null && !quadrant.getTeamList().isEmpty()) {
      for (Team team : quadrant.getTeamList()) {
        teamDtoList.add(TeamConversor.toTeamDtoWithoutQuadrantInfo(team));
      }
    }
    List<VehicleDto> vehicleDtoList = new ArrayList<>();
    if (quadrant.getVehicleList() != null && !quadrant.getVehicleList().isEmpty()) {
      for (Vehicle vehicle : quadrant.getVehicleList()) {
        vehicleDtoList.add(VehicleConversor.toVehicleDtoWithoutQuadrantInfo(vehicle));
      }
    }

    return new QuadrantInfoDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(), teamDtoList, vehicleDtoList, new ArrayList());
  }

  public static QuadrantInfoDto toQuadrantDtoWithoutTeamsAndVehicles(Quadrant quadrant) {
    return new QuadrantInfoDto(quadrant.getId(),
        quadrant.getEscala(),
        quadrant.getNombre(), new ArrayList());
  }


}
