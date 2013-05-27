// http://paulmoore.mit-license.org/

package jsignal;

/**
 * Defines an interface for event dispatching types.
 */
public interface IDispatcher {

	/**
	 * Dispatches the arguments to all listeners registered to the dispatcher.
	 * 
	 * @param args the arguments to dispatch with
	 * @throws SignalException if a slot callback method could not be invoked 
	 */
	public void dispatch(Object... args);
}
