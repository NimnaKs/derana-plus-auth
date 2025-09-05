package lk.rumex.lms_cms_be.auth;

import jakarta.validation.Valid;
import lk.rumex.lms_cms_be.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService service;

  @GetMapping("/email-exists")
  public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
    return ResponseEntity.ok(service.emailExists(email));
  }

  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@RequestBody SignupRequest r) { service.signup(r); return ResponseEntity.ok().build(); }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verify(@RequestBody VerifyEmailRequest r) { service.verifyEmail(r); return ResponseEntity.ok().build(); }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest r) { return ResponseEntity.ok(new AuthResponse(service.login(r))); }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgot(@RequestBody ForgotPasswordRequest r) { service.forgot(r); return ResponseEntity.ok().build(); }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> reset(@RequestBody ResetPasswordRequest r) { service.reset(r); return ResponseEntity.ok().build(); }

  @PostMapping("/token")
  public ResponseEntity<AuthResponse> token(@RequestBody @Valid TokenRequest r) {
    return ResponseEntity.ok(new AuthResponse(service.requestToken(r)));
  }
}
