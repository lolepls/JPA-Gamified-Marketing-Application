package it.polimi.db2.gma.exceptions;

public class AlreadyExistingBadwordException extends Exception {
	private static final long serialVersionUID = 1L;
	public AlreadyExistingBadwordException(String message) {
		super(message);
	}
}
