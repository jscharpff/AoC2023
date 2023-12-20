package challenges.day20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aocutil.collections.CollectionUtil;
import aocutil.number.NumberUtil;
import challenges.day20.gate.BroadcastGate;
import challenges.day20.gate.Gate;
import challenges.day20.gate.OutputGate;

/**
 * A machine that produces sand using illogical gates as its logic
 * 
 * @author Joris
 */
public class SandMachine {
	/** The gates in the machine in their processing order */
	protected final List<Gate> gates;
	
	/** The broadcast gate, this is where all processing starts */
	protected final Gate bcgate;
	
	/**
	 * Creates a new Sand Machine from a list of gate descriptions
	 * 
	 * @param gatelist The list of gates in the machine
	 */
	public SandMachine( final List<String> gatelist ) {
		// first get all the gates
		BroadcastGate bg = null;
		gates = new ArrayList<>( );
		for( final String g : gatelist ) {
			final Gate gate = Gate.fromString( g.split( " ->" )[0] );
			gates.add( gate );
			if( gate instanceof BroadcastGate ) bg = (BroadcastGate) gate;
		}

		// then connect the gates to one another
		for( final String gs : gatelist ) {
			final String g[] = gs.split( " -> " );
			final Gate from = getGate( g[0].startsWith( "%" ) || g[0].startsWith( "&" ) ? g[0].substring( 1 ) : g[0] );
			for( final String to : g[1].split( ", " ) ) {
				try {
					from.connectTo( getGate( to ) );
				} catch( final RuntimeException e ) {
					/* non existing gate, consider this an output gate */
					final Gate outgate = new OutputGate( to ); 
					gates.add( outgate );
					from.connectTo( outgate );
				}
			}
		}

		// finally set the broadcast gate as all processing starts there
		bcgate = bg;
	}
	
	/**
	 * Retrieves a gate by its name
	 * 
	 * @param name The gate label
	 * @return The gate object in the machine with the given name
	 */
	private Gate getGate( final String name ) {
		for( final Gate g : gates )
			if( g.getLabel( ).equals( name ) ) return g;
		throw new RuntimeException( "No such gate in the machine: " + name );
	}
	
	/**
	 * Runs the machine for the specified number of times, counting the number of
	 * high and low signals emitted.
	 * 
	 * @param times The number of times to run the program
	 * @return The product of the total high and low signal counts
	 */
	public long run( final long times ) {
		// use a FIFO queue for signals and count them
		final LinkedList<Pulse> P = new LinkedList<>( );
		long high = 0;
		long low = 0;
		
		// push the button as many times as specified
		for( int i = 0; i < times; i++ ) {
			// push the button!
			P.add( new Pulse( null, bcgate, false ) );
			while( !P.isEmpty( ) ) {
				final Pulse p = P.removeFirst( );
				if( p.signal ) high++; else low++;
				
				// send the next signal and get all outputs
				P.addAll( p.destination.receive( p ) );
			}	
		}		
		return high * low;
	}
	
	/**
	 * Finds the first cycle of the machine at which the specified configuration
	 * is seen.
	 * 
	 * @param configuration The configuration of gates and their signals we want
	 *   to detect
	 * @return The number of cycles required to produce the configuration
	 */
	public long firstSignalConfiguration( final Map<String, Boolean> configuration ) {
		// find all intervals at which the gates specified in the configuration
		// emit the signal we are after
		final List<Set<Long>> I = new ArrayList<>( );
		for( final String gatename : configuration.keySet( ) ) {
			final Gate gate = getGate( gatename );
			final SandMachine submachine = subprogram( gate );
			I.add( submachine.findIntervals( gate.getLabel( ), configuration.get( gatename ) ) );
		}
		
		// generate all possible unique combinations of these intervals
		final List<List<Long>> perms = CollectionUtil.generateCombinations( I );
		
		// find lowest common multiplier of each combination and return the minimum
		// of these to produce the interval in which they are all sending a low
		// signal concurrently for the first time
		long lowestInterval = Long.MAX_VALUE;
		while( !perms.isEmpty( ) ) {
			final List<Long> Ivals = perms.remove( perms.size( ) - 1 );
			while( Ivals.size( ) > 1 ) {
				final long l1 = Ivals.remove( Ivals.size( ) - 1 );
				final long l2 = Ivals.remove( Ivals.size( ) - 1 );
				Ivals.add( NumberUtil.lowestCommonMultiplier( l1, l2 ) );
			}
			if( Ivals.get( 0 ) < lowestInterval ) lowestInterval = Ivals.get( 0 );
		}
		return lowestInterval;
	}

	/**
	 * Produces the set of intervals at which the given gate emits the specified
	 * value within one complete cycle of the machine execution. A complete cycle
	 * occurs when all gates are back to the same states as when starting the
	 * machine.
	 * 
	 * @param gatename The gate to monitor the output of
	 * @param value The value to detect
	 * @return The set of all cycle times at which 
	 */
	private Set<Long> findIntervals( final String gatename, final boolean value ) {
		// use a FIFO queue for signals and count them
		final LinkedList<Pulse> P = new LinkedList<>( );
		final Set<Long> intervals = new HashSet<>( );
		
		// get initial configuration
		final Map<Gate, Boolean> initstate = new HashMap<>( );
		for( final Gate g : gates ) initstate.put( g, g.getState( ) );
		
		// push the button many times to try and detect a cycle in the machine
		// execution
		for( long i = 0; i < 1000000000; i++ ) {
			// detect reset of all states to initial state, this implies a full cycle
			if( i > 0 && initstate.entrySet( ).stream( ).allMatch( e -> e.getKey( ).getState( ) == e.getValue( ) ) )
				return intervals;
			
			// nope, push the button!
			P.add( new Pulse( null, bcgate, false ) );
			while( !P.isEmpty( ) ) {
				final Pulse p = P.removeFirst( );
				
				// monitor for pulses that we are interested in
				if( p.source != null && p.source.getLabel( ).equals( gatename ) && p.signal == value )
					intervals.add( i + 1 );
				
				// send the next signal and get all outputs
				P.addAll( p.destination.receive( p ) );
			}
		}		

		// no cycle detected
		throw new RuntimeException( "Failed to determine repeating pattern in machine" );
	}

	

	/**
	 * Produces a new machine that now has the specified state as its terminal
	 * state. It does so by copying the machine and removing all gates from it
	 * that do not have any (indirect!) influence on the state of the specified
	 * terminal gate. 
	 *  
	 * @param terminal The gate that will become the new terminal gate
	 * @return A new program that only holds gates that interact directly or
	 *   indirectly with the specified terminal gate  
	 */
	private SandMachine subprogram( final Gate terminal ) {
		// first determine set of gates that influence the terminal gate's state
		final Set<Gate> used = new HashSet<>( );
		findUsedInInput( used, terminal );
		
		// copy the machine
		final SandMachine M = copy( );
		
		// disconnect all unused gates from the new machine
		for( final Gate g : M.gates ) {
			for( int i = g.outputs( ).size( ) - 1; i >= 0; i-- ) {
				final Gate out = g.outputs( ).get( i );
				if( !used.contains( out ) ) g.disconnectFrom( out );
			}

		}
		
		// then remove unused gated and return the smaller copy
		M.gates.retainAll( used );
		return M;
	}
	
	/**
	 * Recursively goes through gate connections and stores all encountered gates
	 * in a list. All machine gates that are not in this list hence do not
	 * interact with the (starting) gate.
	 *   
	 * @param used The list of gates found so fare
	 * @param gate The (starting) gate to find all input interaction of
	 */
	private void findUsedInInput( final Set<Gate> used, final Gate gate ) {
		used.add( gate );
		for( final Gate in : gate.inputs( ) ) {
			if( used.contains( in ) ) continue;
			findUsedInInput( used, in );
		}
	}
	
	/**
	 * Produces a copy of the machine with the exact same gates and connections,
	 * albeit with fresh objects as to not have referencing issues when
	 * disconnecting and removing gates. The copy is produced by serialising all
	 * gates and then calling the constructor to parse the serialised version
	 * 
	 * @return The copy of the machine
	 */
	private SandMachine copy( ) {
		// serialise the list of gates
		final List<String> serialised = new ArrayList<>( );
		for( final Gate g : gates ) {
			// strange quirk here, the output gate is not included in the gate list
			if( g instanceof OutputGate ) continue;
			serialised.add( g.serialise( ) );
		}		
		
		// and process again to create a fresh copy
		return new SandMachine( serialised );		
	}
}
