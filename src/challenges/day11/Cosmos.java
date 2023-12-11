package challenges.day11;

import java.util.List;

import aocutil.geometry.Coord2D;
import aocutil.grid.CoordGrid;

/**
 * Our cosmos holding observable galaxies in an ever expanding space
 * 
 * @author Joris
 */
public class Cosmos {
	/** The galaxies observed as a grid */
	protected final CoordGrid<Character> galaxies;

	/**
	 * Parse the observable galaxy into a nice SPARSE grid-like structure
	 * 
	 * @param input The list of strings that describe the initial observation of
	 *   the cosmos and its galaxies, which has to be expanded
	 * @param expfactor The expansion factor with which empty rows and columns in
	 *   the cosmos grid need to be expanded 
	 */
	public Cosmos( final List<String> input, final int expfactor ) {
		// parse the input into a nice sparse grid holding only the galaxy
		// coordinates
		galaxies = CoordGrid.fromCharGrid( input, '.' );
		
		// expand the universe at empty columns and rows by the given factor
		// to do this we first scan for empty columns and rows
		final boolean[] colhasgalaxy = new boolean[ galaxies.window( ).getWidth( ) ];
		final boolean[] rowhasgalaxy = new boolean[ galaxies.window( ).getHeight( ) ];
		for( final Coord2D c : galaxies.getKeys( ) ) colhasgalaxy[ c.x ] = rowhasgalaxy[ c.y ] = true;

		// the we expand by inserting columns and rows equal to expansion minus one
		// as we already had one empty column or row in the cosmos where we insert
		for( int i = colhasgalaxy.length - 1; i >= 0; i-- ) if( !colhasgalaxy[i] ) galaxies.insertColumns( i, expfactor - 1 );
		for( int i = rowhasgalaxy.length - 1; i >= 0; i-- ) if( !rowhasgalaxy[i] ) galaxies.insertRows( i, expfactor - 1 );
	}
	
	/**
	 * Simply sum the Manhattan distance between all pairs of galaxies
	 * 
	 * @return The sum of distances between every pair of galaxies in the cosmos
	 */
	public long sumShortestDistances( ) {
		// easiest way is to sum APSP distances and then divide by half as we count
		// every pair twice
		long sum = 0;
		for( final Coord2D c1 : galaxies.getKeys( ) )
			for( final Coord2D c2 : galaxies.getKeys( ) )
				sum += c1.getManhattanDistance( c2 );
		return sum / 2;
	}
}
