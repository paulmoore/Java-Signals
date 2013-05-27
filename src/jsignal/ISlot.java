// http://paulmoore.mit-license.org/

package jsignal;

import java.lang.reflect.Method;

interface ISlot {

	public Object getListener();
	
	public Method getDelegate();
	
	public boolean getAddOnce();
}
