package es.udc.fireproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementServiceImpl;
import es.udc.fireproject.backend.utils.UserOM;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private final Long INVALID_USER_ID = -1L;
  @Mock
  UserRepository userRepository;

  @InjectMocks
  PersonalManagementServiceImpl personalManagementService;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    lenient().when(userRepository.existsByEmail(anyString())).thenReturn(false);
    lenient().when(userRepository.findById(any())).thenReturn(Optional.of(UserOM.withDefaultValues()));
    lenient().when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");
    lenient().when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserOM.withDefaultValues()));
    lenient().when(userRepository.save(any())).thenReturn(UserOM.withDefaultValues());
  }


  @Test
  void givenValidData_whenSignUpAndLoginFromId_thenUserIsFound()
      throws DuplicateInstanceException, InstanceNotFoundException {

    User user = UserOM.withDefaultValues();
    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    User loggedInUser = personalManagementService.loginFromId(user.getId());
    loggedInUser.setUserRole(UserRole.USER);
    loggedInUser.setPassword(user.getPassword());

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");
  }

  @Test
  void givenDuplicatedData_whenSignUp_thenDuplicateInstanceException() {

    User user = UserOM.withDefaultValues();
    when(userRepository.existsByEmail(anyString())).thenReturn(true);
    assertThrows(DuplicateInstanceException.class, () ->
            personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
                String.valueOf(user.getPhoneNumber()), user.getDni()),
        "DuplicateInstanceException expected");
  }

  @Test
  void givenInvalidData_whenLoginFromId_thenInstanceNotFoundException() {

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> personalManagementService.loginFromId(INVALID_USER_ID));
  }

  @Test
  void givenValidData_whenLogin_thenUserLoggedSuccessfully()
      throws DuplicateInstanceException, IncorrectLoginException {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementService.signUp(userOm.getEmail(),
        userOm.getPassword(),
        userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()),
        userOm.getDni());

    String clearPassword = userOm.getPassword();

    User loggedInUser = personalManagementService.login(userOm.getEmail(), clearPassword);
    loggedInUser.setPassword(userOm.getPassword());
    loggedInUser.setUserRole(UserRole.USER);

    Assertions.assertEquals(user, loggedInUser, "Users must be the same");

  }

  @Test
  void givenInvalidPassword_whenLogin_thenIncorrectLoginException() throws DuplicateInstanceException {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    User user = UserOM.withDefaultValues();

    String clearPassword = user.getPassword();

    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    assertThrows(IncorrectLoginException.class,
        () -> personalManagementService.login(user.getEmail(), 'X' + clearPassword), " Password must be incorrect");

  }

  @Test
  void givenInvalidEmail_whenLogin_thenIncorrectLoginException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThrows(IncorrectLoginException.class, () -> personalManagementService.login("X", "Y"),
        " User must not exist");
  }

  @Test
  void givenValidData_whenUpdateProfile_thenUserUpdatedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(("password"))).thenReturn("password");

    User user = UserOM.withDefaultValues();
    user.setId(1L);

    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    User updatedUser = personalManagementService.updateProfile(user.getId(), 'X' + user.getFirstName(),
        'X' + user.getLastName(), 'X' + user.getEmail(), 111111111, "11111111S");
    updatedUser.setId(user.getId());
    updatedUser.setUserRole(user.getUserRole());

    when(userRepository.findById(any())).thenReturn(Optional.of(updatedUser));

    user.setFirstName('X' + user.getFirstName());
    user.setLastName('X' + user.getLastName());
    user.setEmail('X' + user.getEmail());
    user.setPhoneNumber(111111111);
    user.setDni("11111111S");

    Assertions.assertEquals(user, personalManagementService.loginFromId(user.getId()), "User must be updated");

  }

  @Test
  void givenInvalidData_whenUpdateProfile_thenInstanceNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> personalManagementService.updateProfile(INVALID_USER_ID, "X", "X", "X", 111111111, "11111111S"),
        "User not existent");
  }

  @Test
  void givenValidData_whenChangePassword_thenPasswordSuccessfullyChanged()
      throws DuplicateInstanceException, InstanceNotFoundException, IncorrectPasswordException {

    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.findById(any())).thenReturn(Optional.of(UserOM.withDefaultValues()));
    when(passwordEncoder.encode(("password"))).thenReturn("password");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserOM.withDefaultValues()));

    User user = UserOM.withDefaultValues();

    String oldPassword = user.getPassword();
    String newPassword = 'X' + oldPassword;
    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());

    personalManagementService.changePassword(user.getId(), oldPassword, newPassword);
    user.setPassword(newPassword);

    Assertions.assertDoesNotThrow(() -> personalManagementService.login(user.getEmail(), newPassword));
  }

  @Test
  void givenInvalidID_whenChangePassword_thenInstanceNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(InstanceNotFoundException.class,
        () -> personalManagementService.changePassword(INVALID_USER_ID, "X", "Y"), "User non existent");
  }

  @Test
  void givenIncorrectPassword_whenChangePassword_thenInstanceNotFoundException() throws DuplicateInstanceException {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.findById(any())).thenReturn(Optional.of(UserOM.withDefaultValues()));
    when(passwordEncoder.encode(("password"))).thenReturn("password");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    User user = UserOM.withDefaultValues();

    String oldPassword = user.getPassword();
    String newPassword = 'X' + oldPassword;

    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());
    assertThrows(IncorrectPasswordException.class,
        () -> personalManagementService.changePassword(user.getId(), 'Y' + oldPassword, newPassword),
        "IncorrectPassword Exception expected");

  }

  @Test
  void givenValidData_whenSignUp_thenUserHasUserRole() throws DuplicateInstanceException {

    User userOm = UserOM.withDefaultValues();

    User user = personalManagementService.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    Assertions.assertEquals(UserRole.USER, user.getUserRole(), "Role must be USER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateLowerRole_thenUpdateRolSuccessfully()
      throws DuplicateInstanceException, InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    user.setUserRole(UserRole.COORDINATOR);
    user.setId(0L);

    User targetUser = userList.get(1);
    targetUser.setUserRole(UserRole.MANAGER);
    targetUser.setId(1L);

    when(userRepository.findById(0L)).thenReturn(Optional.of(user));
    when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

    personalManagementService.signUp(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
        String.valueOf(user.getPhoneNumber()), user.getDni());
    personalManagementService.signUp(targetUser.getEmail(), targetUser.getPassword(), targetUser.getFirstName(),
        targetUser.getLastName(),
        String.valueOf(targetUser.getPhoneNumber()), targetUser.getDni());

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUsersWithHigherRole_whenUpdateHigherRole_thenUpdateRolSuccessfully()
      throws InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    user.setUserRole(UserRole.COORDINATOR);
    user.setId(0L);

    User targetUser = userList.get(1);
    targetUser.setUserRole(UserRole.MANAGER);
    targetUser.setId(1L);

    when(userRepository.findById(0L)).thenReturn(Optional.of(user));
    when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR);

    Assertions.assertEquals(UserRole.COORDINATOR, targetUser.getUserRole(), "Role must be MANAGER");

  }

  @Test
  void giveUserWithLessRole_whenUpdateRole_thenInsufficientRolePermissionException() {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    user.setUserRole(UserRole.USER);
    user.setId(0L);

    User targetUser = userList.get(1);
    targetUser.setUserRole(UserRole.MANAGER);
    targetUser.setId(1L);

    when(userRepository.findById(0L)).thenReturn(Optional.of(user));
    when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

    assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.MANAGER),
        "User has not enought permission");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateLowerRole_thenUpdateRolSuccessfully()
      throws InstanceNotFoundException, InsufficientRolePermissionException {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    user.setUserRole(UserRole.MANAGER);
    user.setId(0L);

    User targetUser = userList.get(1);
    targetUser.setUserRole(UserRole.MANAGER);
    targetUser.setId(1L);

    when(userRepository.findById(0L)).thenReturn(Optional.of(user));
    when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

    personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.USER);

    Assertions.assertEquals(UserRole.USER, targetUser.getUserRole(), "Updated user role must be USER");

  }

  @Test
  void giveUsersWithSameRole_whenUpdateHigherRole_thenInsufficientRolePermissionException() {
    int totalUsers = 2;
    List<User> userList = UserOM.withRandomNames(totalUsers);
    User user = userList.get(0);
    user.setUserRole(UserRole.MANAGER);
    user.setId(0L);

    User targetUser = userList.get(1);
    targetUser.setUserRole(UserRole.MANAGER);
    targetUser.setId(1L);

    when(userRepository.findById(0L)).thenReturn(Optional.of(user));
    when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

    assertThrows(InsufficientRolePermissionException.class,
        () -> personalManagementService.updateRole(user.getId(), targetUser.getId(), UserRole.COORDINATOR),
        "User has not enough permission");

  }


}
