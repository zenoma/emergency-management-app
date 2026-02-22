package es.udc.fireproject.backend.integration.services;

import es.udc.fireproject.backend.IntegrationTest;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonaManagementFacade;
import es.udc.fireproject.backend.utils.UserOM;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserServiceImplTest extends IntegrationTest {

  private static final Long INVALID_USER_ID = -1L;

  @Autowired
  private PersonaManagementFacade personaManagementFacade;

  @Test
  void givenValidData_whenSignUpAndLoginFromId_thenUserIsFound()
      throws DuplicateInstanceException, InstanceNotFoundException {

    User userOm = UserOM.withDefaultValues();

    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    User loggedInUser = personaManagementFacade.loginFromId(user.getId());

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenDuplicatedData_whenSignUp_thenDuplicateInstanceException() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personaManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertThrows(DuplicateInstanceException.class, () ->
            personaManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
                String.valueOf(user.getPhoneNumber()), user.getDni()),
        "DuplicateInstanceException expected");

  }

  @Test
  void givenInvalidData_whenLoginFromId_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> personaManagementFacade.loginFromId(INVALID_USER_ID));
  }

  @Test
  void givenValidData_whenLogin_thenUserLoggedSuccessfully()
      throws DuplicateInstanceException, IncorrectLoginException {

    User userOm = UserOM.withDefaultValues();

    String clearPassword = userOm.getPassword();

    User user = personaManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    User loggedInUser = personaManagementFacade.login(userOm.getEmail(), clearPassword);

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenInvalidPassword_whenLogin_thenIncorrectLoginException() throws DuplicateInstanceException {

    User user = UserOM.withDefaultValues();

    String clearPassword = user.getPassword();

    personaManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertThrows(IncorrectLoginException.class, () ->
        personaManagementFacade.login(user.getEmail(), 'X' + clearPassword), " Password must be incorrect");

  }

  @Test
  void givenInvalidEmail_whenLogin_thenIncorrectLoginException() {
    Assertions.assertThrows(IncorrectLoginException.class, () -> personaManagementFacade.login("X", "Y"),
        " User must not exist");
  }


  @Test
  void givenValidData_whenUpdateProfile_thenUserUpdatedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException {

    User userOm = UserOM.withDefaultValues();

    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setFirstName('X' + userOm.getFirstName());
    user.setLastName('X' + userOm.getLastName());
    user.setEmail('X' + userOm.getEmail());

    User updatedUser = personaManagementFacade.updateProfile(user.getId(), 'X' + userOm.getFirstName(),
        'X' + userOm.getLastName(),
        'X' + userOm.getEmail(), 111111111, "11111111S");

    Assertions.assertEquals(user, updatedUser, "User must be updated");

  }


  @Test
  void givenInvalidData_whenUpdateProfile_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            personaManagementFacade.updateProfile(INVALID_USER_ID, "X", "X", "X", 111111111, "11111111S"),
        "User not existent");
  }

  @Test
  void givenValidData_whenChangePassword_thenPasswordSuccessfullyChanged()
      throws DuplicateInstanceException, InstanceNotFoundException,
      IncorrectPasswordException {

    User userOm = UserOM.withDefaultValues();

    String oldPassword = userOm.getPassword();
    String newPassword = 'X' + oldPassword;
    User user = personaManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    personaManagementFacade.changePassword(user.getId(), oldPassword, newPassword);

    Assertions.assertDoesNotThrow(() -> personaManagementFacade.login(user.getEmail(), newPassword));
  }


  @Test
  void givenInvalidID_whenChangePassword_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
        personaManagementFacade.changePassword(INVALID_USER_ID, "X", "Y"), "User non existent");
  }


  @Test
  void givenIncorrectPassword_whenChangePassword_thenInstanceNotFoundException() throws DuplicateInstanceException {
    User userOm = UserOM.withDefaultValues();

    String oldPassword = userOm.getPassword();
    String newPassword = 'X' + oldPassword;

    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    Assertions.assertThrows(IncorrectPasswordException.class, () ->
            personaManagementFacade.changePassword(user.getId(), 'Y' + oldPassword, newPassword),
        "IncorrectPassword Exception expected");

  }


  @Test
  void givenValidData_whenSignUp_thenUserHasUserRole() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personaManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertEquals(UserRole.USER, user.getUserRole(), "Role must be USER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setUserRole(UserRole.COORDINATOR);

    User targetUser = personaManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    targetUserOm.setUserRole(UserRole.MANAGER);

    personaManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateHigherRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setUserRole(UserRole.COORDINATOR);

    User targetUser = personaManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    targetUser.setUserRole(UserRole.MANAGER);

    personaManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR);

    Assertions.assertEquals(UserRole.COORDINATOR, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUserWithLessRole_whenUpdateRole_thenInsufficientRolePermissionException() throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());
    User targetUser = personaManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personaManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.MANAGER),
        "User has not enought permission");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);

    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    User targetUser = personaManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);
    personaManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(),
        "Updated user role must be USER");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateHigherRole_thenInsufficientRolePermissionException()
      throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);

    User user = personaManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());
    User targetUser = personaManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personaManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR),
        "User has not enough permission");

  }
}
