package it.polimi.db2.gma.exceptions;

public class AlreadyExistingEmailException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public AlreadyExistingEmailException(String message) {
		super(message);
	}
}
