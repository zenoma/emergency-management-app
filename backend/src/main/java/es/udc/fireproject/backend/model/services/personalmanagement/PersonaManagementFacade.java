package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonaManagementFacade {


  private final OrganizationService organizationService;
  private final UserService userService;
  private final TeamService teamService;
  private final VehicleService vehicleService;


  // ORGANIZATION SERVICES
  public List<Organization> findOrganizationByNameOrCode(String nameOrCode) {
    return organizationService.findOrganizationByNameOrCode(nameOrCode);
  }


  public Organization findOrganizationById(Long id) throws InstanceNotFoundException {
    return organizationService.findOrganizationById(id);
  }


  public List<Organization> findOrganizationByOrganizationTypeName(String organizationTypeName) {
    return organizationService.findOrganizationByOrganizationTypeName(organizationTypeName);
  }

  public List<OrganizationType> findAllOrganizationTypes() {
    return organizationService.findAllOrganizationTypes();
  }


  public List<Organization> findAllOrganizations() {
    return organizationService.findAllOrganizations();
  }


  public OrganizationType createOrganizationType(String name) {
    return organizationService.createOrganizationType(name);
  }


  public OrganizationType findOrganizationTypeById(Long id) throws InstanceNotFoundException {
    return organizationService.findOrganizationTypeById(id);
  }


  public Organization createOrganization(String code, String name, String headquartersAddress, Point location,
      String organizationTypeName) {
    return organizationService.createOrganization(code, name, headquartersAddress, location, organizationTypeName);
  }


  public Organization createOrganization(Organization organization) {
    return organizationService.createOrganization(organization);
  }


  public void deleteOrganizationById(Long id) {
    organizationService.deleteOrganizationById(id);
  }


  public Organization updateOrganization(Long id, String name, String code, String headquartersAddress, Point location)
      throws InstanceNotFoundException {
    return organizationService.updateOrganization(id, name, code, headquartersAddress, location);
  }
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

  public Team addMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    return teamService.addMember(teamId, userId);
  }

  public void deleteMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
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

  // USER SERVICES

  public List<User> findAllUsers() {
    return userService.findAllUsers();
  }

  public User signUp(String email,
      String password,
      String firstName,
      String lastName,
      String phoneNumber,
      String dni)
      throws DuplicateInstanceException {

    return userService.signUp(email, password, firstName, lastName, phoneNumber, dni);
  }

  public User login(String email, String password) throws IncorrectLoginException {
    return userService.login(email, password);

  }

  public User loginFromId(Long id) throws InstanceNotFoundException {
    return userService.loginFromId(id);
  }

  public User updateProfile(Long id, String firstName, String lastName, String email, Integer phoneNumber, String dni)
      throws InstanceNotFoundException {

    return userService.updateProfile(id, firstName, lastName, email, phoneNumber, dni);
  }

  public void changePassword(Long id, String oldPassword, String newPassword)
      throws InstanceNotFoundException, IncorrectPasswordException {

    userService.changePassword(id, oldPassword, newPassword);
  }

  public void updateRole(Long id, Long targetId, UserRole userRole) throws InstanceNotFoundException,
      InsufficientRolePermissionException {

    userService.updateRole(id, targetId, userRole);

  }

}
