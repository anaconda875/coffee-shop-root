package org.example.coffeeshop.exception.handler;

import org.example.coffeeshop.dto.response.ApiErrorResponse;
import org.example.coffeeshop.exception.OrderException;
import org.example.coffeeshop.exception.QueueNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ OrderException.class, QueueNotAvailableException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	Mono<ApiErrorResponse> handleOrderException(RuntimeException e, ServerRequest serverRequest) {
		return Mono.just( new ApiErrorResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
																						serverRequest.path()
		) );
	}

}
