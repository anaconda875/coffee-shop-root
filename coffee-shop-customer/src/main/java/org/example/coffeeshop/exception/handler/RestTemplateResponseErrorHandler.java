package org.example.coffeeshop.exception.handler;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		return httpResponse.getStatusCode().is5xxServerError() ||
				httpResponse.getStatusCode().is4xxClientError();
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		if ( httpResponse.getStatusCode().is5xxServerError() ) {
			//Handle SERVER_ERROR
			throw new HttpServerErrorException( httpResponse.getStatusCode() );
		}
		if ( httpResponse.getStatusCode().is4xxClientError() ) {
			throw new HttpClientErrorException( httpResponse.getStatusCode() );
		}
	}
}
