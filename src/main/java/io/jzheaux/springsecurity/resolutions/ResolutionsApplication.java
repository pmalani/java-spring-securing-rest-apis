package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserRepositoryJwtAuthenticationConverter authenticationConverter;

    public static void main(String[] args) {
        SpringApplication.run(ResolutionsApplication.class, args);
    }

    @Bean
    public OpaqueTokenIntrospector introspector(UserRepository users, OAuth2ResourceServerProperties properties) {
        OAuth2ResourceServerProperties.Opaquetoken opaquetoken = properties.getOpaquetoken();
        NimbusOpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
                opaquetoken.getIntrospectionUri(), opaquetoken.getClientId(), opaquetoken.getClientSecret());
        return new UserRepositoryOpaqueTokenIntrospector(users, introspector);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return new UserRepositoryUserDetailsService(users);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authz -> authz
                        .anyRequest().authenticated())
                .httpBasic(basic -> { })
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken())
                .cors(cors -> { });
    }

    @Bean
    WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4000")
                        .allowedMethods("HEAD")
                        .allowedHeaders("Authorization");
            }
        };
    }

}
