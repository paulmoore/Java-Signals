// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * The PrioritySignal class is an extension of Signal that dispatches to its
 * listeners in the order of their priority.
 * 
 * This class logs to the </code>"com.paulm.jsignal"</code> Logger potential problems.
 * 
 * This is a port of Robert Penner's Signals for ActionScript 3.0
 * 
 * @see jsignal.Signal
 */
public final class PrioritySignal <E extends Comparable<E>> extends Signal {
	private PriorityQueue<ISlot> listenerQueue;
	
	/**
	 * Constructor
	 * 
	 * @param params the parameter types (as a Class instance) that this signal will dispatch as event data
	 */
	public PrioritySignal(Class<?>... params) {
		this(11, params);
	}
	
	/**
	 * Constructor
	 * 
	 * @param initialCapacity the initial capacity of the underlying priority queue
	 * @param params the parameter types (as a Class instance) that this signal will dispatch as event data
	 */
	public PrioritySignal(int initialCapacity, Class<?>... params) {
		super(params);
		listenerQueue = new PriorityQueue<ISlot>(initialCapacity);
	}
	
	/**
	 * Registers a listener to this signal with a priority.  A higher value
	 * indicates a higher priority.  All integer values are excepted
	 * (including negatives).  More formally, listeners are sorted by the
	 * natural ordering of their integer priorities.
	 * 
	 * @param listener the listener to register to this signal
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched
	 * @param addOnce if true, this listener will be unregistered to this signal the next time it is dispatched
	 * @param priority the priority of this listener
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 * @throws SignalException if a security violation occurs while retrieving the callback method, or if no such method exists
	 */
	public Object add(Object listener, String callback, boolean addOnce, E priority) {	
		Method delegate;
		try {
			delegate = listener.getClass().getMethod(callback, params);
		} catch (SecurityException e) {
			throw new SignalException("Could not access method `"+listener.getClass().getName()+"."+callback+"`", e);
		} catch (NoSuchMethodException e) {
			throw new SignalException("Could not find method `"+listener.getClass().getName()+"."+callback+"`", e);
		}
		ISlot newSlot = new PrioritySlot<E>(listener, delegate, addOnce, priority);
		ISlot previous = listenerMap.put(listener, newSlot);
		if (previous != null) {
			if (!listenerQueue.remove(previous)) {
				throw new SignalException("Invalid signal state while adding listener, previous listener was found in the map but not in the queue");
			}
		}
		listenerQueue.add(newSlot);
		return previous;
	}
	
	/**
	 * @see jsignal.Signal#add(java.lang.Object, java.lang.String, boolean)
	 */
	@Override
	public Object add(Object listener, String callback, boolean addOnce) {
		return add(listener, callback, addOnce, null);
	}
	
	/**
	 * Registers a listener to this signal with a given priority.
	 * <code>addOnce</code> defaults to false.
	 * 
	 * @param listener the listener to register to this signal
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched dispatched
	 * @param priority the priority of this listener
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 * @throws SignalException if a security violation occurs while retrieving the callback method, or if no such method exists
	 */
	public Object add(Object listener, String callback, E priority) {
		return add(listener, callback, false, priority);
	}
	
	/**
	 * @see jsignal.Signal#add(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object add (Object listener, String callback) {
		return add(listener, callback, false, null);
	}

	/**
	 * @see jsignal.Signal#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object listener) {
		if (super.remove(listener)) {
			Iterator<ISlot> it = listenerQueue.iterator();
			while (it.hasNext()) {
				if (it.next().equals(listener)) {
					it.remove();
					return true;
				}
			}
			throw new SignalException("Invalid signal state while removing listener, listener was found in the map but not in the queue");
		}
		return false;
	}
	
	/**
	 * @see jsignal.Signal#removeAll()
	 */
	@Override
	public void removeAll() {
		super.removeAll();
		listenerQueue.clear();
	}

	/**
	 * @see jsignal.Signal#dispatch(java.lang.Object[])
	 */
	@Override
	public void dispatch(Object... args) {
		PriorityQueue<ISlot> newQueue = new PriorityQueue<ISlot>(Math.max(11, listenerQueue.size()));
		while (!listenerQueue.isEmpty()) {
			ISlot slot = listenerQueue.remove();
			try {
				slot.getDelegate().invoke(slot.getListener(), args);
			}
			catch (IllegalArgumentException e) {
				throw new SignalException("Method "+slot.getDelegate()+" received an invalid argument "+Arrays.deepToString(args), e);
			} catch (IllegalAccessException e) {
				throw new SignalException("Could not access method "+slot.getDelegate(), e);
			} catch (InvocationTargetException e) {
				throw new SignalException("Could not invoke method "+slot.getDelegate(), e);
			}
			if (slot.getAddOnce()) {
				super.remove(slot);
			} else {
				newQueue.add(slot);
			}
		}
		listenerQueue = newQueue;
	}
}
