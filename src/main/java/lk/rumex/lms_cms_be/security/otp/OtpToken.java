package lk.rumex.lms_cms_be.security.otp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Entity @Table(name="otp_tokens", indexes = {
  @Index(name="idx_otp_type_email", columnList = "type,email")
})
@Data @NoArgsConstructor @AllArgsConstructor
public class OtpToken {
  public enum Type { SIGNUP_VERIFY, RESET_PASSWORD }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=32)
  private Type type;

  @Column(nullable=false, length=6)
  private String code;

  @Column(nullable=false)
  private Instant expiresAt;

  @Column(nullable=false)
  private boolean used;
}
