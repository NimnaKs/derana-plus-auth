package lk.rumex.lms_cms_be.user.repo;

import lk.rumex.lms_cms_be.user.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
  Optional<UserSettings> findByUserId(Long userId);
}
