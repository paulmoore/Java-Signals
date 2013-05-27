// http://paulmoore.mit-license.org/

package jsignal;

/**
 * General exception for Signals.
 */
public final class SignalException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	protected SignalException(String message) {
		super(message);
	}

	protected SignalException(String message, Exception inner) {
		super(message, inner);
	}
}
