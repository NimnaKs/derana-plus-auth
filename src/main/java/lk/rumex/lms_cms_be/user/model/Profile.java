package lk.rumex.lms_cms_be.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity @Table(name="profiles")
@Data @NoArgsConstructor @AllArgsConstructor
public class Profile {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false, length = 60)
  private String name;

  @Column(nullable = false, length = 16)
  private String maturityLevel; // KIDS, TEEN, ADULT (string for compatibility)

  private String avatarUrl;
  private String language = "EN";

  private boolean autoplayNext = true;
  private boolean autoplayPreviews = false;

  private String pin; // optional 4 digits for kids
}
