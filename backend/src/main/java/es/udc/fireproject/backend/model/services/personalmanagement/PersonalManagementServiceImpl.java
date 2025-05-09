package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationRepository;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.entities.organization.OrganizationTypeRepository;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.team.TeamRepository;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.entities.vehicle.VehicleRepository;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementServiceImpl;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonalManagementServiceImpl implements PersonalManagementService {

  private static final String USER_NOT_FOUND = "User not found";
  private static final String TEAM_NOT_FOUND = "Team not found";
  private static final String VEHICLE_NOT_FOUND = "Vehicle not found";
  private static final String TARGET_USER_NOT_FOUND = "Target user not found";

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  VehicleRepository vehicleRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private OrganizationTypeRepository organizationTypeRepository;
  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private FireManagementServiceImpl fireManagementService;

  // ORGANIZATION SERVICES
  @Override
  public List<Organization> findOrganizationByNameOrCode(String nameOrCode) {
    return organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(nameOrCode, nameOrCode);
  }

  @Override
  public Organization findOrganizationById(Long id) throws InstanceNotFoundException {
    return organizationRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException("Organization not found", id));

  }


  @Override
  public List<Organization> findOrganizationByOrganizationTypeName(String organizationTypeName) {
    return organizationRepository.findByOrganizationType_NameIgnoreCaseContainsOrderByCodeAsc(organizationTypeName);
  }

  public List<OrganizationType> findAllOrganizationTypes() {
    return organizationTypeRepository.findAll();

  }

  @Override
  public List<Organization> findAllOrganizations() {
    return organizationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  @Override
  public OrganizationType createOrganizationType(String name) {
    OrganizationType organizationType = new OrganizationType();
    organizationType.setName(name);

    ConstraintValidator.validate(organizationType);
    return organizationTypeRepository.save(organizationType);
  }

  @Override
  public OrganizationType findOrganizationTypeById(Long id) throws InstanceNotFoundException {
    return organizationTypeRepository.findById(id).orElseThrow(
        () -> new InstanceNotFoundException("Organization type not found", id));

  }

  @Override
  public Organization createOrganization(String code, String name, String headquartersAddress, Point location,
      String organizationTypeName) {
    OrganizationType organizationType = organizationTypeRepository.findByName(organizationTypeName);
    Organization organization = new Organization(code, name, headquartersAddress, location, organizationType);

    organization.setCreatedAt(LocalDateTime.now());
    organization.setOrganizationType(organizationType);
    ConstraintValidator.validate(organization);
    return organizationRepository.save(organization);
  }

  @Override
  public Organization createOrganization(Organization organization) {

    return createOrganization(organization.getCode(),
        organization.getName(),
        organization.getHeadquartersAddress(),
        organization.getLocation(),
        organization.getOrganizationType().getName());
  }


  @Override
  public void deleteOrganizationById(Long id) {
    organizationRepository.deleteById(id);
  }

  @Override
  public Organization updateOrganization(Long id, String name, String code, String headquartersAddress, Point location)
      throws InstanceNotFoundException {

    Organization organization = organizationRepository.findById(id).orElseThrow(
        () -> new InstanceNotFoundException("Organization not found", id));

    organization.setName(name);
    organization.setCode(code);
    organization.setHeadquartersAddress(headquartersAddress);
    organization.setLocation(location);

    ConstraintValidator.validate(organization);
    return organizationRepository.save(organization);
  }
  // TEAM SERVICE

  @Override
  public List<Team> findTeamByCode(String code) {
    return teamRepository.findTeamsByCodeContains(code);
  }

  @Override
  public Team createTeam(String code, Long organizationId) throws InstanceNotFoundException, AlreadyExistException {

    Organization organization = findOrganizationById(organizationId);

    List<Team> teams = findTeamsByOrganizationId(organizationId);

    for (Team item : teams) {
      if (item.getCode().equals(code)) {
        throw new AlreadyExistException(Team.class.getSimpleName(), item.getCode());
      }
    }
    Team team = new Team(code, organization);
    team.setCreatedAt(LocalDateTime.now());

    ConstraintValidator.validate(team);

    return teamRepository.save(team);
  }

  @Override
  @Transactional
  public void dismantleTeamById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, id));

    if (team.getDismantleAt() == null) {
      if (findAllUsersByTeamId(id) != null) {
        List<User> userList = new ArrayList<>(findAllUsersByTeamId(id));
        for (User user : userList) {
          user.setTeam(null);
          userRepository.save(user);
        }
      }
      fireManagementService.retractTeam(team.getId());
      team.setDismantleAt(LocalDateTime.now());

      teamRepository.save(team);
    } else {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }


  }

  @Override
  public Team updateTeam(Long id, String code) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, id));

    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    team.setCode(code);

    ConstraintValidator.validate(team);
    return teamRepository.save(team);


  }

  @Override
  public Team addMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));

    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));

    user.setTeam(team);
    userRepository.save(user);

    List<User> userList = team.getUserList();
    if (userList == null) {
      userList = new ArrayList<>();
    }
    if (!userList.contains(user)) {
      userList.add(user);
      team.setUserList(userList);
    }
    return teamRepository.save(team);
  }

  @Override
  public void deleteMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));
    if (!team.getUserList().contains(user)) {
      throw new InstanceNotFoundException(USER_NOT_FOUND, userId);
    }

    user.setTeam(null);
    userRepository.save(user);

  }

  @Override
  public List<User> findAllUsersByTeamId(Long teamId) throws InstanceNotFoundException {
    List<User> response = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId)).getUserList();
    if (response != null) {
      response.sort(Comparator.comparing(User::getId));
    }
    return response;
  }


  @Override
  public Team findTeamById(Long teamId) throws InstanceNotFoundException {
    return teamRepository.findById(teamId).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
  }

  @Override
  public Team findTeamByUserId(Long userId) throws InstanceNotFoundException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));
    return teamRepository.findById(user.getTeam().getId())
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, user.getTeam().getId()));
  }

  @Override
  public List<Team> findTeamsByOrganizationId(Long organizationId) {
    return teamRepository.findTeamsByOrganizationIdOrderByCode(organizationId);
  }

  @Override
  public List<Team> findActiveTeamsByOrganizationId(Long organizationId) {
    return teamRepository.findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(organizationId);
  }

  @Override
  public List<Team> findAllActiveTeams() {
    return teamRepository.findTeamsByDismantleAtIsNullOrderByCode();
  }

  // VEHICLE SERVICES
  @Override
  public Vehicle createVehicle(String vehiclePlate, String type, Long organizationId) throws InstanceNotFoundException {
    Organization organization = findOrganizationById(organizationId);

    Vehicle vehicle = new Vehicle(vehiclePlate, type, organization);
    vehicle.setCreatedAt(LocalDateTime.now());

    ConstraintValidator.validate(vehicle);

    return vehicleRepository.save(vehicle);
  }

  @Override
  public void dismantleVehicleById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));

    if (vehicle.getDismantleAt() == null) {
      fireManagementService.retractVehicle(vehicle.getId());
      vehicle.setDismantleAt(LocalDateTime.now());

      vehicleRepository.save(vehicle);
    } else {
      throw new AlreadyDismantledException(Vehicle.class.getSimpleName(), vehicle.getVehiclePlate());
    }

  }

  @Override
  public Vehicle updateVehicle(Long id, String vehiclePlate, String type)
      throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));
    if (vehicle.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), vehicle.getVehiclePlate());
    }

    vehicle.setVehiclePlate(vehiclePlate);
    vehicle.setType(type);

    ConstraintValidator.validate(vehicle);
    return vehicleRepository.save(vehicle);
  }


  @Override
  public Vehicle findVehicleById(Long id) throws InstanceNotFoundException {
    return vehicleRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));
  }

  @Override
  public List<Vehicle> findVehiclesByOrganizationId(Long organizationId) {
    return vehicleRepository.findByOrganizationIdOrderByVehiclePlate(organizationId);
  }

  public List<Vehicle> findActiveVehiclesByOrganizationId(Long organizationId) {

    return vehicleRepository.findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(organizationId);

  }

  @Override
  public List<Vehicle> findAllVehicles() {
    return vehicleRepository.findAll();
  }

  @Override
  public List<Vehicle> findAllActiveVehicles() {
    return vehicleRepository.findVehiclesByDismantleAtIsNullOrderByVehiclePlate();
  }

  // USER SERVICES

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAllByOrderByDniAsc();
  }

  @Override
  public void signUp(User user) throws DuplicateInstanceException {

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new DuplicateInstanceException("project.entities.user", user.getEmail());
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt(LocalDateTime.now());
    user.setUserRole(UserRole.USER);
    userRepository.save(user);

  }

  @Override
  @Transactional(readOnly = true)
  public User login(String email, String password) throws IncorrectLoginException {

    User user = userRepository.findByEmail(email).orElseThrow(() -> new IncorrectLoginException(email, password));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IncorrectLoginException(email, password);
    }

    return user;

  }

  @Override
  @Transactional(readOnly = true)
  public User loginFromId(Long id) throws InstanceNotFoundException {

    return userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, id));
  }

  @Override
  public User updateProfile(Long id, String firstName, String lastName, String email, Integer phoneNumber, String dni)
      throws InstanceNotFoundException {

    User user = userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, id));

    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setPhoneNumber(phoneNumber);
    user.setDni(dni);

    userRepository.save(user);
    return user;

  }

  @Override
  public void changePassword(Long id, String oldPassword, String newPassword)
      throws InstanceNotFoundException, IncorrectPasswordException {

    User user = userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, id));

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IncorrectPasswordException();
    } else {
      user.setPassword(passwordEncoder.encode(newPassword));
    }

    userRepository.save(user);

  }

  @Override
  public void updateRole(Long id, Long targetId, UserRole userRole) throws InstanceNotFoundException,
      InsufficientRolePermissionException {
    User user = userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, id));

    User targetUser = userRepository.findById(targetId).orElseThrow(
        () -> new InstanceNotFoundException(TARGET_USER_NOT_FOUND, targetId));

    if (user.getUserRole().isHigherThan(targetUser.getUserRole()) || userRole.isLowerThan(user.getUserRole())) {
      throw new InsufficientRolePermissionException(id, targetId);
    }

    targetUser.setUserRole(userRole);

    userRepository.save(targetUser);

  }

}
