package org.example.coffeeshop.exception;

public class QueueNotAvailableException extends BookingException {

	public QueueNotAvailableException() {
		super( "Could not find any queue" );
	}
}
