package com.example.coffeeshop.config;

import com.example.coffeeshop.oauth2.converter.CustomJwtGrantedAuthoritiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.cors( Customizer.withDefaults())
				.sessionManagement(s -> s.sessionCreationPolicy( SessionCreationPolicy.STATELESS ))
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()))
				.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter(@Value( "${app.security.oauth2.client-id}" ) String clientId) {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter(clientId));

		return converter;
	}

}
