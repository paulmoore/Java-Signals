// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.reflect.Method;

class Slot implements ISlot {
	private final Object listener;
	private final Method delegate;
	private final boolean addOnce;
	
	public Slot(Object listener, Method delegate, boolean addOnce) {
		this.listener = listener;
		this.delegate = delegate;
		this.addOnce = addOnce;
	}
	
	@Override
	public Object getListener() {
		return listener;
	}
	
	@Override
	public Method getDelegate() {
		return delegate;
	}
	
	@Override
	public boolean getAddOnce() {
		return addOnce;
	}
	
	@Override
	public int hashCode() {
		return listener.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISlot) {
			return obj == this;
		}
		return listener.equals(obj);
	}
}
