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

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import junit.framework.TestCase;

import org.junit.Test;

public class SignalTest extends TestCase
{
	private int numCalls;
	
	public SignalTest ()
	{
		Logger.getLogger("com.paulm.jsignal").addHandler(new StreamHandler(System.out, new SimpleFormatter()));
	}

	@Test
	public void test_construction_of_signal_dispatch_no_listeners ()
	{
		try
		{
			Signal signal0 = new Signal();
			Signal signal1 = new Signal(int.class, Object.class, new Object().getClass());
			
			signal0.dispatch();
			signal1.dispatch(0, new Object(), new Object());
		}
		catch (Exception e)
		{
			fail(e.toString());
		}
	}
	
	@Test
	public void test_containsListener_works_after_addition ()
	{
		Signal signal = new Signal();
		assertFalse(signal.containsListener(this));
		signal.add(this, "testCallback0");
		assertTrue(signal.containsListener(this));
	}

	@Test
	public void test_no_param_signal_dispatches ()
	{
		Signal signal = new Signal();
		numCalls = 0;
		signal.add(this, "testCallback0");
		assertTrue(signal.containsListener(this));
		assertEquals(0, numCalls);
		signal.dispatch();
		assertEquals(1, numCalls);
	}
	
	@Test
	public void test_one_param_signal_dispatches ()
	{
		Signal signal = new Signal(int.class);
		signal.add(this, "testCallback1");
		signal.dispatch(1);
	}
	
	@Test
	public void test_multi_param_signal_dispatches ()
	{
		Signal signal = new Signal(int.class, float.class, this.getClass());
		signal.add(this, "testCallback2");
		signal.dispatch(1, 0.1, this);
	}
	
	@Test
	public void test_not_addOnce_registers_multiple_times ()
	{
		Signal signal = new Signal();
		numCalls = 0;
		signal.add(this, "testCallback0", false);
		assertEquals(0, numCalls);
		signal.dispatch();
		assertEquals(1, numCalls);
		signal.dispatch();
		assertEquals(2, numCalls);
	}
	
	@Test
	public void test_addOnce_only_registers_once ()
	{
		Signal signal = new Signal();
		numCalls = 0;
		signal.add(this, "testCallback0", true);
		assertEquals(0, numCalls);
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		assertEquals(1, numCalls);
		assertEquals(0, signal.numListeners());
		signal.dispatch();
		assertEquals(1, numCalls);
		assertFalse(signal.containsListener(this));
	}
	
	@Test
	public void test_removed_listener_dosnt_fire ()
	{
		Signal signal = new Signal();
		numCalls = 0;
		signal.add(this, "testCallback0");
		assertEquals(0, numCalls);
		assertEquals(1, signal.numListeners());
		signal.dispatch();
		assertEquals(1, numCalls);
		signal.remove(this);
		signal.dispatch();
		assertEquals(1, numCalls);
		assertFalse(signal.containsListener(this));
	}
	
	@Test
	public void test_listener_added_twice_dosnt_duplicate ()
	{
		Signal signal = new Signal();
		numCalls = 0;
		signal.add(this, "testCallback0");
		assertEquals(1, signal.numListeners());
		assertEquals(this, signal.add(this, "testCallback0"));
		assertEquals(1, signal.numListeners());
		assertEquals(0, numCalls);
		signal.dispatch();
		assertEquals(1, numCalls);
	}
	
	@Test
	public void test_adding_null_dosnt_add_listener ()
	{
		Signal signal = new Signal();
		try
		{
			signal.add(null, "testCallback0");
			assertEquals(0, signal.numListeners());
			signal.dispatch();
		}
		catch (Exception e)
		{
			fail(e.toString());
		}
	}
	
	public void testCallback0 ()
	{
		numCalls++;
	}
	
	public void testCallback1 (int arg)
	{
		assertEquals(1, arg);
	}
	
	public void testCallBack2 (int arg0, float arg1, Object arg3)
	{
		assertEquals(1, arg0);
		assertEquals(0.1, arg1);
		assertEquals(this, arg3);
	}
}
