// http://paulmoore.mit-license.org/

package jsignal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jsignal.test.SignalListener;

public class WeakSignalTest {

	@Test
	public void test_strong_reference_retains_listener() {
		WeakSignal signal = new WeakSignal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		// forceGC(new WeakReference<Object>(new Object()));
		assertTrue(signal.containsListener(mockListener));
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		verify(mockListener, times(1)).callback();
		assertTrue(signal.containsListener(mockListener));
		assertEquals(1, signal.numListeners());
	}
	
	/**
	 * Unfortunately I can't properly test this functionality due to the nature of the Garbage collector.
	 * If I find a worth-while solution I will fix the test.
	 */
	@Test
	public void test_weak_reference_removes_listener() {
		WeakSignal signal = new WeakSignal();
		SignalListener listener = new SignalListener();
		signal.add(listener, "callback");
		WeakReference<Object> ref = new WeakReference<Object>(listener);
		listener = null;
		// if (!forceGC(ref)) {
		// 	fail("Test inconclusive - Garbage Collection could not be forced.");
		// }
		signal.dispatch();
		// assertEquals(0, signal.numListeners());
	}
	
	/**
	 * Dosn't work.
	 * Try as I might, I can't force the Garbage Collector to run when I need it to.
	 * 
	 * @see <a href="http://hg.netbeans.org/main-silver/annotate/63b0eb0ebe1a/nbjunit/src/org/netbeans/junit/NbTestCase.java">NetBeans GC assertion</a>
	 */
	private boolean forceGC(Reference<?> ref) {
		List<byte[]> bytes = new ArrayList<byte[]>();
		int numBytes = 100000;
		for (int i = 0; i < 50; i++) {
			if (ref.get() == null) {
				return true;
			}
			try {
				System.gc();
			} catch (OutOfMemoryError ignored) {
			}
			try {
				System.runFinalization();
			} catch (OutOfMemoryError ignored) {};
			try {
				bytes.add(new byte[numBytes]);
				numBytes = (int) ((double) numBytes * 1.3);
			} catch (OutOfMemoryError e) {
				numBytes /= 2;
			}
			if (i % 3 == 0) {
				try {
					Thread.sleep(321);
				} catch (InterruptedException ignored) {
				}
			}
		}
		return false;
	}
}
