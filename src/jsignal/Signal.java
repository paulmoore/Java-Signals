// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Signal class acts as an event dispatcher for
 * one type of event.  Each listener object registers
 * directly to the object, no constant event type
 * identifiers are required.
 * 
 * This class logs to the </code>"com.paulm.jsignal"</code> Logger potential problems.
 * 
 * This is a port of Robert Penner's Signals for ActionScript 3.0
 */
public class Signal implements ISignalOwner {
	protected final Class<?>[] params;
	protected final Map<Object, ISlot> listenerMap = new HashMap<Object, ISlot>();
	
	/**
	 * Constructor
	 * 
	 * @param params the parameter types (as a Class instance) that this signal will dispatch as event data
	 */
	public Signal(Class<?>... params) {
		this.params = params;
	}
	
	/**
	 * Adds a listener object to this signal.
	 * 
	 * All callback methods <b>must</b> be
	 * declared as <i>public</i> and <b>must</i> have the same <i>parameter signature</i> this signal
	 * was constructed with.
	 * 
	 * Listeners are put into a map and are keyed by their <code>hashCode()</code> value.  To ensure
	 * unique listenerMap are registered to this signal, ensure their <code>hashCode()</code> value is unique.
	 * 
	 * @param listener the listener object to add
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched
	 * @param addOnce if true, once this signal has dispatched the listener is removed from the listener map
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 * @throws SignalException if a security violation occurs while retrieving the callback method, or if no such method exists
	 */
	@Override
	public Object add (Object listener, String callback, boolean addOnce) {
		Method delegate;
		try {
			delegate = listener.getClass().getMethod(callback, params);
		} catch (SecurityException e) {
			throw new SignalException("Could not access method `"+listener.getClass().getName()+"."+callback+"`", e);
		} catch (NoSuchMethodException e) {
			throw new SignalException("Could not find method `"+listener.getClass().getName()+"."+callback+"`", e);
		}
		ISlot previous = listenerMap.put(listener, new Slot(listener, delegate, addOnce));
		return previous == null ? null : previous.getListener();
	}
	
	/**
	 * Adds a listener object to this signal.
	 * 
	 * All callback methods <b>must</b> be
	 * declared as <i>public</i> and <b>must</i> have the same <i>parameter signature</i> this signal
	 * was constructed with.
	 * 
	 * Listeners are put into a map and are keyed by their <code>hashCode()</code> value.  To ensure
	 * unique listenerMap are registered to this signal, ensure their <code>hashCode()</code> value is unique.
	 * 
	 * This is an overloaded version of <code>Signal.add(Object, String, boolean)</code>.  By default, addOnce
	 * is <b>false</b>.
	 * 
	 * @param listener the listener object to add
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 * @throws SignalException if a security violation occurs while retrieving the callback method, or if no such method exists
	 */
	public Object add(Object listener, String callback) {
		return add(listener, callback, false);
	}
	
	/**
	 * Removes the listener from the listener map.  Listeners are found by the value of their <code>hashCode()</code> method.
	 * 
	 * @param listener the listener to remove
	 * @return if the listener was successfully removed
	 */
	@Override
	public boolean remove(Object listener) {
		return listenerMap.remove(listener) != null;
	}
	
	/* (non-Javadoc)
	 * @see com.paulm.jsignal.ISignalOwner#removeAll()
	 */
	public void removeAll () {
		listenerMap.clear();
	}
	
	/**
	 * Dispatches this Signal to all listenerMap using the given arguments.
	 * 
	 * @param args the argument list to dispatch to listenerMap.  The argument
	 * list must have the same signature as the parameter list this Signal
	 * was constructed with
	 * @throws SignalException if the wrong arguments were supplied, or a callback could not be accessed or invoked
	 */
	@Override
	public void dispatch(Object... args) {
		Iterator<ISlot> iterator = listenerMap.values().iterator();
		while (iterator.hasNext()) {
			ISlot slot = iterator.next();
			try {
				slot.getDelegate().invoke(slot.getListener(), args);
			} catch (IllegalArgumentException e) {
				throw new SignalException("Method "+slot.getDelegate()+" received an invalid argument "+Arrays.deepToString(args), e);
			} catch (IllegalAccessException e) {
				throw new SignalException("Could not access method "+slot.getDelegate(), e);
			} catch (InvocationTargetException e) {
				throw new SignalException("Could not invoke method "+slot.getDelegate(), e);
			}
			if (slot.getAddOnce()) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Checks to see if a given listener has been registered to this signal.
	 * 
	 * @param listener the listener to check for
	 * @return if the listener found in the map given by the listener's <code>hashCode()</code> value
	 * is equal to the listener in question given by the result of the <code>equals(Object)</code> method
	 */
	@Override
	public boolean containsListener(Object listener) {
		ISlot slot = listenerMap.get(listener);
		return slot == null ? false : slot.getListener().equals(listener);
	}
	
	/**
	 * Get the number of listenerMap currently registered to this Signal.
	 * 
	 * @return the number of listenerMap currently registered to this Signal
	 */
	@Override
	public int numListeners() {
		return listenerMap.size();
	}
}
