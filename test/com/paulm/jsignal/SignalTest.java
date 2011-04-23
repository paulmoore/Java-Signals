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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;

import com.paulm.jsignal.test.OutputStreamHandler;
import com.paulm.jsignal.test.SignalListener;

public class SignalTest extends TestCase
{
	static
	{
		Logger.getLogger(Signal.class.getPackage().getName()).addHandler(new OutputStreamHandler());
	}
	
	@Test
	public void test_containsListener_works_after_addition ()
	{
		Signal spySignal = spy(new Signal());
		SignalListener listener = mock(SignalListener.class);
		assertNull(spySignal.add(listener, "callback"));
		verify(spySignal).add(listener, "callback");
		assertTrue(spySignal.containsListener(listener));
		assertEquals(1, spySignal.numListeners());
	}
	
	@Test
	public void test_listener_with_wrong_params_is_not_added ()
	{
		Signal signal = new Signal(String.class);
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		verify(mockListener, never()).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_dispatch_no_listeners ()
	{
		Signal mockSignal = mock(Signal.class);
		mockSignal.dispatch();
		verify(mockSignal, times(1)).dispatch();
		verify(mockSignal, never()).add(anyObject(), anyString());
	}

	@Test
	public void test_no_param_dispatch ()
	{
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		verify(mockListener, times(1)).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_no_param_dispatch_multiple_times ()
	{
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch();
		signal.dispatch();
		signal.dispatch();
		verify(mockListener, times(3)).callback();
	}
	
	@Test
	public void test_one_param_dispatch ()
	{
		Signal signal = new Signal(int.class);
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch(1);
		verify(mockListener).callback(1);
		verify(mockListener, times(1)).callback(anyInt());
	}
	
	@Test
	public void test_multi_param_dispatch ()
	{
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
	public void test_wrong_args_does_not_dispatch ()
	{
		Signal signal = new Signal(int.class, Object.class, String.class);
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.dispatch(1);
		verify(mockListener, never()).callback();
		verify(mockListener, never()).callback(anyInt());
		verify(mockListener, never()).callback(anyInt(), anyObject(), anyString());
	}
	
	@Test
	public void test_addOnce_only_fires_once ()
	{
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback", true);
		signal.dispatch();
		signal.dispatch();
		verify(mockListener, times(1)).callback();
		assertEquals(0, signal.numListeners());
	}
	
	@Test
	public void test_removed_listener_dosnt_fire ()
	{
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.remove(mockListener);
		signal.dispatch();
		verify(mockListener, never()).callback();
		assertEquals(0, signal.numListeners());
	}
	
	@Test
	public void test_listener_added_twice_dosnt_duplicate ()
	{
		Signal signal = new Signal();
		SignalListener mockListener = mock(SignalListener.class);
		signal.add(mockListener, "callback");
		signal.add(mockListener, "callback");
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		verify(mockListener, times(1)).callback();
	}
	
	@Test
	public void test_adding_null_dosnt_add_listener ()
	{
		Signal signal = new Signal();
		signal.add(null, "callback");
		assertEquals(0, signal.numListeners());
	}
}
