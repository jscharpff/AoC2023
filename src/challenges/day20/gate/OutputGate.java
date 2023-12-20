package challenges.day20.gate;

import challenges.day20.Pulse;

/**
 * Simple, dummy gate that represents a terminal output. It holds the last
 * received input as its state
 * 
 * @author Joris
 */
public class OutputGate extends Gate {
	/** The last received signal */
	protected boolean state;
	
	/**
	 * Creates a new OutputGate
	 * 
	 * @param label The label of the gate
	 */
	public OutputGate( final String label ) {
		super( label );
		state = false;
	}
	
	/**
	 * Processes the given pulse by doing nothing...
	 * 
	 * @param pulse The input signal fed to the gate
	 * @return null to not produce any output signal
	 */
	@Override
	public  Boolean process( final Pulse pulse ) {
		state = pulse.signal;
		return null;
	}
	
	/** @return The last received signal */
	@Override
	public boolean getState( ) {
		return state;
	}
}
