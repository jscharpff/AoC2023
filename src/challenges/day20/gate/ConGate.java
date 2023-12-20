package challenges.day20.gate;

import java.util.HashMap;
import java.util.Map;

import challenges.day20.Pulse;

/**
 * Conjunction gate that produces a low signal only when all inputs are high,
 * otherwise a high value is produced.
 * 
 * @author Joris
 */
public class ConGate extends Gate {
	/** The last signal received for each of its outputs */
	private Map<Gate, Boolean> mem;
	
	/**
	 * Creates the gate
	 * 
	 * @param label The label of the gate
	 */
	public ConGate( final String label ) {
		super( label );
		mem = new HashMap<>( );
	}
	
	/**
	 * @return The current signal state of the gate
	 */
	public boolean getState( ) {
		// check if we have all high signals on the input side, else wait for them
		if( mem.size( ) != ins.size( ) ) return true;

		// all signals in, send the output based upon values in memory
		return mem.values( ).contains( false );
	}
	
	/**
	 * Produces the pulses to send when fed a new signal from another gate. For
	 * a conjunction gate this will be a low pulse if all inputs are high, and
	 * a high signal otherwise.
	 * 
	 * @param pulse The input signal
	 * @return The list of pulses to 
	 */
	@Override
	public Boolean process( final Pulse pulse ) {
		// store last signal received for the output and send the gate's state as
		// output to next gates
		mem.put( pulse.source, pulse.signal );
		return getState( );
	}
	
	/** @return The string describing this gate */
	@Override
	public String toString( ) {
		return "&" + super.toString( );
	}
}
