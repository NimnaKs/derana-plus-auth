package lk.rumex.lms_cms_be.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lk.rumex.lms_cms_be.user.model.User;
import lk.rumex.lms_cms_be.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtVerifier {

  private final byte[] secret;
  private final UserRepository users;

  public JwtVerifier(@Value("${jwt.secret}") String secretBase64, UserRepository users) {
    this.secret = Base64.getDecoder().decode(secretBase64);
    this.users = users;
  }

  public Optional<Authentication> authenticate(String token) {
    try {
      var claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret)).build().parseSignedClaims(token).getPayload();
      Long userId = Long.parseLong(claims.getSubject());
      User u = users.findById(userId).orElse(null);
      if (u == null) return Optional.empty();
      String role = u.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
      var auth = new UsernamePasswordAuthenticationToken(
        u.getEmail(), null, Collections.singleton(new SimpleGrantedAuthority(role)));
      return Optional.of(auth);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
