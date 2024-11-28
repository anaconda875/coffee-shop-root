package com.example.coffeeshop.oauth2.converter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

@RequiredArgsConstructor
public class CustomJwtGrantedAuthoritiesConverter  implements Converter<Jwt, Collection<GrantedAuthority>> {

	private final Log logger = LogFactory.getLog(getClass());

	private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";

	private static final String DEFAULT_AUTHORITIES_CLAIM_DELIMITER = " ";

	private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");

	private final String clientId;

	private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;

	private String authoritiesClaimDelimiter = DEFAULT_AUTHORITIES_CLAIM_DELIMITER;

	private String authoritiesClaimName;

	/**
	 * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
	 * @param jwt The {@link Jwt} token
	 * @return The {@link GrantedAuthority authorities} read from the token scopes
	 */
	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for (String authority : getAuthorities(jwt)) {
			grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
		}

		for (String authority : getAuthoritiesFromResourceAccess(jwt)) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + authority));
		}

		return grantedAuthorities;
	}

	private List<String> getAuthoritiesFromResourceAccess(Jwt jwt) {
		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
		Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
		List<String> roles = (List<String>) client.get("roles");

		return roles;
	}


	public void setAuthorityPrefix(String authorityPrefix) {
		Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
		this.authorityPrefix = authorityPrefix;
	}


	public void setAuthoritiesClaimDelimiter(String authoritiesClaimDelimiter) {
		Assert.notNull(authoritiesClaimDelimiter, "authoritiesClaimDelimiter cannot be null");
		this.authoritiesClaimDelimiter = authoritiesClaimDelimiter;
	}


	public void setAuthoritiesClaimName(String authoritiesClaimName) {
		Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
		this.authoritiesClaimName = authoritiesClaimName;
	}

	private String getAuthoritiesClaimName(Jwt jwt) {
		if (this.authoritiesClaimName != null) {
			return this.authoritiesClaimName;
		}
		for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
			if (jwt.hasClaim(claimName)) {
				return claimName;
			}
		}
		return null;
	}

	private Collection<String> getAuthorities(Jwt jwt) {
		String claimName = getAuthoritiesClaimName(jwt);
		if (claimName == null) {
			this.logger.trace("Returning no authorities since could not find any claims that might contain scopes");
			return Collections.emptyList();
		}
		if (this.logger.isTraceEnabled()) {
			this.logger.trace(LogMessage.format("Looking for scopes in claim %s", claimName));
		}
		Object authorities = jwt.getClaim(claimName);
		if (authorities instanceof String) {
			if (StringUtils.hasText((String) authorities)) {
				return Arrays.asList(((String) authorities).split(this.authoritiesClaimDelimiter));
			}
			return Collections.emptyList();
		}
		if (authorities instanceof Collection) {
			return castAuthoritiesToCollection(authorities);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private Collection<String> castAuthoritiesToCollection(Object authorities) {
		return (Collection<String>) authorities;
	}

}

