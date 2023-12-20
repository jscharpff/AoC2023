package challenges.day20.gate;

import java.util.ArrayList;
import java.util.List;

import aocutil.object.LabeledObject;
import challenges.day20.Pulse;

/**
 * Abstract class to implement specific gates with their own logic
 * 
 * @author Joris
 */
public abstract class Gate extends LabeledObject {
	/** The list of input gates */
	protected final List<Gate> ins;

	/** The list of output gates */
	protected final List<Gate> outs;

	/**
	 * Creates a new gate
	 * 
	 * @param label The label of the gate
	 */
	public Gate( String label ) {
		super( label );
		ins = new ArrayList<>( );
		outs = new ArrayList<>( );
	}
	
	/**
	 * Creates a link between to output of this gate to the input of the other
	 * gate 
	 * 
	 * @param g The gate's input to connect to
	 */
	public void connectTo( final Gate g ) {
		outs.add( g );
		g.ins.add( this );
	}
	
	/**
	 * Disconnects this gate's output from the specified other gate
	 * 
	 * @param g The gate to disconnect from
	 */
	public void disconnectFrom( final Gate g ) {
		if( !outs.contains( g ) ) throw new RuntimeException( "Failed to disconnect: " + getLabel( ) + " is not connected to " + g.getLabel( ) );
		outs.remove( g );
		g.ins.remove( this );
	}
	
	/** @return The current state the gate is in */
	public abstract boolean getState( );
	
	/**
	 * Processes a pulse signal
	 * 
	 * @param input The pulse send to the gate
	 * @return The list of pulses that this gate outputs after processing 
	 */
	public final List<Pulse> receive( final Pulse input ) {
		final Boolean result = process( input );
		if( result == null ) return new ArrayList<>( );
		
		final List<Pulse> send = new ArrayList<>( );		
		for( final Gate g : outs ) send.add( new Pulse( this, g, result.booleanValue( ) ) );
		return send;
	}
	
	/**
	 * Function for gates to override to implement their own logic. The function
	 * should return either a true/false signal to send to all of the outputs or
	 * a null when no output is to be sent.
	 * 
	 * @param input The input pulse to process
	 * @return The result of processing the input
	 */
	protected abstract Boolean process( final Pulse input );
	
	/**
	 * Sends the signal to all of the outputs
	 * 
	 * @param signal The signal to send
	 * @return A list of pulses, one per gate connected to the outputs
	 */
	protected List<Pulse> send( final boolean signal ) {
		final List<Pulse> send = new ArrayList<>( );
		for( final Gate g : outs ) send.add( new Pulse( this, g, signal ) );
		return send;
	}
		
	/** @return The inputs connected to this gate */
	public List<Gate> inputs( ) { return ins; }
	
	/** @return The outputs connected to this gate */
	public List<Gate> outputs( ) { return outs; }
	
	/**
	 * @return A very descriptive string of the gate layout
	 */
	public String toFullString( ) {
		final StringBuilder sb = new StringBuilder( );		
		if( ins.size( ) > 0 ) sb.append( ins.toString( ) + " -> " );
		sb.append( "(" );
		sb.append( toString( ) );
		sb.append( ")" );
		if( outs.size( ) > 0 ) sb.append( " -> " + outs  );
		return sb.toString( );
	}
	
	/**
	 * Serialises the gate into a string that can be read by the SandMachine
	 * input parser
	 * 
	 * @return The string that encodes the gate configuration  
	 */
	public String serialise( ) {
		final StringBuilder sb = new StringBuilder( );
		if( this instanceof ConGate ) sb.append( "&" );
		else if( this instanceof FFGate ) sb.append( "%" );
		
		sb.append( getLabel( ) );
			if( !outs.isEmpty( ) ) {
			sb.append( " -> " );
			sb.append( outs.get( 0 ).getLabel( ) );
			for( int i = 1; i < outs.size(); i++ ) {
				sb.append( ", " );
				sb.append( outs.get( i ).getLabel( ) );
			}
		}
		
		return sb.toString( );
	}

	/**
	 * Reconstructs the gate from a string encoding (as produced by the serialise
	 * method)
	 * 
	 * @param in The input string
	 * @return The reconstructed gate
	 */
	public static Gate fromString( final String in ) {
		if( in.equals( "broadcaster" ) ) return new BroadcastGate( );
		if( in.startsWith( "%" ) ) return new FFGate( in.substring( 1 ) );
		if( in.startsWith( "&" ) ) return new ConGate( in.substring( 1 ) );

		throw new RuntimeException( "Unknown gate: " + in );
	}
}
