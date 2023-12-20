package challenges.day20;

import challenges.day20.gate.Gate;

/**
 * Container for a single pulse signal from gate a to b
 * 
 * @author Joris
 */
public class Pulse {
	/** The gate that produced the signal */
	final public Gate source;
	
	/** The gate that should consume the signal */
	final public Gate destination;
	
	/** The signal value */
	final public boolean signal;

	/**
	 * Creates a new pulse, which is a message from a to b with either a low
	 * (false) or high (true) signal value.
	 * 
	 * @param from The sender
	 * @param to The receiver
	 * @param signal TThe pulse value
	 */
	public Pulse( final Gate from, final Gate to, final boolean signal ) {
		this.source = from;
		this.destination = to;
		this.signal = signal;			
	}
	
	/** @return The string description of the pulse */
	@Override
	public String toString( ) {
		return "[" + source + " --(" + (signal ? "high" : "low") + ")--> " + destination + "]";
	}
}