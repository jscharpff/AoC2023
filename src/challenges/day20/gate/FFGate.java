package challenges.day20.gate;

import challenges.day20.Pulse;

/**
 * Flip flop gate that acts as a on/off toggle when fed a low signal and does
 * nothing when given a high signal.
 * 
 * @author Joris
 */
public class FFGate extends Gate {
	/** The state of the flip flop gate */
	private boolean state;
	
	/**
	 * Creates a new flip flop gate
	 * 
	 * @param label The gate label
	 */
	public FFGate( final String label ) {
		super( label );
		
		// start in low state
		state = false;
	}
	
	/** @return The current toggle state of the flip flop */
	public boolean getState( ) { return state; }
	
	/**
	 * Process the input signal. If the input was high, it is ignored and no
	 * output is produced. If input is low, the state of this gate is toggled and
	 * the new state value is sent as output.
	 * 
	 * @param pulse The input signal
	 * @return The state as output signal, if input = low. Null otherwise
	 */
	@Override
	public Boolean process( final Pulse pulse ) {
		// if the flip flop is high, ignore the signal and send no output
		if( pulse.signal ) return null;

		// otherwise toggle the state and send signal accordingly
		state = !state;
		return state;
	}
	
	/** @return The string describing this gate */
	@Override
	public String toString( ) {
		return "%" + super.toString( );
	}
}
