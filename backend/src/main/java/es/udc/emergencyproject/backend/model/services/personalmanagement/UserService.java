package es.udc.emergencyproject.backend.model.services.personalmanagement;

import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRole;
import es.udc.emergencyproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.emergencyproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InsufficientRolePermissionException;
import java.util.List;


public interface UserService {


  List<User> findAllUsers();

  User signUp(String email, String password, String firstName, String lastName, String phoneNumber, String dni)
      throws DuplicateInstanceException;

  User login(String email, String password) throws IncorrectLoginException;

  User loginFromId(Long id) throws InstanceNotFoundException;

  User updateProfile(Long id, String firstName, String lastName, String email, Integer phoneNumber, String dni)
      throws InstanceNotFoundException;

  void changePassword(Long id, String oldPassword, String newPassword)
      throws InstanceNotFoundException, IncorrectPasswordException;

  void updateRole(Long id, Long targetId, UserRole userRole) throws InstanceNotFoundException,
      InsufficientRolePermissionException;

}
