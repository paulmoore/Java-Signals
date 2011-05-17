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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The WeakSignal class is functionally the same as the Signal class, however,
 * it maintains weak references to its listeners that are automatically cleaned up.
 * It is useful for memory sensitive systems where Signal's can't be responsible
 * for removing their listeners.
 * 
 * This class logs to the </code>"com.paulm.jsignal"</code> Logger potential problems.
 * 
 * This is a port of Robert Penner's Signals for ActionScript 3.0
 * 
 * @author Paul Moore
 * @see com.paulm.jsignal
 */
final class WeakSignal extends Signal
{
	@Override
	public Object add (Object listener, String callback, boolean addOnce) throws SignalException
	{
		Method delegate;
		
		try
		{
			delegate = listener.getClass().getMethod(callback, params);
		}
		catch (SecurityException e)
		{
			SignalException se = new SignalException (e+" listener:"+listener+" callback:"+callback);
			log.throwing("WeakSignal", "add", se);
			throw se;
		}
		catch (NoSuchMethodException e)
		{
			SignalException se = new SignalException (e+" listener:"+listener+" callback:"+callback);
			log.throwing("WeakSignal", "add", se);
			throw se;
		}
		
		ISlot previous = listenerMap.put(listener, new WeakSlot(listener, delegate, addOnce));
		
		return previous == null ? null : previous.getListener();
	}
	
	@Override
	public void dispatch (Object... args) throws SignalException
	{
		Iterator<ISlot> iterator = listenerMap.values().iterator();
		ISlot slot;
		
		while (iterator.hasNext())
		{
			slot = iterator.next();
			Object listener = null;
			
			try
			{
				listener = slot.getListener();
				if (listener != null)
				{
					slot.getDelegate().invoke(listener, args);
				}
				else
				{
					iterator.remove();
				}
			}
			catch (IllegalArgumentException e)
			{
				SignalException se = new SignalException(e+" listener:"+slot.getDelegate()+" args:"+Arrays.deepToString(args));
				log.throwing("WeakSignal", "dispatch", se);
				throw se;
			}
			catch (IllegalAccessException e)
			{
				SignalException se = new SignalException(e+" listener:"+slot.getDelegate()+" args:"+Arrays.deepToString(args));
				log.throwing("WeakSignal", "dispatch", se);
				throw se;
			}
			catch (InvocationTargetException e)
			{
				SignalException se = new SignalException(e+" listener:"+slot.getDelegate()+" args:"+Arrays.deepToString(args));
				log.throwing("WeakSignal", "dispatch", se);
				throw se;
			}
			
			if (slot.getAddOnce() && listener != null)
			{
				iterator.remove();
			}
		}
	}
}
