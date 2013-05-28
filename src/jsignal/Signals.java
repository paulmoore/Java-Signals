// http://paulmoore.mit-license.org/

package jsignal;

/**
 * Utility class which contains static methods to operate on signal instances.
 * Notably, it can create wrappers to existing signals to add functionality.
 */
public class Signals {

	private Signals() {
	}

	/**
	 * Creates a synchronized signal from the given signal.
	 * The resultant signal synchronizes on itself.
	 *
	 * @param signal The unsynchronized signal to make thread safe
	 * @return A synchronized signal that wraps the original signal
	 */
	public static ISignal synchronizedSignal(ISignal signal) {
		return new SynchronizedSignal(signal);
	}

	/**
	 * @see jsignal.Signals#synchronizedSignal(jsignal.ISignal)
	 */
	public static ISignalOwner synchronizedSignal(ISignalOwner signal) {
		return new SynchronizedSignal(signal);
	}
}

class SynchronizedSignal implements ISignalOwner {
	private final ISignal signal;

	public SynchronizedSignal(ISignal signal) {
		this.signal = signal;
	}

	public synchronized Object add(Object listener, String callback, boolean addOnce) {
		return signal.add(listener, callback, addOnce);
	}
	
	public synchronized boolean remove(Object listener) {
		return signal.remove(listener);
	}

	public synchronized boolean containsListener(Object listener) {
		return signal.containsListener(listener);
	}

	public synchronized int numListeners() {
		return signal.numListeners();
	}

	public synchronized void removeAll() {
		((ISignalOwner)signal).removeAll();
	}

	public synchronized void dispatch(Object... args) {
		((IDispatcher)signal).dispatch(args);
	}
}
