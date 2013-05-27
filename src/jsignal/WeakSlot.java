// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

final class WeakSlot implements ISlot {
	private WeakReference<Object> listenerReference;
	private Method delegate;
	private boolean addOnce;
	
	public WeakSlot(Object listener, Method delegate, boolean addOnce) {
		listenerReference = new WeakReference<Object>(listener);
		this.delegate = delegate;
		this.addOnce = addOnce;
	}
	
	@Override
	public boolean getAddOnce() {
		return addOnce;
	}

	@Override
	public Method getDelegate() {
		return delegate;
	}

	@Override
	public Object getListener() {
		return listenerReference.get();
	}
	
	@Override
	public int hashCode() {
		Object listener = listenerReference.get();
		if (listener == null) {
			return 0; // equivalent to System.identifyHashCode(null)
		}
		return listener.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISlot) {
			return obj == this;
		}
		return obj.equals(listenerReference.get());
	}
}
