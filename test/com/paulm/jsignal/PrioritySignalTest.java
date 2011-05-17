/* 
 * Copyright (c) 2011 Paul Moore
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.paulm.jsignal;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.InOrder;

import com.paulm.jsignal.test.SignalListener;

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
			fail(e.toString());
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
			fail(e.toString());
		}
		priority.verify(mockListenerFirst, times(1)).callback();
		priority.verify(mockListenerThird, times(1)).callback();
		verify(mockListenerSecond, times(1)).callback();
	}
}
