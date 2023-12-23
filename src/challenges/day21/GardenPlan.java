package challenges.day21;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Window2D;
import aocutil.grid.CoordGrid;

/**
 * Class that holds a garden layout and helps find the spots our gardener can
 * end up in
 * 
 * @author Joris
 */
public class GardenPlan {
	/** The garden layout */
	protected final CoordGrid<Character> garden;
	
	/** The starting position of the gardener */
	protected final Coord2D gardener;
	
	/**
	 * Reconstructs the garden plan from the grid layout
	 * 
	 * @param grid The garden layout as a list of string
	 */
	public GardenPlan( final List<String> grid ) {
		// parse the garden and make sure the size is fixed to include empty
		// outside border
		garden = CoordGrid.fromCharGrid( grid, '.' );
		garden.fixWindow( new Window2D( grid.get( 0 ).length( ), grid.size( ) ) );
		
		// find start coordinate and remove it from the plan
		Coord2D start = null;
		for( final Coord2D c : garden.getKeys( ) ) 
			if( garden.get( c ) == 'S' ) {
				start = c;
				break;
			}
		garden.unset( start );
		gardener = start;
	}
	
	/**
	 * Finds the number of garden plots that a gardener can visit by moving 
	 * exactly the specified number of steps
	 * 
	 * @param steps The number of steps the gardener can take
	 * @return The number of garden plots it can visit
	 */
	public long countVisitable( final int steps ) {		
		// do a breadth-first exploration to find reachable coordinates
		final Set<Coord2D> V = new HashSet<>( );
		Stack<Coord2D> S = new Stack<>( );
		S.push( gardener );
		long dist = 0;

		// the gardener can walk back and forth to end up in either only even or
		// uneven coordinates
		final int rem = (steps % 2);
		long count = 0;
		
		while( !S.empty( ) ) {
			// reached maximum number of steps, stop moving
			if( dist > steps ) break;
			
			// explore next distance
			final Stack<Coord2D> Snext = new Stack<>( );
			while( !S.empty( ) ) {
				final Coord2D c = S.pop( );
				if( V.contains( c ) ) continue;
				V.add( c );
				
				// only count if even/uneven
				if( dist % 2 == rem ) count++;
				
				// check for new positions to test in next round
				for( final Coord2D n : c.getAdjacent( false ) ) {
					if( V.contains( n ) || garden.get( n ) == '#' || !garden.contains( n ) ) continue;
					
					Snext.add( n );
				}
			}
			dist++;
			S = Snext;
		}
		return count;
	}
	
	/**
	 * Finds the number of garden plots that a gardener can visit by moving 
	 * exactly the specified number of steps, however now the garden repeats for
	 * an infinite amount of times
	 * 
	 * @param steps The number of steps the gardener can take
	 * @return The number of garden plots it can visit
	 */
	public long countVisitableInfinite( final int steps ) {	
		// get width and height for modulo coordinates
		final int WH = garden.window( ).getWidth( );
		
		// use a modulo window that is always even
		final int M = WH * 2;
		
		// keep track of counts and the sum of differences. Due to the map layout,
		// the latter will eventually stabilise and let's us define a function that
		// allows fast computation of future counts
		final Map<Long, Long> values = new HashMap<>( );	
		final LinkedList<Long> dsum = new LinkedList<>( );
		boolean isstable = false;
		
		// do a breadth-first exploration
		final Set<Coord2D> V = new HashSet<>( );
		Stack<Coord2D> S = new Stack<>( );
		S.push( gardener );
		long dist = 0;
		
		// again only even/uneven counts
		final int remainder = (steps % 2);
		long count = 0;
		
		while( !S.empty( ) ) {
			// reached maximum number of steps
			if( dist > steps ) break;
			
			final Stack<Coord2D> Snext = new Stack<>( );
			while( !S.empty( ) ) {
				final Coord2D c = S.pop( );
				if( V.contains( c ) ) continue;
				V.add( c );
				
				if( dist % 2 == remainder ) count++;
				
				for( final Coord2D n : c.getAdjacent( false ) ) {
					if( V.contains( n ) || garden.get( (n.x % WH + WH) % WH, (n.y % WH + WH) % WH ) == '#' ) continue;
					
					Snext.add( n );
				}
			}

			// swap sets for next round
			S = Snext;
	
			// and increase distance, but copy old one for ease computation further on
			final long d = dist;
			dist++;
	
			
			// now store value for this distance (if even/uneven)
			if( d % 2 != remainder ) continue;
			values.put( d, count );
			
			// see if the change in values has stabilised for the modulo window
			// but only if we have enough values
			if( d <= M * 2 + 2 ) continue;
			
			// sum over the difference in the last M - 2 values and add that to the
			// FIFO list of difference sums
			long sum = 0;
			for( int i = 0; i < M; i += 2 )
				sum += (values.get( d - i ) - values.get( d - i - M )) - ((values.get( d - i - 2 ) - values.get( d - i - M - 2 )));
			dsum.addLast( sum );		
			
			// keep its size constant to the size of the modulo window
			if( dsum.size( ) > M ) dsum.removeFirst( );
			
			// can we compute the value of the number of steps based upon the
			// pattern? This is possible if we have enough samples, all differences
			// are now equal and the number of steps is an integer product of the
			// current distance
			if( !isstable ) isstable = dsum.size( ) == M && dsum.stream( ).allMatch( ds -> ds == dsum.getFirst( ).longValue( ) );
			if( isstable && steps % M == d % M	) {
				// yes, extrapolate value based upon known values
				final long n = (steps - d) / M;
				final long dM = values.get( d ) - values.get( d - M );
				return values.get( d ) + dM * n + (n*(n+1)/2) * dsum.getFirst( );
			}
		}

		// the algorithm terminated before we extrapolated, which is also fine of
		// course..
		return count;
	}
}
