package challenges.day23;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.grid.CoordGrid;

/**
 * Class that helps to find the longest possible path on a map of trails, 
 * accounting for slopes on the route
 * 
 * @author Joris
 */
public class HikeTrailsSloped {
	/** The map of trails */
	protected final CoordGrid<Character> map;
	
	/** Map of POIs */
	protected final Map<POI, Map<POI, Long>> POIs;
	
	/**
	 * Recreates the trails from a textual description of the map layout
	 * 
	 * @param input The list of strings describing the map
	 */
	public HikeTrailsSloped( final List<String> input ) {
		this.map = CoordGrid.fromCharGrid( input, '.' );
		this.POIs = findPOIs( );
	}
	
	/**
	 * Finds all points of interest (POIs) on the map and builds the All Pair
	 * Longest Path map from every POI to every other POI. The POIs are the
	 * start, end and slopes 
	 * 
	 * @return The APLP map from every POI to every other POI
	 */
	private Map<POI, Map<POI, Long>> findPOIs( ) {
		final Map<POI, Map<POI, Long>> P = new HashMap<>( );
		
		// get starting and ending point
		final Stack<POI> rem = new Stack<>( );
		for( int x = map.window( ).getMinX( ); x <= map.window( ).getMaxX( ); x++ ) {
			if( map.get( x, map.window( ).getMinY( ) ) == '.' ) {
				final POI start = new POI( new Coord2D( x, map.window( ).getMinY( ) ), 'S' );
				map.set( start.pos, 'S' );
				rem.push( start );
			}
			if( map.get( x, map.window( ).getMaxY( ) ) == '.' ) {
				final POI end = new POI( new Coord2D( x, map.window( ).getMaxY( ) ), 'E' );
				map.set( end.pos, 'E' );
				P.put( end, new HashMap<>( ) );
			}
		}
		
		// find all reachable POIs from the start on the map
		while( !rem.isEmpty( ) ) {
			final POI p = rem.pop( );
			final Map<POI, Long> pmap = findPOIs( p ) ;
			P.put( p, pmap );
			
			// check if there are new, unvisited POIs to explore from
			for( final POI pnew : pmap.keySet( ) )
				if( !P.containsKey( pnew ) && pnew.ch != 'E' ) rem.push( pnew );
		}
		return P;
	}
	
	/**
	 * Finds the APLP routes to every other POI reachable from this one
	 * 
	 * @param p The POI to start from
	 * @return The map of distances from this POI to every other POI
	 */
	private Map<POI, Long> findPOIs( final POI p ) {
		final Map<POI, Long> P = new HashMap<>( );
		
		// run BFS algorithm to find all reachable POIs
		final Set<Coord2D> V = new HashSet<>( );
		V.add( p.pos );
		Stack<Move> S = new Stack<>( );
		S.push( p.getStartingMove( ) );
		long dist = 1;
		
		while( !S.isEmpty( ) ) {
			final Stack<Move> nextS = new Stack<>( );
			
			while( !S.isEmpty( ) ) {
				final Move move = S.pop( );
				if( V.add( move.to ) );
				
				// chcek if this is a POI?
				final char ch = map.get( move.to );
				if( ch != '.'  ) {
					// yes, check if we can climb this slope (always true for start and
					// end)
					final POI poi = new POI( move.to, ch );
					if( poi.canScale( move ) ) P.put( poi, dist );
					
					// anyway terminate the search here, we are not going to be able to
					// reach more POIs form this point onward
					continue;
				}
				
				// no POI, explore further
				for( final Direction d : Direction.values( ) ) {
					final Move nextmove = new Move( move.to.move( d, 1 ), d );
					if( !map.contains( nextmove.to ) || map.get( nextmove.to ) == '#' ) continue;
					if( V.contains( nextmove.to ) ) continue;
					nextS.push( nextmove );
				}
			}
			S = nextS;
			dist++;
		}
		
		return P;
	}
	
	/**
	 * Container that desribes a single move on the map
	 * 
	 * @author Joris
	 */
	private class Move {
		/** The position we are moving to */
		final Coord2D to;
		
		/** The direction we are moving into */
		final Direction dir;
		
		/**
		 * Creates a new move
		 * 
		 * @param to The coordinate to move to
		 * @param d The direction in which we are moving
		 */
		public Move( final Coord2D to, final Direction d ) {
			this.to = to; 
			this.dir = d;
		}
	}
	
	/**
	 * Finds the longest possible route over all trails on the map from start to
	 * end
	 * 
	 * @return The length of the longest route possible
	 */
	public long findLongestRoute( ) {
		// find start and end
		POI start = null;
		POI end = null;
		for( final POI p : POIs.keySet( ) ) {
			if( p.ch == 'S' ) start = p;
			else if( p.ch == 'E' ) end = p;
		}
		if( start == null || end == null ) throw new RuntimeException( "Failed to find start and end coordinates" );
		
		
		return findLongestRoute( start, end );
	}
	
	/**
	 * Finds the longest possible route from the given POI from to the target POI
	 * 
	 * @param from The starting POI
	 * @param to The target POI
	 * @return The longest possible path between the two
	 */
	private long findLongestRoute( final POI from, final POI to ) {
		// are we there yet?
		if( from.equals( to ) ) return 0;

		// nope, explore further
		long longest = Long.MIN_VALUE;
		final Map<POI, Long> N = POIs.get( from );
		for( final POI next : N.keySet( ) ) {
			final long dist = N.get( next ) + findLongestRoute( next, to );
			if( dist > longest ) longest = dist;
		}
		return longest;
	}
	
	/**
	 * Point of interest on the map
	 * 
	 * @author Joris
	 */
	private class POI {
		/** The location of the POI */
		final Coord2D pos;
		
		/** The POI itself */
		final char ch;
		
		/**
		 * Creates a new POI
		 * 
		 * @param pos The location of the POI
		 * @param ch The character that describes the POI
		 */
		public POI( final Coord2D pos, final char ch ) {
			this.pos = pos;
			this.ch = ch;
		}
		
		/**
		 * Gets the first move we make if we would walk a trail starting from this
		 * POI.
		 * 
		 * @return The move that describes the start of the trail from the POI
		 */
		public Move getStartingMove( ) {
			switch( ch ) { 
				case 'S': return new Move( pos.move( Direction.South, 1 ), Direction.South );
				case 'E': return null;
				default: 
					final Direction d = Direction.fromSymbol( ch );
					return new Move( pos.move( d, 1 ), d );
			}
		}
		
		/**
		 * Checks if we can 'scale' this POI (slope) with the specified move
		 * 
		 * @param move The move that describes the coordinate and direction we are
		 *   moving into
		 * @return True iff the POI can be reached by the move. For a slope this
		 *   means that the move must be in the direction of the slope. For other
		 *   POIs it will always return true;
		 */
		public boolean canScale( final Move move ) {
			if( ch == 'S' || ch == 'E' ) return true;
			return Direction.fromSymbol( ch ).equals( move.dir );
		}
		
		/**
		 * Two POIs are equal if the position and character match
		 * 
		 * @param obj The object to compare against
		 * @return True if the other object is a POI at the same location and of
		 *   the same character
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof POI)) return false;
			final POI p = (POI)obj;
			return p.pos.equals( pos ) && p.ch == ch;
		}
		
		/** @return The string describing the POI */
		@Override
		public String toString( ) {
			return pos + ": " + ch;
		}
		
		/** @return The hash code of the unique string */
		@Override
		public int hashCode( ) {
			return toString( ).hashCode( );
		}
	}
}
