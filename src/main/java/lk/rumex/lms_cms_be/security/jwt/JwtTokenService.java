package lk.rumex.lms_cms_be.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtTokenService {
  private final byte[] secret;
  private final String issuer;
  private final long accessTtlSec;
  private final long refreshTtlSec;

  public JwtTokenService(
      @Value("${jwt.secret}") String secretBase64,
      @Value("${jwt.issuer}") String issuer,
      @Value("${jwt.access-ttl-sec}") long accessTtlSec,
      @Value("${jwt.refresh-ttl-sec}") long refreshTtlSec) {
    this.secret = Base64.getDecoder().decode(secretBase64);
    this.issuer = issuer;
    this.accessTtlSec = accessTtlSec;
    this.refreshTtlSec = refreshTtlSec;
  }

  public String accessToken(Long userId, String email) {
    var now = Instant.now();
    return Jwts.builder()
      .issuer(issuer)
      .subject(String.valueOf(userId))
      .claim("email", email)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(accessTtlSec)))
      .signWith(Keys.hmacShaKeyFor(secret))
      .compact();
  }

  public String refreshToken(Long userId, String deviceId, String rtId) {
    var now = Instant.now();
    return Jwts.builder()
      .issuer(issuer)
      .subject(String.valueOf(userId))
      .claim("typ", "refresh")
      .claim("deviceId", deviceId)
      .claim("rtId", rtId)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(refreshTtlSec)))
      .signWith(Keys.hmacShaKeyFor(secret))
      .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret))
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public boolean isRefreshClaims(Claims claims) {
    String typ = claims.get("typ", String.class);
    return "refresh".equals(typ);
  }
}
