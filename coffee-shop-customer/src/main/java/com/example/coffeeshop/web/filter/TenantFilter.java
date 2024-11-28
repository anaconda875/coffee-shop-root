package com.example.coffeeshop.web.filter;

import java.io.IOException;

import com.example.coffeeshop.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String tenantName = request.getHeader( "X-Tenant-Id" );
		TenantContext.setCurrentTenant( tenantName );

		try {
			filterChain.doFilter( request, response );
		}
		finally {
			TenantContext.setCurrentTenant( "" );
		}
	}
}
