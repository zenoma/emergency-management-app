package es.udc.emergencyproject.backend.model.services.personal;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRole;
import es.udc.emergencyproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InsufficientRolePermissionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalManagementFacade {


  private final OrganizationService organizationService;
  private final UserService userService;


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

  public void registerMobileDevice(Long userId, String fcmToken) throws InstanceNotFoundException {
    userService.registerMobileDevice(userId, fcmToken);
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
