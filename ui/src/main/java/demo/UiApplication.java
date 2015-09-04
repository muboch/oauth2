package demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class UiApplication {

  public static void main(String[] args) {
    SpringApplication.run(UiApplication.class, args);
  }

  @Configuration
  protected static class SecurityConfiguration extends OAuth2SsoConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.logout().and().antMatcher("/**").authorizeRequests().antMatchers("/index.html", "/home.html", "/", "/login")
          .permitAll().anyRequest().authenticated().and().csrf().csrfTokenRepository(csrfTokenRepository()).and()
          .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
      http.addFilterBefore(accessDeniedInterceptor(), FilterSecurityInterceptor.class);
    }

    private Filter csrfHeaderFilter() {
      return new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
          CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
          if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String token = csrf.getToken();
            if (cookie == null || token != null && !token.equals(cookie.getValue())) {
              cookie = new Cookie("XSRF-TOKEN", token);
              cookie.setPath("/");
              response.addCookie(cookie);
            }
          }
          filterChain.doFilter(request, response);
        }
      };
    }

    private CsrfTokenRepository csrfTokenRepository() {
      HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
      repository.setHeaderName("X-XSRF-TOKEN");
      return repository;
    }

    /**
     * If the resource is protected the Spring Framework throws an AccessDenied exception which then
     * results in a "redirect to login page" response (302). <br/>
     * This filter catches the exception and creates an "unauthorized" response (401) instead.
     * 
     * @return The filter.
     */
    private Filter accessDeniedInterceptor() {
      return new GenericFilterBean() {
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
          final HttpServletRequest request = (HttpServletRequest) req;
          final HttpServletResponse response = (HttpServletResponse) res;
          try {
            filterChain.doFilter(request, response);
          } catch (AccessDeniedException accessDeniedException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, accessDeniedException.getMessage());
          }
        }
      };
    }
  }

}
