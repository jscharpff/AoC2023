package challenges.day12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Class that counts unique configurations of blocks of springs, given a layout
 * that describes potential spring positions.
 * 
 * @author Joris
 */
public class SpringConfigurator {
	/** The spring slots layout */
	protected final String springs;
	
	/** The block sizes of springs to configure in the layout */
	protected final List<Integer> blocks;
	
	/**
	 * Creates a new configurator
	 * 
	 * @param springlayout The layout with possible spring positions
	 * @param blocks The blocks of springs that need to be placed in the given
	 *   layout
	 */
	protected SpringConfigurator( final String springlayout, final List<Integer> blocks ) {
		this.springs = springlayout;
		this.blocks = blocks;
	}
	
	/**
	 * Counts the number of unique configurations possible, given the layout and
	 * blocks. 
	 * 
	 * @param folds The number of repetitions of the layout and block set
	 * @return The total count of unique block configurations possible in the
	 *   given (folded) layout
	 */
	public long countConfigurations( final int folds ) {
		// create initial state to solve from
		Map<String, Long> S = new HashMap<String, Long>( );
		final StringBuilder sb = new StringBuilder( );
		sb.append( springs );
		for( int i = 1; i < folds; i++ ) sb.append( "?" + springs );
		S.put( sb.toString( ), 1l );

		// create the stack of spring blocks that need to be fitted in the slots 
		final Stack<Integer> blockstack = new Stack<>( );
		for( int i = 0; i < folds; i++ ) blockstack.addAll( blocks );
		Collections.reverse( blockstack );
		
		// keep track of the minimum required size to fit the remaining blocks to
		// help identify invalid configurations quickly (i.e., no more space to fit
		// all remaining blocks anyway) 
		int B = blocks.stream( ).mapToInt( x -> x ).sum( ) + blocks.size( ) - 1;

		// generate all unique next states from this state by fixing the first
		// block and keep track of count per state
		while( !blockstack.isEmpty( ) ) {
			final Map<String, Long> Snew = new HashMap<String, Long>( );
			
			// try next block and generate new states
			final int block = blockstack.pop( );
			for( final String state : S.keySet( ) ) {
				
				// determine set of new states reachable from this state
				final Map<String, Long> configs = new HashMap<>( );				
				for( int i = 0; i < state.length( ) - B + 1; i++ ) {
					if( !fits( state, i, block ) ) continue;
					
					// new valid configuration for this block, add it as a next state
					final String key = state.substring( Math.min( i + block + 1, state.length( ) ) );
					configs.put( key, configs.getOrDefault( key, 0l ) + 1 );
				}

				// decrease size of spring blocks still to be slotted with the size of
				// just fitted block (and a single empty slot following it)
				B -= block + 1;

				// add new states to the set for next run, also keeping track of the
				// number of times each state has been encountered
				for( final String cfg : configs.keySet( ) )				
					Snew.put( cfg, Snew.getOrDefault( cfg, 0l ) + S.get( state ) * configs.get( cfg ) );
			}
			
			// swap next states with current state set and try next block, if any
			S = Snew;			
		}
		
		// remove invalid states from the map and return the count		
		return S.entrySet( ).stream( ).filter( e -> !e.getKey( ).contains( "#" ) ).mapToLong( e -> e.getValue( ) ).sum( );
	}
	
	/**
	 * Function that validates whether a block of given size can be slotted at
	 * the specified index, given the remaining slot layout 
	 * 
	 * @param springlayout The (remaining) layout of spring positions
	 * @param idx The index at which we want to fit the spring block
	 * @param blocksize The size of the block to fit
	 * @return True iff the block would fit at the given position. That is, 
	 *   fitting the block will not lead to an invalid configuration 
	 */
	private boolean fits( final String springlayout, final int idx, final int blocksize ) {
		// would the block fit at all?
		if( idx + blocksize > springlayout.length( ) ) return false;
		
		// the block can't end just before any mandatory spring
		final int afteridx = idx + blocksize;
		if( springlayout.length( ) > afteridx && springlayout.charAt( afteridx ) == '#' ) return false;
		
		// can't put a block over a dash
		for( int i = idx; i < idx + blocksize; i++ ) if( springlayout.charAt( i ) == '.' ) return false;
		
		// would the block invalidate a passed block in the spring set?
		for( int i = 0; i < idx; i++ ) if( springlayout.charAt( i ) == '#' ) return false;
		
		return true;
	}
	
	/**
	 * Reconstructs a SpringConfigurator from a input string that describes it
	 * 
	 * @param input The string holding the layout and sizes of spring blocks to
	 *   configure, separated by a space
	 * @return The SpringConfigurator
	 */
	public static SpringConfigurator fromString( final String input ) {
		// split input into two parts
		final String[] s = input.split( " " );
		
		// get the sizes of the spring blocks
		final List<Integer> blocksizes = new ArrayList<>( );
		for( final String sz : s[1].split( "," ) )
			blocksizes.add( Integer.parseInt( sz ) );
		
		return new SpringConfigurator( s[0], blocksizes );
	}
}
