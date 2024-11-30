package org.example.coffeeshop.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.example.coffeeshop.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ HttpServerErrorException.class, HttpClientErrorException.class })
	ResponseEntity<ApiErrorResponse> handleOrderException(HttpStatusCodeException e, HttpServletRequest request) {
		return ResponseEntity.status( e.getStatusCode() ).body( new ApiErrorResponse(
				e.getStatusCode().value(),
				e.getMessage(),
				request.getContextPath() + request.getServletPath()
		) );
	}

}
