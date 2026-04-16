package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentLogDto;
import java.time.ZoneOffset;
import org.hibernate.Hibernate;

public class AssignmentLogMapper {

  private AssignmentLogMapper() {
  }

  public static AssignmentLogDto toGlobalLogDto(AssignmentLog gl) {
    if (gl == null) {
      return null;
    }
    var dto = new AssignmentLogDto();

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
