package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.services.assignment.AssignmentService;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentStatusRequestDto;
import es.udc.emergencyproject.backend.rest.mappers.AssignmentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AssignmentsController implements AssignmentsApi {

  private final AssignmentService assignmentService;


  @Override
  public ResponseEntity<Void> deleteAssignmentById(Long id) {
    assignmentService.deleteAssignment(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<AssignmentResponseDto> getAssignmentById(Long id) {
    Assignment a = assignmentService.findAssignmentById(id);
    return ResponseEntity.ok(AssignmentMapper.toAssignmentResponseDto(a));
  }

  @Override
  public ResponseEntity<List<AssignmentResponseDto>> getAssignments(Long emergencyQuadrantId, Long resourceId) {
    List<Assignment> assignments;

    if (emergencyQuadrantId != null) {
      assignments = assignmentService.findByEmergencyQuadrantQuadrantId(emergencyQuadrantId.intValue());
    } else if (resourceId != null) {
      assignments = assignmentService.findByResourceId(resourceId);
    } else {
      // return all assignments when no filter is provided
      assignments = assignmentService.findAll();
    }

    List<AssignmentResponseDto> dtos = assignments.stream().map(AssignmentMapper::toAssignmentResponseDto).toList();
    return ResponseEntity.ok(dtos);
  }

  @Override
  public ResponseEntity<AssignmentResponseDto> postAssignment(AssignmentRequestDto request) {

    Assignment a = assignmentService.createAssignment(request.getEmergencyId(), request.getQuadrantId(),
        request.getResourceId(), request.getNotes());

    AssignmentResponseDto dto = AssignmentMapper.toAssignmentResponseDto(a);

    return ResponseEntity.ok().body(dto);
  }


  @Override
  public ResponseEntity<AssignmentResponseDto> putAssignmentStatus(Long id,
      AssignmentStatusRequestDto assignmentStatusRequestDto) {
    Assignment a = assignmentService.updateStatus(id,
        AssignmentStatus.valueOf(assignmentStatusRequestDto.getStatus().name()));

    return ResponseEntity.ok().body(AssignmentMapper.toAssignmentResponseDto(a));
  }

}
