// http://paulmoore.mit-license.org/

package jsignal;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.junit.Test;

import com.paulm.jsignal.test.SignalListener;

public class SignalTest extends TestCase {

	@Test
	public void test_containsListener_works_after_addition() {
		Signal spySignal = spy(new Signal());
		SignalListener listener = mock(SignalListener.class);
		assertNull(spySignal.add(listener, "callback"));
		verify(spySignal).add(listener, "callback");
		assertTrue(spySignal.containsListener(listener));
		assertEquals(1, spySignal.numListeners());
	}
	
	@Test
	public void test_listener_with_wrong_params_is_not_added() {
		Signal signal = new Signal(String.class);
		SignalListener mockListener = mock(SignalListener.class);
		try {
			signal.add(mockListener, "callback");
			signal.dispatch();
			fail("Exception not thrown");
		} catch (SignalException expected) {
		}
		verify(mockListener, never()).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_dispatch_no_listeners() {
		Signal mockSignal = mock(Signal.class);
		mockSignal.dispatch();
		verify(mockSignal, times(1)).dispatch();
		verify(mockSignal, never()).add(anyObject(), anyString());
	}

	@Test
	public void test_no_param_dispatch() {
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		verify(mockListener, times(1)).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_no_param_dispatch_multiple_times() {
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		signal.dispatch();
		signal.dispatch();
		verify(mockListener, times(3)).callback();
	}
	
	@Test
	public void test_one_param_dispatch() {
		Signal signal = new Signal(int.class);
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch(1);
		verify(mockListener).callback(1);
		verify(mockListener, times(1)).callback(anyInt());
	}
	
	@Test
	public void test_multi_param_dispatch() {
		Signal signal = new Signal(int.class, Object.class, String.class);
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		int arg0 = 1;
		Object arg1 = new Object();
		String arg2 = "test";
		signal.dispatch(arg0, arg1, arg2);
		verify(mockListener, times(1)).callback(arg0, arg1, arg2);
		verify(mockListener, never()).callback();
		verify(mockListener, never()).callback(anyInt());
	}
	
	@Test
	public void test_wrong_args_does_not_dispatch() {
		Signal signal = new Signal(int.class, Object.class, String.class);
		SignalListener mockListener = mock(SignalListener.class);
		try {
			signal.add(mockListener, "callback");
			signal.dispatch(1);
			fail("Expected exception was not thrown");
		} catch (SignalException expected) {
		}
		verify(mockListener, never()).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_addOnce_only_fires_once() {
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback", true);
		signal.dispatch();
		signal.dispatch();
		verify(mockListener, times(1)).callback();
		assertEquals(0, signal.numListeners());
	}
	
	@Test
	public void test_removed_listener_doesnt_fire() {
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.remove(mockListener);
		signal.dispatch();
		verify(mockListener, never()).callback();
		assertEquals(0, signal.numListeners());
	}
	
	@Test
	public void test_listener_added_twice_doesnt_duplicate() {
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.add(mockListener, "callback");
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		verify(mockListener, times(1)).callback();
	}
	
	@Test
	public void test_adding_null_doesnt_add_listener() {
		Signal signal = new Signal();
		try {
			signal.add(null, "callback");
			fail("Expected exception was not thrown");
		} catch (NullPointerException expected) {
		}
		assertEquals(0, signal.numListeners());
	}
}
