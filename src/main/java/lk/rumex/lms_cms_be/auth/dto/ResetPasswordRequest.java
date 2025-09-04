package lk.rumex.lms_cms_be.auth.dto;

public record ResetPasswordRequest(String email, String code, String newPassword) {
}
