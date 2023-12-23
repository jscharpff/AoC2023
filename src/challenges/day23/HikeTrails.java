package challenges.day23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.grid.CoordGrid;

/**
 * Class that helps plan the longest route possible over a map of paths
 * 
 * @author Joris
 */
public class HikeTrails {
	/** The map that describes the trails */
	protected final CoordGrid<Character> map;
	
	/** The All Pair Longest Path map that for every coordinate of interest
	 * (start, end and junctions) describes the longest path to reachable other
	 * interesting locations */
	protected final Map<Coord2D, List<Path>> APLP;
	
	/**
	 * Builds the trail routes from the given map
	 * 
	 * @param input List of string that visually describe the layout of the
	 * terrain
	 */
	public HikeTrails( final List<String> input ) {
		this.map = CoordGrid.fromCharGrid( input, '.' );		
		this.APLP = buildAPLP( );
	}
	
	/**
	 * Builds the All Pair Longest Path map for every point of interest on the
	 * map, in this case the POIs are the start, end and junctions. This because
	 * that are the locations at which we have to make a routing choice. Note
	 * that the APLP algorithm here looks very much like an APSP one.. Due to the
	 * structure of the input, the longest path is equal to the shortest path
	 * from A to B.
	 * 
	 * @return The map that describes the APLP for all coordinates of interest
	 */
	private Map<Coord2D, List<Path>> buildAPLP( ) {
		final Stack<Coord2D> rem = new Stack<>( );
		
		// first determine start and end coordinates, also writing them on the map
		// for future searches
		for( int x = map.window( ).getMinX( ); x <= map.window( ).getMaxX( ); x++ ) {
			if( map.get( x, map.window( ).getMinY( ) ) == '.' ) {
				final Coord2D start = new Coord2D( x, map.window( ).getMinY( ) );
				map.set( start, 'S' );
				rem.add( start );
			}
			if( map.get( x, map.window( ).getMaxY( ) ) == '.' ) {
				final Coord2D end = new Coord2D( x, map.window( ).getMaxY( ) );
				map.set( end, 'E' );
			}
		}
		
		// now build APLP from every point of interest to all reachable ones, 
		// starting from the start coordinate
		final Map<Coord2D, List<Path>> APLP = new HashMap<>( );
		while( !rem.isEmpty( ) ) {
			final Coord2D c = rem.pop( );
			final List<Path> P = buildAPLP( c );
			APLP.put( c, P );
			
			// check trail ends for the coordinates to explore next
			for( final Path next : P ) {
				if( !APLP.containsKey( next.to ) ) rem.push( next.to );
			}
		}
		
		return APLP;
	}
	
	/**
	 * Builds the APLP path list for all coordinates reachable from the given
	 * start coordinate
	 * 
	 * @param start The starting position
	 * @return The list of paths possible from this start coordinate
	 */
	private List<Path> buildAPLP( final Coord2D start ) {
		// find all junctions in the map using a BFS algorithm
		final List<Path> T = new ArrayList<>( );
		final Set<Coord2D> V = new HashSet<>( );
		Stack<Coord2D> S = new Stack<>( );
		S.push( start );
		long dist = 0;
		
		// keep exploring until we seen all reachable coordinates
		while( !S.empty( ) ) {
			final Stack<Coord2D> newS = new Stack<>( );
			
			// next round!
			while( !S.empty( ) ) {
				final Coord2D c = S.pop( );
				V.add( c );
				
				// check for junctions or the end coordinate
				final int count = Stream.of( Direction.values( ) ).mapToInt( d -> map.get( c.move( d, 1 ) ) != '#' && map.contains( c.move( d, 1 ) ) ? 1 : 0 ).sum( );
				if( (count >= 3 && !c.equals( start )) || (map.get( c ) == 'E') ) {
					T.add( new Path( start, c, dist ) );
					continue;
				}
				
				// not at an interesting coordinate, move on
				for( final Direction d : Direction.values( ) ) {
					final Coord2D newc = c.move( d, 1 );
					if( !map.contains( newc ) || map.get( newc ) == '#' ) continue;
					if( V.contains( newc ) ) continue;
					newS.push( newc );
				}
				
			}
			
			// done with this distance, swap sets and explore next distance
			S = newS;
			dist++;
		}
		
		return T;
	}
	
	/**
	 * Finds the longest possible route from the start coordinate to the end
	 * coordinate on the map
	 * 
	 * @return The length of the longest possible route that does not visit a
	 *   single tile twice
	 */
	public long findLongestRoute( ) {
		// get start and end coordinates
		Coord2D start = null;
		Coord2D end = null;
		for( final Coord2D c : map.getKeys( ) ) {
			if( map.get( c ).equals( 'S' ) ) start = c;
			else if( map.get( c ).equals( 'E' ) ) end = c;
		}
		if( start == null || end == null ) throw new RuntimeException( "Failed to find start and end coordinates" );
		
		// now find the longest route by trying all paths possible from the start
		final List<Coord2D> route = new ArrayList<>( );
		route.add( start );
		return findLongestRoute( start, end, route );
	}
	
	/**
	 * Explores all paths from a given coordinate to the target coordinate
	 * 
	 * @param from The coordinate we are at
	 * @param to The coordinate we are trying to reach
	 * @param route The route so far as a list of unique coordinates
	 * @return The length of the longest path possible from here
	 */
	private long findLongestRoute( final Coord2D from, final Coord2D to, final List<Coord2D> route ) {
		// are we there yet?
		if( from.equals( to ) ) return 0;

		// start with a very low value to make sure that invalid routes are 
		// discarded. I.e., when no new path is available
		long longest = Long.MIN_VALUE;

		
		// get all possible paths from this coordinate
		final List<Path> N = APLP.get( from );
		for( final Path p : N ) {
			// already in the path?
			final Coord2D target = p.getOther( from );
			if( route.contains( target ) ) continue;
			
			// nope, extend the current route and continue the process
			final List<Coord2D> newroute = new ArrayList<>( route );
			newroute.add( target );
			final long dist = p.dist + findLongestRoute( target, to, newroute );
			if( dist > longest ) longest = dist;
		}
		
		return longest;
	}

	/**
	 * Container for a simple path from A to B with distance
	 * 
	 * @author Joris
	 */
	private class Path {
		/** The starting coordinate of the path */
		final Coord2D from;
		
		/** The ending coordinate of the path */
		final Coord2D to;
		
		/** The path length */
		final long dist;
		
		/**
		 * Creates a new path from A to B
		 * 
		 * @param from The starting coordinate
		 * @param to The ending coordinate
		 * @param dist The length of the path
		 */
		public Path( final Coord2D from, final Coord2D to, final long dist ) {
			this.from = from;
			this.to = to;
			this.dist = dist;
		}
		
		/**
		 * Returns the other end of the path from the given path coordinate
		 * 
		 * @param c The coordinate on the path
		 * @return To if c == from, from if c == to
		 */
		public Coord2D getOther( final Coord2D c ) {
			if( c.equals( from ) ) return to;
			if( c.equals( to ) ) return from;
			throw new RuntimeException( "Coordinate " + c + " is not on path " + this );
		}
		
		/**
		 * Two paths are equal iff their coordinates agree (regardless of order)
		 * and their distances are equal
		 * 
		 * @param obj The object to compare against
		 * @return True iff both paths have the same coordinates and distance
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof Path) ) return false;
			final Path p = (Path)obj;
			return (p.from.equals( from ) && p.to.equals( to )) || (p.from.equals( to ) && p.to.equals( from )) && p.dist == dist;
		}
		
		/**
		 * @return The description of the path
		 */
		@Override
		public String toString( ) {
			return "[" + dist + "] " + from + " -> " + to;
		}
		
		/**
		 * @return The has code of the unique string
		 */
		@Override
		public int hashCode( ) {
			return toString( ).hashCode( );
		}
	}
}
