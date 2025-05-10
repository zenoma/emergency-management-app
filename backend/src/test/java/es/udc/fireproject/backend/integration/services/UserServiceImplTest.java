package es.udc.fireproject.backend.integration.services;

import es.udc.fireproject.backend.IntegrationTest;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.utils.UserOM;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceImplTest extends IntegrationTest {

  private final Long INVALID_USER_ID = -1L;

  @Autowired
  private PersonalManagementService personalManagementService;

  @Test
  void givenValidData_whenSignUpAndLoginFromId_thenUserIsFound()
      throws DuplicateInstanceException, InstanceNotFoundException {

    User user = UserOM.withDefaultValues();

    personalManagementService.signUp(user);

    User loggedInUser = personalManagementService.loginFromId(user.getId());

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenDuplicatedData_whenSignUp_thenDuplicateInstanceException() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personalManagementService.signUp(user);
    Assertions.assertThrows(DuplicateInstanceException.class, () -> personalManagementService.signUp(user),
        "DuplicateInstanceException expected");

  }

  @Test
  void givenInvalidData_whenLoginFromId_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> personalManagementService.loginFromId(INVALID_USER_ID));
  }

  @Test
  void givenValidData_whenLogin_thenUserLoggedSuccessfully()
      throws DuplicateInstanceException, IncorrectLoginException {

    User user = UserOM.withDefaultValues();

    String clearPassword = user.getPassword();

    personalManagementService.signUp(user);

    User loggedInUser = personalManagementService.login(user.getEmail(), clearPassword);

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenInvalidPassword_whenLogin_thenIncorrectLoginException() throws DuplicateInstanceException {

    User user = UserOM.withDefaultValues();

    String clearPassword = user.getPassword();

    personalManagementService.signUp(user);
    Assertions.assertThrows(IncorrectLoginException.class, () ->
        personalManagementService.login(user.getEmail(), 'X' + clearPassword), " Password must be incorrect");

  }

  @Test
  void givenInvalidEmail_whenLogin_thenIncorrectLoginException() {
    Assertions.assertThrows(IncorrectLoginException.class, () -> personalManagementService.login("X", "Y"),
        " User must not exist");
  }


  @Test
  void givenValidData_whenUpdateProfile_thenUserUpdatedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException {

    User user = UserOM.withDefaultValues();

    personalManagementService.signUp(user);

    user.setFirstName('X' + user.getFirstName());
    user.setLastName('X' + user.getLastName());
    user.setEmail('X' + user.getEmail());

    personalManagementService.updateProfile(user.getId(), 'X' + user.getFirstName(), 'X' + user.getLastName(),
        'X' + user.getEmail(), 111111111, "11111111S");

    User updatedUser = personalManagementService.loginFromId(user.getId());

    Assertions.assertEquals(user, updatedUser, "User must be updated");

  }


  @Test
  void givenInvalidData_whenUpdateProfile_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            personalManagementService.updateProfile(INVALID_USER_ID, "X", "X", "X", 111111111, "11111111S"),
        "User not existent");
  }

  @Test
  void givenValidData_whenChangePassword_thenPasswordSuccessfullyChanged()
      throws DuplicateInstanceException, InstanceNotFoundException,
      IncorrectPasswordException {

    User user = UserOM.withDefaultValues();

    String oldPassword = user.getPassword();
    String newPassword = 'X' + oldPassword;
    personalManagementService.signUp(user);

    personalManagementService.changePassword(user.getId(), oldPassword, newPassword);

    Assertions.assertDoesNotThrow(() -> personalManagementService.login(user.getEmail(), newPassword));
  }


  @Test
  void givenInvalidID_whenChangePassword_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
        personalManagementService.changePassword(INVALID_USER_ID, "X", "Y"), "User non existent");
  }


  @Test
  void givenIncorrectPassword_whenChangePassword_thenInstanceNotFoundException() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    String oldPassword = user.getPassword();
    String newPassword = 'X' + oldPassword;

    personalManagementService.signUp(user);
    Assertions.assertThrows(IncorrectPasswordException.class, () ->
            personalManagementService.changePassword(user.getId(), 'Y' + oldPassword, newPassword),
        "IncorrectPassword Exception expected");

  }


  @Test
  void givenValidData_whenSignUp_thenUserHasUserRole() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personalManagementService.signUp(user);

    Assertions.assertEquals(UserRole.USER, user.getUserRole(), "Role must be USER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    User targetUser = userList.get(1);
    personalManagementService.signUp(user);
    user.setUserRole(UserRole.COORDINATOR);
    personalManagementService.signUp(targetUser);
    targetUser.setUserRole(UserRole.MANAGER);

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateHigherRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    User targetUser = userList.get(1);
    personalManagementService.signUp(user);
    user.setUserRole(UserRole.COORDINATOR);
    personalManagementService.signUp(targetUser);
    targetUser.setUserRole(UserRole.MANAGER);

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR);

    Assertions.assertEquals(UserRole.COORDINATOR, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUserWithLessRole_whenUpdateRole_thenInsufficientRolePermissionException() throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    User targetUser = userList.get(1);
    personalManagementService.signUp(user);
    personalManagementService.signUp(targetUser);

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.MANAGER),
        "User has not enought permission");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    User targetUser = userList.get(1);
    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);
    personalManagementService.signUp(user);
    personalManagementService.signUp(targetUser);

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(),
        "Updated user role must be USER");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateHigherRole_thenInsufficientRolePermissionException()
      throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    User targetUser = userList.get(1);
    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);
    personalManagementService.signUp(user);
    personalManagementService.signUp(targetUser);

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR),
        "User has not enough permission");

  }
}
