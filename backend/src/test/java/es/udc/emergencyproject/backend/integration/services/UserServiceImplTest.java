package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRole;
import es.udc.emergencyproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.utils.UserOM;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImplTest extends IntegrationTest {

  private static final Long INVALID_USER_ID = -1L;

  @Autowired
  private PersonalManagementFacade personalManagementFacade;

  @Test
  void givenValidData_whenSignUpAndLoginFromId_thenUserIsFound()
      throws DuplicateInstanceException, InstanceNotFoundException {

    User userOm = UserOM.withDefaultValues();

    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    User loggedInUser = personalManagementFacade.loginFromId(user.getId());

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenDuplicatedData_whenSignUp_thenDuplicateInstanceException() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personalManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertThrows(DuplicateInstanceException.class, () ->
            personalManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
                String.valueOf(user.getPhoneNumber()), user.getDni()),
        "DuplicateInstanceException expected");

  }

  @Test
  void givenInvalidData_whenLoginFromId_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> personalManagementFacade.loginFromId(INVALID_USER_ID));
  }

  @Test
  void givenValidData_whenLogin_thenUserLoggedSuccessfully()
      throws DuplicateInstanceException, IncorrectLoginException {

    User userOm = UserOM.withDefaultValues();

    String clearPassword = userOm.getPassword();

    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    User loggedInUser = personalManagementFacade.login(userOm.getEmail(), clearPassword);

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenInvalidPassword_whenLogin_thenIncorrectLoginException() throws DuplicateInstanceException {

    User user = UserOM.withDefaultValues();

    String clearPassword = user.getPassword();

    personalManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertThrows(IncorrectLoginException.class, () ->
        personalManagementFacade.login(user.getEmail(), 'X' + clearPassword), " Password must be incorrect");

  }

  @Test
  void givenInvalidEmail_whenLogin_thenIncorrectLoginException() {
    Assertions.assertThrows(IncorrectLoginException.class, () -> personalManagementFacade.login("X", "Y"),
        " User must not exist");
  }


  @Test
  void givenValidData_whenUpdateProfile_thenUserUpdatedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException {

    User userOm = UserOM.withDefaultValues();

    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setFirstName('X' + userOm.getFirstName());
    user.setLastName('X' + userOm.getLastName());
    user.setEmail('X' + userOm.getEmail());

    User updatedUser = personalManagementFacade.updateProfile(user.getId(), 'X' + userOm.getFirstName(),
        'X' + userOm.getLastName(),
        'X' + userOm.getEmail(), 111111111, "11111111S");

    Assertions.assertEquals(user, updatedUser, "User must be updated");

  }


  @Test
  void givenInvalidData_whenUpdateProfile_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            personalManagementFacade.updateProfile(INVALID_USER_ID, "X", "X", "X", 111111111, "11111111S"),
        "User not existent");
  }

  @Test
  void givenValidData_whenChangePassword_thenPasswordSuccessfullyChanged()
      throws DuplicateInstanceException, InstanceNotFoundException,
      IncorrectPasswordException {

    User userOm = UserOM.withDefaultValues();

    String oldPassword = userOm.getPassword();
    String newPassword = 'X' + oldPassword;
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    personalManagementFacade.changePassword(user.getId(), oldPassword, newPassword);

    Assertions.assertDoesNotThrow(() -> personalManagementFacade.login(user.getEmail(), newPassword));
  }


  @Test
  void givenInvalidID_whenChangePassword_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
        personalManagementFacade.changePassword(INVALID_USER_ID, "X", "Y"), "User non existent");
  }


  @Test
  void givenIncorrectPassword_whenChangePassword_thenInstanceNotFoundException() throws DuplicateInstanceException {
    User userOm = UserOM.withDefaultValues();

    String oldPassword = userOm.getPassword();
    String newPassword = 'X' + oldPassword;

    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    Assertions.assertThrows(IncorrectPasswordException.class, () ->
            personalManagementFacade.changePassword(user.getId(), 'Y' + oldPassword, newPassword),
        "IncorrectPassword Exception expected");

  }


  @Test
  void givenValidData_whenSignUp_thenUserHasUserRole() throws DuplicateInstanceException {
    User user = UserOM.withDefaultValues();

    personalManagementFacade.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    Assertions.assertEquals(UserRole.MEMBER, user.getUserRole(), "Role must be MEMBER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setUserRole(UserRole.COORDINATOR);

    User targetUser = personalManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    targetUserOm.setUserRole(UserRole.MANAGER);

    personalManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.MEMBER);

    Assertions.assertEquals(UserRole.MEMBER, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateHigherRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    user.setUserRole(UserRole.COORDINATOR);

    User targetUser = personalManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    targetUser.setUserRole(UserRole.MANAGER);

    personalManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR);

    Assertions.assertEquals(UserRole.COORDINATOR, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUserWithLessRole_whenUpdateRole_thenInsufficientRolePermissionException() throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);
    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());
    User targetUser = personalManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.MANAGER),
        "User has not enought permission");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateLowerRole_thenUpdateRolSuccessfully() throws DuplicateInstanceException,
      InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);

    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    User targetUser = personalManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);
    personalManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.MEMBER);

    Assertions.assertEquals(UserRole.MEMBER, targetUser.getUserRole(),
        "Updated user role must be MEMBER");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateHigherRole_thenInsufficientRolePermissionException()
      throws DuplicateInstanceException {
    int totalUsers = 2;
    List<User> usersOmList = UserOM.withRandomNames(totalUsers);
    User userOm = usersOmList.get(0);
    User targetUserOm = usersOmList.get(1);

    User user = personalManagementFacade.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());
    User targetUser = personalManagementFacade.signUp(targetUserOm.getEmail(),
        targetUserOm.getPassword(),
        targetUserOm.getFirstName(),
        targetUserOm.getLastName(),
        String.valueOf(targetUserOm.getPhoneNumber()),
        targetUserOm.getDni());

    user.setUserRole(UserRole.MANAGER);
    targetUser.setUserRole(UserRole.MANAGER);

    Assertions.assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementFacade.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR),
        "User has not enough permission");

  }
}
