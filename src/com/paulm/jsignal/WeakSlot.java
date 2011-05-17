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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

final class WeakSlot implements ISlot
{
	private WeakReference<Object> listenerReference;
	private Method delegate;
	private boolean addOnce;
	
	public WeakSlot (Object listener, Method delegate, boolean addOnce)
	{
		listenerReference = new WeakReference<Object>(listener);
		this.delegate = delegate;
		this.addOnce = addOnce;
	}
	
	@Override
	public boolean getAddOnce()
	{
		return addOnce;
	}

	@Override
	public Method getDelegate()
	{
		return delegate;
	}

	@Override
	public Object getListener()
	{
		return listenerReference.get();
	}
	
	@Override
	public int hashCode ()
	{
		Object listener = listenerReference.get();
		if (listener == null)
		{
			return 0; // equivalent to System.identifyHashCode(null)
		}
		return listener.hashCode();
	}
	
	@Override
	public boolean equals (Object obj)
	{
		if (obj instanceof ISlot)
		{
			return obj == this;
		}
		
		return obj.equals(listenerReference.get());
	}
}
