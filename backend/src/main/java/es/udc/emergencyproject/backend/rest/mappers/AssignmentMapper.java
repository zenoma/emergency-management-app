package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentResponseDto;


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

    return dto;
  }

}
