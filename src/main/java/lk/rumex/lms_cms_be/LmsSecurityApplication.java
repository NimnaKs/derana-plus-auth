package lk.rumex.lms_cms_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"lk.rumex.ott_domain_models.clientPackage"})
public class LmsSecurityApplication {
  public static void main(String[] args) {
    SpringApplication.run(LmsSecurityApplication.class, args);
  }
}
