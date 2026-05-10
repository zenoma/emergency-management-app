package es.udc.emergencyproject.backend.model.entities.mobiledevice;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileDeviceRepository extends JpaRepository<MobileDevice, Long> {

  Optional<MobileDevice> findByFcmToken(String fcmToken);

  Optional<MobileDevice> findByUserId(Long userId);
}
