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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.logging.Level;

/**
 * The PrioritySignal class is an extension of Signal that dispatches to its
 * listeners in the order of their priority.
 * 
 * This class logs to the </code>"com.paulm.jsignal"</code> Logger potential problems.
 * 
 * This is a port of Robert Penner's Signals for ActionScript 3.0
 * 
 * @author Paul Moore
 * @see com.paulm.jsignal.Signal
 */
public class PrioritySignal extends Signal
{
	private PriorityQueue<PrioritySlot> listenerQueue;
	
	/**
	 * Constructor
	 * 
	 * @param params the parameter types (as a Class instance) that this signal will dispatch as event data
	 */
	public PrioritySignal (Class<?>... params)
	{
		this(11, params);
	}
	
	/**
	 * Constructor
	 * 
	 * @param initialCapacity the initial capacity of the underlying priority queue
	 * @param params the parameter types (as a Class instance) that this signal will dispatch as event data
	 */
	public PrioritySignal (int initialCapacity, Class<?>... params)
	{
		super(params);
		
		listenerQueue = new PriorityQueue<PrioritySlot>(initialCapacity);
	}
	
	/**
	 * Registers a listener to this signal with a priority.  A higher value
	 * indicates a higher priority.  All integer values are excepted
	 * (including negatives).  More formally, listeners are sorted by the
	 * natural ordering of their integer priorities.
	 * 
	 * @param listener the listener to register to this signal
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched
	 * @param addOnce if true, this listener will be unregistered to this signal the next time it is dispatched
	 * @param priority the priority of this listener
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 */
	public Object add (Object listener, String callback, boolean addOnce, int priority)
	{	
		Method delegate;
		
		try
		{
			delegate = listener.getClass().getMethod(callback, params);
		}
		catch (Exception e)
		{
			logger.throwing("PrioritySignal", "add", e);
			return null;
		}
		
		PrioritySlot newSlot = new PrioritySlot(listener, delegate, addOnce, priority);
		
		Slot previous = listenerMap.put(listener, newSlot);
		
		if (previous != null)
		{
			if (!listenerQueue.remove(previous))
			{
				logger.logp(Level.WARNING, "PrioritySignal", "add", "Previous listener was found in listener map: "+listener+" but couldn't be found in listener queue!");
			}
		}
		
		listenerQueue.add(newSlot);
		
		return previous;
	}
	
	/* (non-Javadoc)
	 * @see com.paulm.jsignal.Signal#add(java.lang.Object, java.lang.String, boolean)
	 */
	@Override
	public Object add (Object listener, String callback, boolean addOnce)
	{
		return add(listener, callback, addOnce, 0);
	}
	
	/**
	 * Registers a listener to this signal with a given priority.
	 * <code>addOnce</code> defaults to false.
	 * 
	 * @see com.paulm.jsignal.PrioritySignal#add(Object, String, boolean, int)
	 * 
	 * @param listener the listener to register to this signal
	 * @param callback the callback method, as a String, to invoke when this signal is dispatched dispatched
	 * @param priority the priority of this listener
	 * @return the old listener keyed to the same <code>hashCode()</code> value, or null if no such listener was replaced
	 */
	public Object add (Object listener, String callback, int priority)
	{
		return add(listener, callback, false, priority);
	}
	
	/* (non-Javadoc)
	 * @see com.paulm.jsignal.Signal#add(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object add (Object listener, String callback)
	{
		return add(listener, callback, false, 0);
	}

	/* (non-Javadoc)
	 * @see com.paulm.jsignal.Signal#remove(java.lang.Object)
	 */
	@Override
	public boolean remove (Object listener)
	{
		if (super.remove(listener))
		{
			if (!listenerQueue.remove(listener))
			{
				logger.logp(Level.WARNING, "PrioritySignal", "remove", "Listener was removed from the listener map:"+listener+" but could not be found in the listener queue!");
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.paulm.jsignal.Signal#removeAll()
	 */
	@Override
	public void removeAll ()
	{
		super.removeAll();
		
		listenerQueue.clear();
	}

	/* (non-Javadoc)
	 * @see com.paulm.jsignal.Signal#dispatch(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dispatch (Object... args)
	{
		PrioritySlot slot;
		
		while (!listenerQueue.isEmpty())
		{
			slot = listenerQueue.remove();
			
			try
			{
				slot.getDelegate().invoke(slot.getListener(), args);
			}
			catch (Exception e)
			{
				logger.throwing("PrioritySignal", "dispatch", e);
				return;
			}
			
			if (slot.getAddOnce())
			{
				super.remove(slot);
			}
		}
		
		listenerQueue.addAll((Collection<? extends PrioritySlot>) listenerMap);
	}
}
