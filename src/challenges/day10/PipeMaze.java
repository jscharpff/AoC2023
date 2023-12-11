package challenges.day10;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.geometry.Window2D;
import aocutil.grid.CoordGrid;

/**
 * Container for a maze consisting of straight and corner pipe pieces
 * 
 * @author Joris
 */
public class PipeMaze {
	/** The maze grid */
	protected final CoordGrid<Character> maze;
	
	/** The starting point in the maze */
	protected final Coord2D start;

	/**
	 * Constructs a new maze from a textual input
	 *  
	 * @param input The maze grid layout as string list
	 */
	public PipeMaze( final List<String> input ) {
		// first parse textual maze into a grid
		this.maze = CoordGrid.fromCharGrid( input, '.' );

		// replace start by its original pipe
		start = maze.getEntries( ).stream( ).filter( e -> e.getValue( ) == 'S' ).findAny( ).get( ).getKey( );

		// then replace the start tile with its original pipe
		final char startchar;
		if( canMove( start, Direction.West ) && canMove( start, Direction.North ) ) startchar = 'J';
		else if( canMove( start, Direction.West ) && canMove( start, Direction.South ) ) startchar = '7';
		else if( canMove( start, Direction.East ) && canMove( start, Direction.North ) ) startchar = 'L';
		else if( canMove( start, Direction.East ) && canMove( start, Direction.South ) ) startchar = 'F';
		else if( canMove( start, Direction.West ) && canMove( start, Direction.East ) ) startchar = '-';
		else startchar = '|';
		maze.set( start, startchar );
	}
	
	/**
	 * Determines the farthest point on the main loop we can reach from the
	 * starting position, in either direction
	 * 
	 * @return The maximum distance from start to any of the pipes in the main
	 *   loop
	 */
	public long findFarthestPoint( ) {
		return getLoopDistances( ).values( ).stream( ).mapToInt( x -> x ).max( ).getAsInt( );
	} 

	/**
	 * Build a map of all pipes that are part of the main loop and their
	 * travel distance from the maze starting position
	 * 
	 * @return The map of main pipe parts and their distances from start in
	 *   number of pipes traversed
	 */
	private Map<Coord2D, Integer> getLoopDistances( ) {
		// do a breadth-first search to find farthest point from here
		final Map<Coord2D, Integer> visited = new HashMap<>( );
		final Stack<Coord2D> curr = new Stack<>( );
		curr.push( start );
		visited.put( start, 0 );
		int dist = 0;
		
		// keep traversing the pipes until no new coordinates are found
		while( !curr.empty( ) ) {
			final Set<Coord2D> next = new HashSet<>( );
			dist++;
			
			while( !curr.empty( ) ) {
				// check next moves from this one
				final Coord2D c = curr.pop( );				
				for( final Coord2D nc : getNextPipes( c ) ) {
					// already visited?
					if( visited.containsKey( nc ) ) continue;
					
					// nope, add it to the list for next round and store the distance to it
					next.add( nc );
					visited.put( nc, dist );
				}
			}
			
			// set list of nodes for the next round
			curr.addAll( next );
		}
		
		// return set of distance we've travelled
		return visited;
	}
	
	/**
	 * Get the set of pipes we can walk from the current position
	 * 
	 * @param curr The current position
	 * @return The set of neighbouring pipes that can be visited from here
	 */
	private Set<Coord2D> getNextPipes( final Coord2D curr ) {
		final Set<Coord2D> N = new HashSet<>( );
		
		for( final Direction d : Direction.values( ) )
			if( canMove( curr, d ) ) N.add( curr.move( d, 1 ) );
				
		return N;
	}

	/**
	 * Checks if we can move into the specified direction
	 * 
	 * @param curr The current coordinate
	 * @param dir The direction to move towards
	 * @return True iff there is a pipe at the new position that we can move to
	 *   from the current pipe piece we are on
	 */
	protected boolean canMove( final Coord2D curr, final Direction dir ) {
		final char currch = maze.get( curr );
		final char nextch = maze.get( curr.move( dir, 1 ) );

		switch( dir ) {
			case West: return (currch == 'S' || currch == 'J' || currch == '7' || currch == '-') && (nextch == 'F' || nextch == 'L' || nextch == '-');
			case East: return (currch == 'S' || currch == 'L' || currch == 'F' || currch == '-') && (nextch == 'J' || nextch == '7' || nextch == '-');
			case North: return (currch == 'S' || currch == 'J' || currch == 'L' || currch == '|') && (nextch == '7' || nextch == 'F' || nextch == '|');
			case South: return (currch == 'S' || currch == '7' || currch == 'F' || currch == '|') && (nextch == 'J' || nextch == 'L' || nextch == '|');
			default:
				throw new RuntimeException( "Unknown direction: " + dir );
		}
	}

	/**
	 * Algorithm to count all tiles enclosed by the main pipe loop. The algorithm
	 * does this by performing these steps:
	 * 
	 * 1) find all pipes belonging to the main loop
	 * 2) create a new maze that contains only the pipes of the main loop and
	 *    explode the new maze such that each tile is replaced by a 3x3 tile so
	 *    that all spaces between walls become empty spaces that can be traversed
	 * 3) start from outside the new maze and mark all empty tiles that we can
	 *    reach by simply moving along empty spaces as being outside the main loop
	 * 4) Now every tile in the original maze can be flagged as inside the main
	 *    loop if a) it is not part of the main loop and b) its corresponding
	 *    expanded tile is not reachable from outside of the loop. Return the
	 *    count of all such inside tiles of the original maze.
	 * 
	 * @return The count of all tiles inside the main loop
	 */
	public long countEnclosedTiles( ) {
		// 1) identify all pipes part of the main loop (we are not interested in
		// the distances in the result)
		final Map<Coord2D, Integer> loop = getLoopDistances( );

		// 2) create a new maze that will hold the expanded version of the original
		// maze. Copy the main loop into the new maze but also expand the pipes so
		// that spaces between pipes of the original maze can be traversed
		final CoordGrid<Character> newmaze = new CoordGrid<Character>( maze.getDefaultValue( ) );
		for( final Coord2D c : loop.keySet( ) ) {
			// get associated expanded coordinate and character to write there
			final Coord2D newc = new Coord2D( c.x * 2, c.y * 2 );
			final char ch = maze.get( c );
			newmaze.set( newc, ch );
			
			// write neighbouring walls to reflect a 'zoomed in' version of the pipe
			// E.g., a '7' pipe (top right corner) becomes a 3x3 piece:
			// ...
			// -7.
			// .|.
			if( ch == '-' || ch == 'J' || ch == '7' ) newmaze.set( newc.move( -1,  0 ), '-' );
			if( ch == '-' || ch == 'F' || ch == 'L' ) newmaze.set( newc.move(  1,  0 ), '-' );
			if( ch == '|' || ch == 'J' || ch == 'L' ) newmaze.set( newc.move(  0, -1 ), '|' );
			if( ch == '|' || ch == 'F' || ch == '7' ) newmaze.set( newc.move(  0,  1 ), '|' );
		}

		// 3) start from outside the maze and see what tiles we can reach from there
		markOutside( newmaze );		

		// 4) then count the tiles in the original maze that a) are not part of the
		// main loop and b) cannot reach the outside in the expanded maze
		long inside = 0;
		for( final Coord2D c : maze ) {
			if( loop.containsKey( c ) ) continue;
			if( newmaze.get( c.x * 2, c.y * 2 ) == '.' ) inside++;
		}
		return inside;
	}

	/**
	 * Breadth-first algorithm to mark all tiles that are not enclosed by the
	 * main loop. The algorithm starts in the top left corner just outside the
	 * grid and keeps moving across empty tiles until all reachable tiles are
	 * marked.
	 * 
	 * @param M The grid to mark, it will be modified in place by the algorithm
	 */
	private void markOutside( final CoordGrid<Character> M ) {
		// find all reachable tiles in the expanded maze, crawling along/between
		// the walls of the original pipe maze
		// use the dimensions of the original maze as outer bounds for the marking
		// to make sure all original coordinates can be mapped onto the expanded one 
		final Window2D border = new Window2D( -1, -1, maze.window( ).getMaxX( ) * 2 + 1, maze.window( ).getMaxY( ) * 2 + 1 );
		
		// keep track of current tiles to explore and keep a history of past
		// tiles, start top left outside the maze
		final Set<Coord2D> visited = new HashSet<>( );
		final Stack<Coord2D> curr = new Stack<>( );
		curr.add( border.getMinCoord( ) );
		
		// keep going until we find no more empty tiles to move
		while( !curr.empty( ) ) {
			final Set<Coord2D> next = new HashSet<>( );
			
			// process all tiles of this round
			while( !curr.empty( ) ) {
				final Coord2D c = curr.pop( );
				
				// check what neighbours to traverse to
				for( final Coord2D nc : c.getAdjacent( false ) ) {
					// we can only move to empty tiles
					if( M.hasValue( nc ) ) continue;
					
					// seen before or not within grid area? don't explore here
					if( visited.contains( nc ) || !border.contains( nc ) ) continue;
					
					// not visited before, mark as visited and explore further next round
					visited.add( nc );
					next.add( nc );
				}				
			}
			
			// add all coordinates to inspect in the next round
			curr.addAll( next );
		}
		
		// overwrite all visited coordinates with the mark
		for( final Coord2D c : visited ) if( !border.onBorder( c ) ) M.set( c, 'O' );
	}
}
