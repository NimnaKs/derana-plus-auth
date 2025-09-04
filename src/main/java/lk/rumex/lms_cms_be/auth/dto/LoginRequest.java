package lk.rumex.lms_cms_be.auth.dto;

public record LoginRequest(String email, String password, String deviceId, String deviceName, String platform) {
}
