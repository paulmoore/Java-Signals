// http://paulmoore.mit-license.org/

package jsignal;

/**
 * Defines an interface for a signal which has full control of adding, removing,
 * removing all listeners, as well as dispatching capabilities.
 * Intended for objects that own or need full control over a signal instance.
 */
public interface ISignalOwner extends ISignal, IDispatcher {

	/**
	 * Unregisters all listeners to this signal.
	 */
	public void removeAll();
}
