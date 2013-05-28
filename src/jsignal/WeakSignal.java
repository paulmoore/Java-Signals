// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The WeakSignal class is functionally the same as the Signal class, however,
 * it maintains weak references to its listeners that are automatically cleaned up.
 * It is useful for memory sensitive systems where Signal's can't be responsible
 * for removing their listeners.
 * 
 * This class logs to the </code>"com.paulm.jsignal"</code> Logger potential problems.
 * 
 * This is a port of Robert Penner's Signals for ActionScript 3.0
 * 
 * @see jsignal.Signal
 */
final class WeakSignal extends Signal {
	
	@Override
	public Object add(Object listener, String callback, boolean addOnce) {
		Method delegate;
		try {
			delegate = listener.getClass().getMethod(callback, params);
		} catch (SecurityException e) {
			throw new SignalException("Could not access method `"+listener.getClass().getName()+"."+callback+"`", e);
		} catch (NoSuchMethodException e) {
			throw new SignalException("Could not find method `"+listener.getClass().getName()+"."+callback+"`", e);
		}
		ISlot previous = listenerMap.put(listener, new WeakSlot(listener, delegate, addOnce));
		return previous == null ? null : previous.getListener();
	}
	
	@Override
	public void dispatch(Object... args) {
		Iterator<ISlot> iterator = listenerMap.values().iterator();
		while (iterator.hasNext()) {
			ISlot slot = iterator.next();
			Object listener;
			try {
				listener = slot.getListener();
				if (listener != null) {
					slot.getDelegate().invoke(listener, args);
				} else {
					iterator.remove();
				}
			} catch (IllegalArgumentException e) {
				throw new SignalException("Method "+slot.getDelegate()+" received an invalid argument "+Arrays.deepToString(args), e);
			} catch (IllegalAccessException e) {
				throw new SignalException("Could not access method "+slot.getDelegate(), e);
			} catch (InvocationTargetException e) {
				throw new SignalException("Could not invoke method "+slot.getDelegate(), e);
			}
			if (slot.getAddOnce() && listener != null) {
				iterator.remove();
			}
		}
	}
}
