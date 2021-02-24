package it.polimi.db2.gma.exceptions;

public class AlreadyExistingUserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AlreadyExistingUserException(String message) {
		super(message);
	}

}
