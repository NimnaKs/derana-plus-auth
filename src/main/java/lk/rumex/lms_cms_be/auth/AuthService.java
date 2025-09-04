package lk.rumex.lms_cms_be.auth;

import io.jsonwebtoken.Claims;
import lk.rumex.lms_cms_be.auth.dto.*;
import lk.rumex.lms_cms_be.common.MailService;
import lk.rumex.lms_cms_be.security.device.UserDeviceRepository;
import lk.rumex.lms_cms_be.security.device.UserDevice;
import lk.rumex.lms_cms_be.security.jwt.JwtTokenService;
import lk.rumex.lms_cms_be.security.otp.OtpService;
import lk.rumex.lms_cms_be.security.otp.OtpToken;
import lk.rumex.lms_cms_be.user.Enum.AccountStatus;
import lk.rumex.lms_cms_be.user.Enum.LoginType;
import lk.rumex.lms_cms_be.user.model.User;
import lk.rumex.lms_cms_be.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AuthService {

  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final OtpService otp;
  private final MailService mail;
  private final JwtTokenService jwt;
  private final UserDeviceRepository devices;

  @Value("${devices.max-per-user:3}")
  private int maxDevices;

  @Transactional
  public void signup(SignupRequest r) {
    if (users.existsByEmail(r.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
    var u = new User();
    u.setEmail(r.email());
    u.setPassword(encoder.encode(r.password()));
    u.setFullName(r.fullName());
    u.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
    u.setLoginType(LoginType.EMAIL);
    users.save(u);
    var code = otp.issue(r.email(), OtpToken.Type.SIGNUP_VERIFY, 15);
    mail.send(r.email(), "Verify your account", "Your verification code is: " + code + " (valid 15 minutes)");
  }

  @Transactional
  public void verifyEmail(VerifyEmailRequest r) {
    boolean ok = otp.validateAndConsume(r.email(), OtpToken.Type.SIGNUP_VERIFY, r.code());
    if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
    var u = users.findByEmail(r.email()).orElseThrow();
    u.setAccountStatus(AccountStatus.ACTIVE);
    u.setEmailVerified(true);
    users.save(u);
  }

  @Transactional
  public TokenPair login(LoginRequest r) {
    var u = users.findByEmail(r.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (u.getAccountStatus() != AccountStatus.ACTIVE) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not verified");
    if (!encoder.matches(r.password(), u.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

    var refreshTokenId = UUID.randomUUID().toString();
    var access = jwt.accessToken(u.getId(), u.getEmail());
    var refresh = jwt.refreshToken(u.getId(), r.deviceId(), refreshTokenId);

    var active = devices.findByUserIdAndActiveTrue(u.getId());
    boolean sameDevice = active.stream().anyMatch(d -> d.getDeviceId().equals(r.deviceId()));
    if (!sameDevice && active.size() >= maxDevices) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Device limit reached");
    }
    var dev = devices.findByUserIdAndDeviceId(u.getId(), r.deviceId())
      .orElseGet(() -> { var d=new UserDevice(); d.setUserId(u.getId()); d.setDeviceId(r.deviceId()); return d; });
    dev.setDeviceName(r.deviceName());
    dev.setDeviceType(r.platform());
    dev.setActive(true);
    dev.setLastSeenAt(Instant.now());
    dev.setRefreshTokenId(refreshTokenId);
    devices.save(dev);

    return new TokenPair(access, refresh);
  }

  @Transactional
  public void forgot(ForgotPasswordRequest r) {
    users.findByEmail(r.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account"));
    var code = otp.issue(r.email(), OtpToken.Type.RESET_PASSWORD, 10);
    mail.send(r.email(), "Reset your password", "Your reset code is: " + code + " (valid 10 minutes)");
  }

  @Transactional
  public void reset(ResetPasswordRequest r) {
    boolean ok = otp.validateAndConsume(r.email(), OtpToken.Type.RESET_PASSWORD, r.code());
    if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
    var u = users.findByEmail(r.email()).orElseThrow();
    u.setPassword(encoder.encode(r.newPassword()));
    users.save(u);
  }

  @Transactional
  public TokenPair requestToken(TokenRequest req) {
    if (req == null || req.refreshToken() == null || req.refreshToken().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refreshToken is required");
    }

    Claims claims;
    try {
      claims = jwt.parseClaims(req.refreshToken());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    if (!jwt.isRefreshClaims(claims)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong token type");
    }

    Long userId = Long.parseLong(claims.getSubject());
    String deviceId = claims.get("deviceId", String.class);
    String rtId = claims.get("rtId", String.class);

    var dev = devices.findByUserIdAndDeviceId(userId, deviceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device not found"));

    // must still be active and carrying same refreshTokenId
    if (!dev.isActive() || dev.getRefreshTokenId() == null || !dev.getRefreshTokenId().equals(rtId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revoked");
    }

    // rotate
    String newRtId = UUID.randomUUID().toString();
    dev.setRefreshTokenId(newRtId);
    dev.setLastSeenAt(Instant.now());
    devices.save(dev);

    var user = users.findById(userId).orElseThrow();
    String newAccess = jwt.accessToken(userId, user.getEmail());
    String newRefresh = jwt.refreshToken(userId, deviceId, newRtId);

    return new TokenPair(newAccess, newRefresh);
  }
}
