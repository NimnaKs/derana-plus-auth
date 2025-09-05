package lk.rumex.lms_cms_be.user.model;

import jakarta.persistence.*;
import lk.rumex.lms_cms_be.user.Enum.AccountStatus;
import lk.rumex.lms_cms_be.user.Enum.LoginType;
import lk.rumex.ott_domain_models.clientPackage.model.ClientPackage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 190)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 120)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private LoginType loginType = LoginType.EMAIL;

  @Column(nullable = false)
  private boolean emailVerified = false;

  @Column(nullable = false)
  private boolean admin = false;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  // Netflix-style profiles (owned by user)
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Profile> profiles = new LinkedHashSet<>();

  @ManyToOne
  @JoinColumn(name = "clientPackages_id")
  private ClientPackage clientPackage;

}
