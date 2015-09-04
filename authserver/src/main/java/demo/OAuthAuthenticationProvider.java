package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OAuthAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserController userController;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    ResponseEntity<Void> user = userController.getUser(username, password);
    if (user.getStatusCode() == HttpStatus.FOUND) {
      return new UsernamePasswordAuthenticationToken(username, "password", null);
    } else {
      throw new UsernameNotFoundException("Username " + username + " not found");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return true;
  }

}
