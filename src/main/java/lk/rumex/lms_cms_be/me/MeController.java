package lk.rumex.lms_cms_be.me;

import lk.rumex.lms_cms_be.me.dto.*;
import lk.rumex.lms_cms_be.security.device.UserDevice;
import lk.rumex.lms_cms_be.security.device.UserDeviceRepository;
import lk.rumex.lms_cms_be.user.model.Profile;
import lk.rumex.lms_cms_be.user.model.User;
import lk.rumex.lms_cms_be.user.model.UserSettings;
import lk.rumex.lms_cms_be.user.repo.ProfileRepository;
import lk.rumex.lms_cms_be.user.repo.UserRepository;
import lk.rumex.lms_cms_be.user.repo.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

  private final UserRepository users;
  private final ProfileRepository profiles;
  private final UserSettingsRepository settings;
  private final UserDeviceRepository devices;

  private Long userId(Authentication auth) {
    var u = users.findByEmail(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return u.getId();
  }

  // Settings
  @GetMapping("/settings")
  public UserSettings getSettings(Authentication auth) {
    Long uid = userId(auth);
    return settings.findByUserId(uid).orElseGet(() -> settings.save(UserSettings.builder().userId(uid).language("EN").emailNotifications(true).twoFactorEmail(false).build()));
  }

  @PutMapping("/settings")
  public UserSettings updateSettings(Authentication auth, @RequestBody SettingsUpdateRequest r) {
    Long uid = userId(auth);
    var s = settings.findByUserId(uid).orElseThrow();
    if (r.emailNotifications()!=null) s.setEmailNotifications(r.emailNotifications());
    if (r.twoFactorEmail()!=null) s.setTwoFactorEmail(r.twoFactorEmail());
    if (r.language()!=null) s.setLanguage(r.language());
    return settings.save(s);
  }

  // Profiles
  @GetMapping("/profiles")
  public List<Profile> listProfiles(Authentication auth) {
    Long uid = userId(auth);
    return profiles.findByUserId(uid);
  }

  @PostMapping("/profiles")
  public Profile addProfile(Authentication auth, @RequestBody ProfileCreateRequest r) {
    Long uid = userId(auth);
    var u = users.findById(uid).orElseThrow();
    var p = new Profile();
    p.setUser(u);
    p.setName(r.name());
    p.setMaturityLevel(r.maturityLevel()==null? "ADULT" : r.maturityLevel());
    p.setAvatarUrl(r.avatarUrl());
    p.setLanguage(r.language()==null? "EN" : r.language());
    p.setPin(r.pin());
    return profiles.save(p);
  }

  @DeleteMapping("/profiles/{profileId}")
  public void deleteProfile(Authentication auth, @PathVariable Long profileId) {
    Long uid = userId(auth);
    var p = profiles.findById(profileId).orElseThrow();
    if (!p.getUser().getId().equals(uid)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    profiles.delete(p);
  }

  // Devices
  @GetMapping("/devices")
  public List<UserDevice> myDevices(Authentication auth) {
    Long uid = userId(auth);
    return devices.findByUserIdAndActiveTrue(uid);
  }

  @PostMapping("/devices/upsert")
  public UserDevice upsert(Authentication auth, @RequestBody DeviceUpsertRequest r) {
    Long uid = userId(auth);
    var d = devices.findByUserIdAndDeviceId(uid, r.deviceId()).orElseGet(() -> {
      var x = new UserDevice(); x.setUserId(uid); x.setDeviceId(r.deviceId()); return x;
    });
    d.setDeviceName(r.deviceName());
    d.setDeviceType(r.deviceType());
    d.setActive(true);
    d.setLastSeenAt(Instant.now());
    return devices.save(d);
  }

  @DeleteMapping("/devices/{id}")
  public void revoke(Authentication auth, @PathVariable Long id) {
    Long uid = userId(auth);
    var d = devices.findById(id).orElseThrow();
    if (!d.getUserId().equals(uid)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    d.setActive(false);
    devices.save(d);
  }
}
