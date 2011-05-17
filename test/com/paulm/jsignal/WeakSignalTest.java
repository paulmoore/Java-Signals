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

import com.paulm.jsignal.test.SignalListener;

public class WeakSignalTest
{
	@Test
	public void test_strong_reference_retains_listener ()
	{
		WeakSignal signal = new WeakSignal();
		SignalListener mockListener = mock(SignalListener.class);
		try
		{
			signal.add(mockListener, "callback");
//			forceGC(new WeakReference<Object>(new Object()));
			assertTrue(signal.containsListener(mockListener));
			assertEquals(1, signal.numListeners());
			signal.dispatch();
		}
		catch (SignalException e)
		{
			fail(e.toString());
		}
		verify(mockListener, times(1)).callback();
		assertTrue(signal.containsListener(mockListener));
		assertEquals(1, signal.numListeners());
	}
	
	/**
	 * Unfortunately I can't properly test this functionality due to the nature of the Garbage collector.
	 * If I find a worth-while solution I will fix the test.
	 */
	@Test
	public void test_weak_reference_removes_listener ()
	{
		WeakSignal signal = new WeakSignal();
		SignalListener listener = new SignalListener();
		try
		{
			signal.add(listener, "callback");
			WeakReference<Object> ref = new WeakReference<Object>(listener);
			listener = null;
//			if (!forceGC(ref))
//			{
//				fail("Test inconclusive - Garbage Collection could not be forced.");
//			}
			signal.dispatch();
		}
		catch (SignalException e)
		{
			fail(e.toString());
		}
//		assertEquals(0, signal.numListeners());
	}
	
	/**
	 * Dosn't work.
	 * Try as I might, I can't force the Garbage Collector to run when I need it to.
	 * 
	 * @see <a href="http://hg.netbeans.org/main-silver/annotate/63b0eb0ebe1a/nbjunit/src/org/netbeans/junit/NbTestCase.java">NetBeans GC assertion</a>
	 */
	private boolean forceGC (Reference<?> ref)
	{
		List<byte[]> bytes = new ArrayList<byte[]>();
		int numBytes = 100000;
		for (int i = 0; i < 50; i++)
		{
			if (ref.get() == null)
			{
				return true;
			}
			try
			{
				System.gc();
			}
			catch (OutOfMemoryError ignored) {};
			try
			{
				System.runFinalization();
			}
			catch (OutOfMemoryError ignored) {};
			try
			{
				bytes.add(new byte[numBytes]);
				numBytes = (int) ((double) numBytes * 1.3);
			}
			catch (OutOfMemoryError e)
			{
				numBytes /= 2;
			}
			if (i % 3 == 0)
			{
				try
				{
					Thread.sleep(321);
				}
				catch (InterruptedException ignored) {};
			}
		}
		return false;
	}
}
