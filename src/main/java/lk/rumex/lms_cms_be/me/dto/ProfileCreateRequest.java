package lk.rumex.lms_cms_be.me.dto;

public record ProfileCreateRequest(String name, String maturityLevel, String avatarUrl, String language, String pin) {
}
