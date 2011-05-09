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

/**
 * Defines an interface for signals from which listeners can
 * be added or removed.
 * 
 * @author Paul Moore
 */
public interface ISignal
{
	/**
	 * Registers a listener to this signal.  If there is already a listener
	 * who's <code>Object.hashCode()</code> value is equal that to the listener
	 * argument, that listener is removed and returned.
	 * 
	 * @param listener the listener object
	 * @param callback the callback method
	 * @param addOnce if true, once this signal has dispatched the listener will be unregistered to this signal
	 * @return the previous listener with the same <code>Object.hasCode()</code> value as the listener argument, or null if no such listener exists
	 */
	public Object add (Object listener, String callback, boolean addOnce) throws SignalException;
	
	/**
	 * Unregisters a listener from this signal.
	 * 
	 * @param listener the listener to remove
	 * @return if the listener was successfully removed
	 */
	public boolean remove (Object listener);
	
	/**
	 * Returns whether or not a listener is registered to this signal.
	 * 
	 * @param listener the listener to check for
	 * @return if the listener is registered to this signal
	 */
	public boolean containsListener (Object listener);
	
	/**
	 * @return the number of listeners currently registered to this signal
	 */
	public int numListeners ();
}
