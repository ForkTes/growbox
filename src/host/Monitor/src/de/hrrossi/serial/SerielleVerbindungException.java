package de.hrrossi.serial;

public class SerielleVerbindungException extends Exception {

	/**  */
	private static final long serialVersionUID = 3157283576289473432L;

	public SerielleVerbindungException(String aMessage) {
		super(aMessage);
	}

	public SerielleVerbindungException(String aMessage, Throwable aTriggerException) {
		super(aMessage, aTriggerException);
	}

}
