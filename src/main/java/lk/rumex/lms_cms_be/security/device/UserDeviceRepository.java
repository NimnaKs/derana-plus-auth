package lk.rumex.lms_cms_be.security.device;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
  List<UserDevice> findByUserIdAndActiveTrue(Long userId);
  Optional<UserDevice> findByUserIdAndDeviceId(Long userId, String deviceId);
}
