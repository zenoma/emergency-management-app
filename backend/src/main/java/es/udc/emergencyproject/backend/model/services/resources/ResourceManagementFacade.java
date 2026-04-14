package es.udc.emergencyproject.backend.model.services.resources;

import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceManagementFacade {


  private final TeamService teamService;
  private final VehicleService vehicleService;

  // TEAM SERVICE

  public List<Team> findTeamByCode(String code) {
    return teamService.findTeamByCode(code);
  }

  public Team createTeam(String code, Long organizationId) throws InstanceNotFoundException, AlreadyExistException {
    return teamService.createTeam(code, organizationId);
  }

  public void dismantleTeamById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    teamService.dismantleTeamById(id);
  }

  public Team updateTeam(Long id, String code) throws InstanceNotFoundException, AlreadyDismantledException {
    return teamService.updateTeam(id, code);

  }

  public Team addMemberToTeam(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    return teamService.addMember(teamId, userId);
  }

  public void deleteMemberFromTeam(Long teamId, Long userId)
      throws InstanceNotFoundException, AlreadyDismantledException {
    teamService.deleteMember(teamId, userId);

  }

  public List<User> findAllUsersByTeamId(Long teamId) throws InstanceNotFoundException {
    return teamService.findAllUsersByTeamId(teamId);
  }


  public Team findTeamById(Long teamId) throws InstanceNotFoundException {
    return teamService.findTeamById(teamId);
  }

  public Team findTeamByUserId(Long userId) throws InstanceNotFoundException {
    return teamService.findTeamByUserId(userId);
  }

  public List<Team> findTeamsByOrganizationId(Long organizationId) {
    return teamService.findTeamsByOrganizationId(organizationId);
  }

  public List<Team> findActiveTeamsByOrganizationId(Long organizationId) {
    return teamService.findActiveTeamsByOrganizationId(organizationId);
  }

  public List<Team> findAllActiveTeams() {
    return teamService.findAllActiveTeams();
  }

  // VEHICLE SERVICES

  public Vehicle createVehicle(String vehiclePlate, String type, Long organizationId) throws InstanceNotFoundException {
    return vehicleService.createVehicle(vehiclePlate, type, organizationId);
  }


  public void dismantleVehicleById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    vehicleService.dismantleVehicleById(id);

  }


  public Vehicle updateVehicle(Long id, String vehiclePlate, String type)
      throws InstanceNotFoundException, AlreadyDismantledException {
    return vehicleService.updateVehicle(id, vehiclePlate, type);
  }


  public Vehicle findVehicleById(Long id) throws InstanceNotFoundException {
    return vehicleService.findVehicleById(id);
  }


  public List<Vehicle> findVehiclesByOrganizationId(Long organizationId) {
    return vehicleService.findVehiclesByOrganizationId(organizationId);
  }

  public List<Vehicle> findActiveVehiclesByOrganizationId(Long organizationId) {

    return vehicleService.findActiveVehiclesByOrganizationId(organizationId);

  }


  public List<Vehicle> findAllVehicles() {
    return vehicleService.findAllVehicles();
  }


  public List<Vehicle> findAllActiveVehicles() {
    return vehicleService.findAllActiveVehicles();
  }


}
