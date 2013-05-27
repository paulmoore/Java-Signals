h1. A new approach to events in Java.

*Java Signals* is a typed, compact API that allows events to be implemented quickly and efficiently.

 * A ??Signal?? (not to be confused with the JVM's native signal handling) is essentially a self contained event dispatching mechanism.
 * Each Signal corresponds to a unique event, which contains its own list of listeners and event data types.
 * Only a Signal instance is needed for each event, no extra constants or subclasses are needed.
 * Event listeners register directly to the Signal, without need of an event manager and string based representation of the event type.
 * Interfaces allow for easy encapsulation of Signal functionality where needed.
 * The Java Signal's API is an event model that can be used outside of Applet development (AWT/Swing).

h2. Examples

h3. Basic Signal Example

<pre><code>// using the Java Signals event model in a simple banking example
import com.paulm.jsignal.Signal;
import com.paulm.jsignal.SignalException;

public class BankApp
{
	public static void main (String args[])
	{
		BankApp app = new BankApp();
		ATM atm = new ATM();
		try
		{
			// add the app as a listener to the ATM transactionComplete signal
			atm.transactionComplete.add(app, "handleNewBalance");
		}
		catch (SignalException e)
		{
			e.printStackTrace();
		}
		atm.doTransaction();
	}

	public void handleNewBalance (String name, double balance)
	{
		System.out.println("New balance recieved, name:"+name+" balance:"+balance);
	}
}

class ATM
{
	protected final Signal transactionComplete;
	
	public ATM ()
	{
		// any number of parameter class types can be passed to the constructor
		transactionComplete = new Signal(String.class, double.class);
	}
	
	public void doTransaction ()
	{
		// do something
		try
		{
			// dispatch the event; to enforce strict-typing of event data, the data types of the arguments must match up with formal parameters
			transactionComplete.dispatch("Paul", 17.06);
		}
		catch (SignalException e)
		{
			e.printStackTrace();
		}
	}
}</code></pre>

h3. PrioritySignal Example

<pre><code>// using the Java Signals event model for priority based event dispatching

//...
// we can specify generically a comparable type to use as priority, in this case we will use Integers
PrioritySignal<Integer> transactionComplete = new PrioritySignal<Integer>(String.class, double.class);
//...

// add the high and low priority handlers
// note that Integers are compared by their natural ordering, so a lower number has a higher priority in this case
atm.transactionComplete.add(lowPriorityApp, "handleNewBalance", 1);
atm.transactionComplete.add(highPriorityApp, "handleNewBalance", 0);</code></pre>

h3. WeakSignal Example

<pre><code>// using WeakSignals for memory sensitive code

//...
WeakSignal signal = new WeakSignal();
Listener listener = new Listener();
signal.add(listener, "callback");
listener = null;
//... after garbage collection

signal.dispatch(); // listener was garbaged collected and automatically removed as a listener from the WeakSignal instance</code></pre>

*Note:* Because native AWT events haven't yet been wrapped by Java Signals, there is no need to post a side by side comparison of the two methods.  You can find Oracle's tutorial on events "here":http://download.oracle.com/javase/tutorial/uiswing/events/index.html

h2. Links

 * "Robert Penner's original Signals API for AS3":https://github.com/robertpenner/as3-signals
 * Feel free to contact me at <b>paulahmoore@shaw.ca</b> with any questions/comments/concerns/critiques/etc