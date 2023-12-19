package challenges.day17;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.grid.CoordGrid;

/**
 * Class that handles the transport logistics of moving lava through a grid of
 * city blocks using slightly inconvenient crucibles.
 * 
 * @author Joris
 */
public class LavaLogistics {
	/** The city block heat loss grid */
	protected final CoordGrid<Integer> heatmap;
	
	/** If true, Ultra Crubles will be used for navigation */
	protected final boolean ultramode;
	
	/**
	 * Creates a new LavaLogistics manager from the list of strings that
	 * describes the city's heat loss map
	 * 
	 * @param heatmap The list of strings that textually describes the heat loss
	 *   when moving through a city block
	 * @param ultramode If false, the navigation uses crucibles that can move at
	 *   most 3 consecutive steps in a single direction. If true, ultra crucibles
	 *   are used that allow 10 steps in the same direction but require a
	 *   minimum of 4 consecutive steps before turning. 
	 */
	public LavaLogistics( final List<String> heatmap, final boolean ultramode ) {
		this.heatmap = CoordGrid.fromDigitGrid( heatmap );
		this.ultramode = ultramode;
	}
	
	/**
	 * Builds the map of heuristic values by simply finding the lowest heat value
	 * from each state towards the end position, ignoring all kinds of cumbersome
	 * movement rules (hey, it's a heuristic!)
	 *
	 * @param goal The coordinate of the goal to reach
	 * @return A grid of coordinates that for each coordinate holds the lowest
	 *   possible heat loss path to the goal when movement rules are ignored
	 */
	private CoordGrid<Integer> computeHeuristicMap( final Coord2D goal ) {
		final CoordGrid<Integer> H = new CoordGrid<Integer>( 0 );
		H.set( goal, 0 );

		// perform BFS to build heuristic map, start with an empty queue
		final Queue<QD> Q = new PriorityQueue<>( (a,b) -> a.heatloss - b.heatloss );
		for( final Coord2D n : goal.getAdjacent( false ) ) {
			if( heatmap.contains( n ) )
				Q.offer( new QD( n, heatmap.get( goal ) ) );
		}
		
		// then find the lowest heat loss to all coordinates in the grid
		while( !Q.isEmpty( ) ) {
			final QD q = Q.poll( );
			if( H.hasValue( q.pos ) ) continue;
			
			H.set( q.pos, q.heatloss );
			
			for( final Coord2D n : q.pos.getAdjacent( false ) ) {
				if( !heatmap.contains( n ) || H.hasValue( n ) ) continue;
				
				Q.offer( new QD( n, heatmap.get( n ) + q.heatloss ) );
			}
		}
		return H;
	}
	
	/**
	 * Struct to hold a state / heat loss combination for the priority queue
	 * @author Joris
	 */
	private class QD {
		public final Coord2D pos;
		public final int heatloss;
		public QD( final Coord2D pos, final int heatloss ) {
			this.pos = pos;
			this.heatloss = heatloss;
		}
	}

	/**
	 * Navigates the city heat map from the top left to the bottom right corner
	 * and returns the heat loss of the path that minimises this value
	 * 
	 * @return The heat loss of the optimal path from top left to bottom right
	 */
	public long findLowestHeatLoss( ) {
		final Coord2D start = heatmap.window( ).getMinCoord( );
		final Coord2D end = heatmap.window( ).getMaxCoord( );
		
		// compute heuristic map to guide search
		final CoordGrid<Integer> Hmap = computeHeuristicMap( end );
		
		// start navigation procedure
		return navigate( Hmap, start, end );
	}
	
	/**
	 * Navigates the heatmap to find the path with the lowest heat loss possible
	 * from start to end. The algorithm uses a A*-like exploration that uses a
	 * priority queue of states with an associated heuristic value to optimise
	 * the exploration of lowest heat paths. The states that are explored by the
	 * algorithm depend on the value of the ultramode parameter.
	 * 
	 * @param Hmap The map of heuristic values per coordinate of the heat map,
	 *   used to set the heuristic value for queue elements
	 * @param start The coordinate of the starting position
	 * @param end The target coordinate to reach
	 * @return The heat loss of the path from start to end that minimises heat
	 *   loss.
	 */
	private long navigate( final CoordGrid<Integer> Hmap, final Coord2D start, final Coord2D end ) {
		// create an exploration queue that orders on heuristic value
		final Queue<QElem> Q = new PriorityQueue<>( );
		Q.add( new QElem( new LLState( start ), 0, Hmap.get( start ) ) );
		
		// keep track of visited moves and upper bound on heat loss
		final Map<LLState, Integer> V = new HashMap<>( );		
		int UB = Integer.MAX_VALUE;
		
		// keep track of optimal moves from each coordinate		
		while( !Q.isEmpty( ) ) {
			final QElem q = Q.poll( );
			final LLState state = q.state;
			
			// check if the queue element has been bested in the meantime
			if( hasBetter( V, q ) || UB <= q.heatloss ) continue;
			
			// mark as best known heat loss in this state
			V.put( state, q.heatloss );
			
			// are we there yet?
			if( state.pos.equals( end ) ) {
				if( UB > q.heatloss ) UB = q.heatloss;
				continue;
			}
			
			// generate neighbours of this move
			for( final LLState next : generateNextStates( state ) ) {
				
				// check if it makes sense to queue this element at all
				final QElem newq = new QElem( next, q.heatloss + heatmap.get( next.pos ), Hmap.get( next.pos ) );
				if( hasBetter( V, newq ) ) continue; 
				
				// yes, queue it for future exploration based on its heuristic value
				Q.offer( newq );
			}
		}
		
		// return the lowest heat loss which is equal to the upper bound after
		// exploring all states
		return UB;
	}

	/**
	 * Generate the set of next states that are reachable from the given state.
	 * The returned set will be different depending on the value for the
	 * ultramode parameter.
	 * 
	 * @param state The current state to generate next states for
	 * @return The set of valid next states that can be reached from the given
	 *   state
	 */
	private Set<LLState> generateNextStates( final LLState state ) {
		final Set<LLState> N = new HashSet<>( );
		for( final Direction d : Direction.values( ) ) {
			// keep search within bounds
			if( !heatmap.contains( state.pos.move( d, 1 ) ) ) continue;
			
			// cannot do a 180 degree turn
			if( d.flip( ).equals( state.dir ) ) continue;
			
			// a minimum of 4 consecutive moves in the same direction
			if( ultramode )
				if( state.dir != null && !d.equals( state.dir ) && state.count < 4 ) continue;
			
			// at most 3 or 10 consecutive moves in the same direction, depending on the mode
			if( d.equals( state.dir ) && state.count > (ultramode ? 9 : 2) ) continue;
			
			// generate and add the new state
			N.add( state.move( d ) );
		}
		return N;
	}
	
	/**
	 * Checks if the current least heat loss map has a better value for the state
	 * in the QElem we want to consider
	 * 
	 * @param V The map of best known heat losses per state
	 * @param q The QElem to test against the map
	 * @return True iff the heat loss recorded in the QElem is higher than the
	 *   current lowest known heat loss for the associated state 
	 */
	private boolean hasBetter( final Map<LLState, Integer> V, final QElem q ) {
		// has the state been seen before?
		if( !V.containsKey( q.state ) ) return false;		
		return V.get( q.state ) <= q.heatloss;
	}
	
	/**
	 * A single element for our priority queue that holds a state, total heat
	 * loss so far and the heuristic value for the element. 
	 * 
	 * @author Joris
	 */
	private class QElem implements Comparable<QElem> {
		/** The state to explore from */
		protected final LLState state;
		
		/** The total heat loss so far */
		protected final int heatloss;
		
		/** The heuristic value for the coordinate in the state */
		protected final int heur;
		
		/**
		 * Creates a new queue element
		 * 
		 * @param state The state to explore
		 * @param heatloss The heat loss so far
		 * @param hval The heuristic value of the state for queueing priority
		 */
		protected QElem( final LLState state, final int heatloss, final int hval ) {
			this.state = state;
			this.heatloss = heatloss;
			this.heur = heatloss + hval;
		}
		
		/**
		 * Compares queue elements based upon their heuristic value
		 * 
		 * @param q The QElem to compare agianst
		 * @return heur - q.heur
		 */
		@Override
		public int compareTo( final QElem q ) {
			return heur - q.heur;
		}
		
		/** @return The string describing the queue element */
		@Override
		public String toString( ) {
			return "<" + state.toString( ) + ": " + heatloss + " (h = " + heur + ")>";
		}
	}
	
	/**
	 * A single state of the navigation process
	 *  
	 * @author Joris
	 */
	private class LLState {
		/** The position of the state */
		protected final Coord2D pos;
		
		/** The direction in which we last moved move into */
		protected final Direction dir;
		
		/** The number of times this direction was moved into previously */
		protected final int count;
		
		/** The string that uniquely describes this state */
		private final String statestr;
		
		/**
		 * Creates a new state
		 * 
		 * @param pos The position we are in
		 * @param dir The direction we last moved in
		 * @param count The number of times we moved in that direction recently
		 */
		private LLState( final Coord2D pos, final Direction dir, final int count ) {
			this.pos = pos;
			this.dir = dir;
			this.count = count;
			
			// generate unique state string for comparison and hashing
			this.statestr = dir == null ? "(start)" : pos + " " + dir.toSymbol( ) + count;
		}
		
		/**
		 * Creates a new, empty move for the given direction from the (starting)
		 * position
		 * 
		 * @param pos The starting position
		 */
		public LLState( final Coord2D pos ) {
			this( pos, null, 0 );
		}
		
		/**
		 * Extends the state by moving 1 step in the given direction.
		 * 
		 * @param d The direction to move in
		 * @return The new state that is generated
		 */
		private LLState move( final Direction d ) {
			return new LLState( pos.move( d, 1 ), d, d == dir ? count + 1 : 1 );
		}
		
		/**
		 * Tests whether this state is equal to the given object
		 * 
		 * @param obj The object to test against
		 * @return True iff their unique state strings are equal
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof LLState) ) return false;
			return statestr.equals( ((LLState)obj).statestr );
		}
		
		/** @return The state string */
		@Override
		public String toString( ) {
			return statestr;
		}
		
		/** @return The hash code of the unique state string */
		@Override
		public int hashCode( ) {
			return statestr.hashCode( );
		}
	}
}
