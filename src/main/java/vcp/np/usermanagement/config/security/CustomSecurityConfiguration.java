package vcp.np.usermanagement.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {


	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("\n>>>>>>>>>> Customizing security configuration >>>>>>>>>>");

		// return http
		// 		// .formLogin(form -> form
		// 		// 		.loginPage("/cas/login").permitAll())
		// 		.authorizeHttpRequests(auth -> auth
		// 				.requestMatchers( "/cas/**", "/", "/user/**")
		// 				.permitAll()
		// 				.anyRequest()
		// 				.authenticated())
						
		// 		.build();


		return http
                .csrf(csrf -> csrf // Disable CSRF protection (not recommended for production)
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST).permitAll() // Allow POST to /api/user/list without authentication
                        .requestMatchers("/cas/**", "/", "/user/**", "/client/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .build();
	}

}
