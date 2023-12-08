package challenges.day08;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aocutil.number.NumberUtil;
import aocutil.string.RegexMatcher;

/**
 * A Desert Maze consisting of nodes with left and right paths to other nodes
 * that has to be navigated in order to pass through the sand storm.
 * 
 * @author Joris
 */
public class DesertMaze {
	/** The nodes in the desert */
	protected final Map<String, String[]> nodes;

	/**
	 * Creates a new and empty desert maze
	 */
	private DesertMaze( ) {
		nodes = new HashMap<>( );
	}
		
	/**
	 * Simply navigate the desert from position 'AAA' until we reach the end
	 * position 'ZZZ'
	 * 
	 * @param moves The moves to navigate the desert with
	 * @return The number of steps required to get from 'AAA' to 'ZZZ'
	 */
	public long navigateAToZ( final String moves ) {
		final Set<String> ends = new HashSet<>( );
		ends.add( "ZZZ" );
		return navigate( "AAA", ends, moves );
	}

	/**
	 * Navigate the desert as a ghost, that is, from several starting positions
	 * (ending with 'A') at the same time, until we are in ending positions
	 * (ending with 'Z') SIMULTANEOUSLY.
	 * 
	 * @param moves The move set to navigate the desert
	 * @return The number of steps required to end up in all ending positions
	 *   at the same time.
	 */
	public long navigateGhostly( final String moves ) {
		// determine all starting and ending positions
		final Set<String> starts = new HashSet<>( );
		final Set<String> ends = new HashSet<>( );
		for( final String n : nodes.keySet( ) ) {
			if( n.endsWith( "A" ) ) starts.add( n );
			if( n.endsWith( "Z" ) ) ends.add( n );
		}
		
		// find the times at which each individual starting position ends in a Z
		// state and then find their lowest common multiplier. That would be the
		// first time that all ghosts end up in the same position
		//
		// N.B.: Manual inspection showed that the input was carefully constructed
		// such that for every ghost the navigation is a perfect repetition from
		// initial state to always the same, single end state. In other words, only
		// the number of steps to reach the end state is required to determine
		// their joint end state, not some complex interval function arithmetics.
		long prod = 1;
		for( final String s : starts ) {
			final long f = navigate( s, ends, moves );
			prod = NumberUtil.lowestCommonMultiplier( prod, f );
		}		
		return prod;
	}
	
	/**
	 * Performs the actual navigation from the start state to any of the end
	 * states, given by the set.
	 * 
	 * @param start The start position
	 * @param ends The set of terminal states
	 * @param moves The moves to perform to navigate the desert
	 * @return The number of steps needed to reach any of the terminal states
	 */
	public long navigate( final String start, final Set<String> ends, final String moves ) {
		// initiate search
		long steps = 0;
		int cidx = 0;
		String curr = start;
		
		// keep going until we end up in a end state or we've exhausted our patience
		while( steps < 1000000000l ) {
			// use next character for navigation, wrap index around if no more length
			char n = moves.charAt( cidx );
			cidx = (cidx + 1) % moves.length( );

			// use the move to go the next node
			curr = nodes.get( curr )[ n == 'L' ? 0 : 1 ];
			steps++;

			// are we there yet?
			if( ends.contains( curr ) ) return steps;
		}
		
		// no end state found after a lot of tries? just give up already!
		throw new RuntimeException( "Failed to reach any of the ending states " + ends + " from starting state " + start );
	}

	/**
	 * Reconstructs the desert maze from a list of string describing the nodes
	 * 
	 * @param input The node list as node = (left node, right node)
	 * @return The DesertMaze
	 */
	public static DesertMaze fromStringList( final List<String> input ){
		final DesertMaze DM = new DesertMaze( );
		
		// process all nodes and paths
		for( final String i : input ) {
			final RegexMatcher rm = RegexMatcher.match( "([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)", i );
			DM.nodes.put( rm.get( 1 ), new String[] { rm.get( 2 ), rm.get( 3 ) } );
		}

		return DM;
	}
}
