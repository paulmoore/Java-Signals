package com.paulm.jsignal;

/**
 * General exception for Signals.
 * 
 * @author Paul
 */
public final class SignalException extends Exception
{
	private static final long serialVersionUID = 8390671431549393525L;

	protected SignalException (String message)
	{
		super(message);
	}
}
