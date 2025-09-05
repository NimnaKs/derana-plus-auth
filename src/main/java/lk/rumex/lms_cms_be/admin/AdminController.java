package lk.rumex.lms_cms_be.admin;

import lk.rumex.lms_cms_be.admin.dto.AdminLoginRequest;
import lk.rumex.lms_cms_be.admin.dto.AdminSignupRequest;
import lk.rumex.lms_cms_be.auth.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService service;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody AdminSignupRequest r) {
    service.register(r);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AdminLoginRequest r) {
    return ResponseEntity.ok(new AuthResponse(service.login(r)));
  }
}
