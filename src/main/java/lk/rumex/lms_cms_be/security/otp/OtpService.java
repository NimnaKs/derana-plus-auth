package lk.rumex.lms_cms_be.security.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service @RequiredArgsConstructor
public class OtpService {
  private final OtpTokenRepository repo;
  private final SecureRandom random = new SecureRandom();

  public String issue(String email, OtpToken.Type type, int minutes) {
    String code = String.format("%06d", random.nextInt(1_000_000));
    var t = new OtpToken();
    t.setEmail(email);
    t.setType(type);
    t.setCode(code);
    t.setExpiresAt(Instant.now().plus(minutes, ChronoUnit.MINUTES));
    t.setUsed(false);
    repo.save(t);
    return code;
  }

  public boolean validateAndConsume(String email, OtpToken.Type type, String code) {
    var opt = repo.findTopByEmailAndTypeAndUsedIsFalseOrderByIdDesc(email, type);
    if (opt.isEmpty()) return false;
    var t = opt.get();
    if (t.isUsed()) return false;
    if (!t.getCode().equals(code)) return false;
    if (Instant.now().isAfter(t.getExpiresAt())) return false;
    t.setUsed(true);
    repo.save(t);
    return true;
  }
}
