package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import org.hibernate.Hibernate;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleResponseDto;


public class AssignmentMapper {

  private AssignmentMapper() {
  }

  public static AssignmentResponseDto toAssignmentResponseDto(Assignment a) {
    AssignmentResponseDto dto = new AssignmentResponseDto();
    dto.setId(a.getId());

    if (a.getEmergencyQuadrant() != null) {
      dto.setEmergencyQuadrantId(a.getEmergencyQuadrant().getId());
    }

    if (a.getResource() != null) {
      dto.setResourceId(a.getResource().getId());
    }

    if (a.getStatus() != null) {
      dto.setStatus(AssignmentResponseDto.StatusEnum.fromValue(a.getStatus().name()));
    }

    dto.setNotes(a.getNotes());
    dto.setAssignedAt(a.getAssignedAt());
    dto.setAcceptedAt(a.getAcceptedAt());
    dto.setCompletedAt(a.getCompletedAt());

    if (a.getEmergency() != null) {
      EmergencyResponseDto emergencyDto = EmergencyMapper.toEmergencyDto(a.getEmergency());
      dto.setEmergencyInfo(emergencyDto);
    }

    if (a.getEmergencyQuadrant() != null && a.getEmergencyQuadrant().getQuadrant() != null) {
      QuadrantDto quadrantDto = QuadrantMapper.toQuadrantDto(a.getEmergencyQuadrant().getQuadrant());
      dto.setQuadrantInfo(quadrantDto);
    }

    if (a.getResource() != null) {
      var resource = a.getResource();
      if (resource.getResourceType() != null) {
        Object unproxied = Hibernate.unproxy(resource);
        switch (resource.getResourceType()) {
          case TEAM:
            TeamResponseDto teamDto = TeamMapper.toTeamDto((Team) unproxied);
            dto.setTeamInfo(teamDto);
            break;
          case VEHICLE:
            VehicleResponseDto vehicleDto = VehicleMapper.toVehicleDto((Vehicle) unproxied);
            dto.setVehicleInfo(vehicleDto);
            break;
          default:
        }
      }
    }

    return dto;
  }

}
