package com.paulm.jsignal;

import org.junit.Test;
import org.mockito.InOrder;

import com.paulm.jsignal.test.SignalListener;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

public class PrioritySignalTest extends TestCase
{
	@Test
	public void test_one_listener_dispatch ()
	{
		Signal signal = new PrioritySignal<Integer>();
		SignalListener mockListener = mock(SignalListener.class);
		try
		{
			signal.add(mockListener, "callback");
			signal.dispatch();
		}
		catch (SignalException e)
		{
			e.printStackTrace();
		}
		verify(mockListener, times(1)).callback();
	}
	
	@Test
	public void test_two_listener_dispatch ()
	{
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener mockListenerFirst = mock(SignalListener.class);
		SignalListener mockListenerSecond = mock(SignalListener.class);
		InOrder priority = inOrder(mockListenerFirst, mockListenerSecond);
		try
		{
			signal.add(mockListenerSecond, "callback", 1);
			signal.add(mockListenerFirst, "callback", 0);
			signal.dispatch();
		}
		catch (SignalException e)
		{
			fail(e.getLocalizedMessage());
		}
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerSecond, times(1)).callback();
		assertEquals(2, signal.numListeners());
	}
	
	@Test
	public void test_addOnce_removes_from_priority_queue ()
	{
		PrioritySignal<Integer> signal = new PrioritySignal<Integer>();
		SignalListener mockListenerFirst = mock(SignalListener.class);
		SignalListener mockListenerSecond = mock(SignalListener.class);
		SignalListener mockListenerThird = mock(SignalListener.class);
		InOrder priority = inOrder(mockListenerFirst, mockListenerSecond, mockListenerThird);
		try
		{
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
		}
		catch (SignalException e)
		{
			fail(e.getLocalizedMessage());
		}
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerThird, times(1)).callback();
		verify(mockListenerSecond, times(1)).callback();
	}
}
