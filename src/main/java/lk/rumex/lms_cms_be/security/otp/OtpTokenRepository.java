package lk.rumex.lms_cms_be.security.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
  Optional<OtpToken> findTopByEmailAndTypeAndUsedIsFalseOrderByIdDesc(String email, OtpToken.Type type);
}
