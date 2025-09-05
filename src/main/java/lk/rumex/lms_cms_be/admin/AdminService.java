package lk.rumex.lms_cms_be.admin;

import lk.rumex.lms_cms_be.admin.dto.AdminLoginRequest;
import lk.rumex.lms_cms_be.admin.dto.AdminSignupRequest;
import lk.rumex.lms_cms_be.auth.dto.TokenPair;
import lk.rumex.lms_cms_be.security.jwt.JwtTokenService;
import lk.rumex.lms_cms_be.user.Enum.AccountStatus;
import lk.rumex.lms_cms_be.user.Enum.LoginType;
import lk.rumex.lms_cms_be.user.model.User;
import lk.rumex.lms_cms_be.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtTokenService jwt;

  public void register(AdminSignupRequest r) {
    if (users.existsByEmail(r.email()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
    var u = new User();
    u.setEmail(r.email());
    u.setPassword(encoder.encode(r.password()));
    u.setFullName(r.fullName()==null? "" : r.fullName());
    u.setAccountStatus(AccountStatus.ACTIVE);
    u.setLoginType(LoginType.EMAIL);
    u.setEmailVerified(true);
    u.setAdmin(true);
    users.save(u);
  }

  public TokenPair login(AdminLoginRequest r) {
    var u = users.findByEmail(r.email())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!u.isAdmin() || !encoder.matches(r.password(), u.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    var access = jwt.accessToken(u.getId(), u.getEmail());
    var refresh = jwt.refreshToken(u.getId(), "admin", UUID.randomUUID().toString());
    return new TokenPair(access, refresh);
  }
}
