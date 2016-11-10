package org.jddp.exception;

public class JDDPException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JDDPException() {
		
	}

	public JDDPException(String message) {
		super(message);
		
	}

	public JDDPException(Throwable cause) {
		super(cause);
	}

	public JDDPException(String message, Throwable cause) {
		super(message, cause);
	}

	public JDDPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
