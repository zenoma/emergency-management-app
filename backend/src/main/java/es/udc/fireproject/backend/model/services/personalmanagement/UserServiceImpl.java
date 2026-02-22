package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.entities.user.UserRole;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.fireproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.InsufficientRolePermissionException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND = "User not found";
  private static final String TARGET_USER_NOT_FOUND = "Target user not found";
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private User getUser(String email, String password, String firstName, String lastName, String phoneNumber, String dni,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    User user = new User(email, password, firstName, lastName, dni,
        phoneNumber != null ? Integer.parseInt(phoneNumber) : null);

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new DuplicateInstanceException("project.entities.user", user.getEmail());
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    user.setUserRole(UserRole.USER);

    return userRepository.save(user);
  }

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAllByOrderByDniAsc();
  }

  @Override
  public User signUp(String email,
      String password,
      String firstName,
      String lastName,
      String phoneNumber,
      String dni)
      throws DuplicateInstanceException {

    return getUser(email, password, firstName, lastName, phoneNumber, dni, userRepository, passwordEncoder);

  }

  @Override
  @Transactional(readOnly = true)
  public User login(String email, String password) throws IncorrectLoginException {

    User user = userRepository.findByEmail(email).orElseThrow(() -> new IncorrectLoginException(email));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IncorrectLoginException(email);
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
