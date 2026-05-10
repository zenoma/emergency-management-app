package es.udc.emergencyproject.backend.model.entities.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByOrderByDniAsc();


  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  @Query("select distinct u from User u "
      + "join fetch u.mobileDevice md "
      + "where u.team.id = :teamId "
      + "and u.userRole in :roles "
      + "and md.fcmToken is not null")
  List<User> findTeamUsersWithMobileDeviceByTeamIdAndUserRoleIn(@Param("teamId") Long teamId,
      @Param("roles") Collection<UserRole> roles);

  @Query("select distinct u from User u "
      + "join fetch u.mobileDevice md "
      + "where u.team.id = :teamId "
      + "and md.fcmToken is not null")
  List<User> findTeamUsersWithMobileDeviceByTeamId(@Param("teamId") Long teamId);

}
