// http://paulmoore.mit-license.org/

package jsignal;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.InOrder;

import jsignal.test.SignalListener;

public class PrioritySignalTest extends TestCase {

	@Test
	public void test_one_listener_dispatch() {
		Signal signal = new PrioritySignal<Integer>();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		verify(mockListener, times(1)).callback();
	}
	
	@Test
	public void test_two_listener_dispatch() {
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener mockListenerFirst = mock(SignalListener.class);
		SignalListener mockListenerSecond = mock(SignalListener.class);
		InOrder priority = inOrder(mockListenerFirst, mockListenerSecond);
		signal.add(mockListenerSecond, "callback", 1);
		signal.add(mockListenerFirst, "callback", 0);
		signal.dispatch();
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerSecond, times(1)).callback();
		assertEquals(2, signal.numListeners());
	}
	
	@Test
	public void test_addOnce_removes_from_priority_queue() {
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener mockListenerFirst = mock(SignalListener.class);
		SignalListener mockListenerSecond = mock(SignalListener.class);
		SignalListener mockListenerThird = mock(SignalListener.class);
		InOrder priority = inOrder(mockListenerFirst, mockListenerSecond, mockListenerThird);
		signal.add(mockListenerThird, "callback", 2);
		signal.add(mockListenerSecond, "callback", true, 1);
		signal.add(mockListenerFirst, "callback", 0);
		assertEquals(3, signal.numListeners());
		signal.dispatch();
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerSecond, times(1)).callback();
		priority.verify(mockListenerThird, times(1)).callback();
		assertEquals(2, signal.numListeners());
		assertFalse(signal.containsListener(mockListenerSecond));
		priority = inOrder(mockListenerFirst, mockListenerThird);
		signal.dispatch();
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerThird, times(1)).callback();
		verify(mockListenerSecond, times(1)).callback();
	}
	
	@Test
	public void test_listener_can_be_removed_before_dispatch() {
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener listener = mock(SignalListener.class);
		signal.add(listener, "callback", 0);
		assertEquals(1, signal.numListeners());
		signal.remove(listener);
		assertEquals(0, signal.numListeners());
		verify(listener, never()).callback();
	}
	
	@Test
	public void test_listener_can_be_removed_after_dispatch() {
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener listener = mock(SignalListener.class);
		signal.add(listener, "callback", 0);
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		verify(listener, times(1)).callback();
		signal.remove(listener);
		assertEquals(0, signal.numListeners());
		signal.dispatch();
		verify(listener, times(1)).callback();
	}
}
