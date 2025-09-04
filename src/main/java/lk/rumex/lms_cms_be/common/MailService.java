package lk.rumex.lms_cms_be.common;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mail;

  public void send(String to, String subject, String body) {
    var msg = new SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(body);
    mail.send(msg);
  }
}
