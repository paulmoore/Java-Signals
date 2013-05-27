// http://paulmoore.mit-license.org/

package jsignal;

/**
 * Defines an interface for signals from which listeners can be added or removed.
 */
public interface ISignal {

	/**
	 * Registers a listener to this signal.  If there is already a listener
	 * who's <code>Object.hashCode()</code> value is equal that to the listener
	 * argument, that listener is removed and returned.
	 * 
	 * @param listener the listener object
	 * @param callback the callback method
	 * @param addOnce if true, once this signal has dispatched the listener will be unregistered to this signal
	 * @return the previous listener with the same <code>Object.hasCode()</code> value as the listener argument, or null if no such listener exists
	 * @throws SignalException if the listener is null, or the callback method does not exist
	 */
	public Object add(Object listener, String callback, boolean addOnce);
	
	/**
	 * Unregisters a listener from this signal.
	 * 
	 * @param listener the listener to remove
	 * @return if the listener was successfully removed
	 */
	public boolean remove(Object listener);
	
	/**
	 * Returns whether or not a listener is registered to this signal.
	 * 
	 * @param listener the listener to check for
	 * @return if the listener is registered to this signal
	 */
	public boolean containsListener(Object listener);
	
	/**
	 * @return the number of listeners currently registered to this signal
	 */
	public int numListeners();
}
