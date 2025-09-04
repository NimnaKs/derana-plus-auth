package lk.rumex.lms_cms_be.me.dto;

public record SettingsUpdateRequest(Boolean emailNotifications, Boolean twoFactorEmail, String language) {}
