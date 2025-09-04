package lk.rumex.lms_cms_be.security.device;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Entity @Table(name="user_devices", indexes = {
  @Index(name="idx_device_user", columnList = "userId"),
  @Index(name="idx_device_deviceId", columnList = "deviceId", unique = true)
})
@Data @NoArgsConstructor @AllArgsConstructor
public class UserDevice {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false)
  private Long userId;

  @Column(nullable=false, length=64)
  private String deviceId; // uuid from client

  private String deviceName;   // "Chrome on Windows", "Pixel 7"
  private String deviceType;   // WEB / ANDROID / IOS / TV
  private String appVersion;
  private String platformVersion;

  @Column(nullable=false)
  private boolean active = true;

  @Column(nullable=false)
  private Instant createdAt = Instant.now();

  private Instant lastSeenAt;
  private String refreshTokenId;
}
