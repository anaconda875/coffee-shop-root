package org.example.coffeeshop.exception;

public class QueueNotAvailableException extends OrderException {

	public QueueNotAvailableException() {
		super( "Could not find any queue" );
	}
}
