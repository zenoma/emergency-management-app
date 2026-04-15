package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.GeneralLog;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.rest.dtos.GlobalLogDto;
import java.time.ZoneOffset;
import org.hibernate.Hibernate;

public class GeneralLogMapper {

  private GeneralLogMapper() {
  }

  public static GlobalLogDto toGlobalLogDto(GeneralLog gl) {
    if (gl == null) {
      return null;
    }
    var dto = new GlobalLogDto();

    dto.setId(gl.getId());
    dto.setEventType(gl.getEventType() != null ? gl.getEventType().name() : null);
    dto.setEventAt(gl.getEventAt() != null ? gl.getEventAt().atOffset(ZoneOffset.UTC) : null);
    dto.setDetails(gl.getDetails());

    dto.setAssignment(gl.getAssignment() != null ? AssignmentMapper.toAssignmentResponseDto(gl.getAssignment()) : null);
    dto.setEmergency(
        gl.getEmergency() != null ? EmergencyMapper.toEmergencyDtoWithoutQuadrants(gl.getEmergency()) : null);
    dto.setQuadrant(gl.getQuadrant() != null ? QuadrantMapper.toQuadrantDto(gl.getQuadrant()) : null);

    if (gl.getResource() != null) {
      Object unproxied = Hibernate.unproxy(gl.getResource());
      if (gl.getResource().getResourceType() == ResourceType.TEAM) {
        dto.setTeamInfo(TeamMapper.toTeamDto((Team) unproxied));
      } else if (gl.getResource().getResourceType() == ResourceType.VEHICLE) {
        dto.setVehicleInfo(VehicleMapper.toVehicleDtoWithoutQuadrantInfo((Vehicle) unproxied));
      }
    }
    return dto;
  }
}
