package challenges.day14;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aocutil.geometry.Coord2D;
import aocutil.grid.CoordGrid;

/**
 * A platform that positions mirrors using rocks as weights.
 * 
 * @author Joris
 */
public class MirrorPlatform {
	/** The grid of rocks on the platform */
	protected CoordGrid<Character> rocks;
	
	/**
	 * Creates a new platform from a list of strings
	 * 
	 * @param grid 
	 */
	public MirrorPlatform( final List<String> grid ) {
		this.rocks = CoordGrid.fromCharGrid( grid, '.' );
		rocks.fixWindow( new Coord2D( 0, 0 ), new Coord2D( grid.get( 0 ).length( ) - 1, grid.size( ) - 1 ) );
	}
	
	/**
	 * Tilts the platform northwards and computes the total load on the northern
	 * support beams afterwards
	 * 
	 * @return The total load on the northern beams
	 */
	public long getTiltedLoad( ) {
		tiltNorth();
		return getNorthBeamLoad( );
	}
	
	/**
	 * Computes the load on the northern support beams after running a given
	 * number of rotate and tilt cycles
	 * 
	 * @param cycles The number of cycles to perform
	 * @return The load on the northern beams after the cycles completed
	 */
	public long getCycledLoad( final long cycles ) {
		// keep track of seen configurations
		final Map<String, Long> M = new HashMap<>( );
		
		// perform all the cycles!
		for( long c = 0; c < cycles; c++ ) {
			// one cycle consists of 4 tilt-rotate steps 
			for( int r = 0; r < 4; r++ ) {
				tiltNorth( );
				rocks = rocks.rotate( 1 );
			}
			
			// check if the rocks end up in a position we have seen before, if so we
			// know the process is cyclic and we can extrapolate the position of the
			// rocks at the specified number of cycles
			final String R = rocks.toString( );
			if( M.containsKey( R ) ) {
				// compute the repeat interval and return the load iff we can perform
				// an integer number of repeated cycles from here to get to the desired
				// amount of cycles
				final long interval = c - M.get( R );
				if( (cycles - c - 1) % interval == 0 ) return getNorthBeamLoad( ); 
			}
			
			// store this position and cycle number for future reference
			M.put( R, c );
		}
		
		// no repeating configuration but still completed the cycles, return the
		// load on the northern beams
		return getNorthBeamLoad( );
	}

	/**
	 * Computes the total load on the north support beams of the platform 
	 * @return
	 */
	protected long getNorthBeamLoad( ) {
		long load = 0;
		final int rows = rocks.window( ).getHeight( );
		for( final Coord2D c : rocks.getKeys( ) ) {
			if( rocks.get( c ) == '#' ) continue;
			load += rows - c.y; 
		}
		return load;
	}
	
	/**
	 * Tilt the grid so that all round rocks in the grid will 'fall' Northward.
	 * They will be moved as far north as possible, until they hit either another
	 * rock or the edge.
	 */
	protected void tiltNorth( ) {
		for( int x = rocks.window( ).getMinX( ); x <= rocks.window( ).getMaxX( ); x ++ ) {
			int free = Integer.MAX_VALUE;
			for( int y = rocks.window( ).getMinY( ); y <= rocks.window( ).getMaxY( ); y++ ) {
				final char ch = rocks.get( x, y );
				if( ch == '.' ) {
					if( free > y ) free = y;
				} else if( ch == '#' ) {
					free = Integer.MAX_VALUE;
				} else {
					if( y > free ) {
						rocks.set( x, free, rocks.unset( new Coord2D( x, y ) ) );
						free++;
					}
				}
			}
		}
	}
	
	/** @return The visual representation of the rock positions as a string */
	@Override
	public String toString( ) {
		return rocks.toString( );
	}
}
