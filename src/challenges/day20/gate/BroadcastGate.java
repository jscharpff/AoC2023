package challenges.day20.gate;

import challenges.day20.Pulse;

/**
 * Specific type of gate that will pass on any input signal to all of the gates
 * connected at the output side 
 *  
 * @author Joris
 */
public class BroadcastGate extends Gate {
	public BroadcastGate( ) {
		super( "broadcaster" );
	}
	
	/**
	 * Simply passes on the input pulse to all outputs
	 * 
	 * @param pulse The input signal
	 * @return The signal value of the input pulse
	 */
	@Override
	public Boolean process( final Pulse pulse ) {
		// create a broadcast signal to all connected gates
		return pulse.signal;
	}
	
	/** @return Broadcast gates have no particular state */ 
	@Override
	public boolean getState( ) {
		return false;
	}
}
