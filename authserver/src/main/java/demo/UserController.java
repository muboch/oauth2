package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
public class UserController {

  @Value("${url:http://localhost:8080/RestServices/gateway/}")
  private String url;

  @RequestMapping(value = "/{userName}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> getUser(String userIdent, @RequestHeader String userPassword) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("userName", userIdent);
    headers.set("userPassword", userPassword);
    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
    try {
      ResponseEntity<?> rsp = restTemplate.exchange(url.concat(userIdent), HttpMethod.GET, entity, String.class);
      if (rsp.getStatusCode() != HttpStatus.OK) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      } else {
        return ResponseEntity.status(HttpStatus.FOUND).build();
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
