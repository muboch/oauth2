package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableOAuth2Resource
public class ResourceApplication {

  @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<String> getHome() {
    String id = UUID.randomUUID().toString();
    String content = "Hello Musti!!";
    List<String> a = new ArrayList<>();
    a.add(id);
    a.add(content);
    return a;
  }


  public static void main(String[] args) {
    SpringApplication.run(ResourceApplication.class, args);
  }


}

