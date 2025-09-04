package lk.rumex.lms_cms_be.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lk.rumex.lms_cms_be.security.jwt.JwtVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter {
  private final JwtVerifier jwtVerifier;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    var http = (HttpServletRequest) request;
    var auth = http.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      jwtVerifier.authenticate(auth.substring(7)).ifPresent(authentication -> {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      });
    }
    chain.doFilter(request, response);
  }
}
