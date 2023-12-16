package challenges.day16;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.geometry.Window2D;
import aocutil.grid.CoordGrid;

/**
 * A cave with a maze of mirrors that reflect lasers
 * 
 * @author Joris
 */
public class MirrorMaze {
	/** The layout of the mirror maze as a 2D grid */
	protected final CoordGrid<Character> mirrors;
	
	/**
	 * Creates a new mirror maze from the grid input
	 * 
	 * @param maze The list of string that describe the layout of the mirror maze
	 */
	public MirrorMaze( final List<String> maze ) {
		// parse grid and make sure its size stays equal to the input
		this.mirrors = CoordGrid.fromCharGrid( maze, '.' );
		this.mirrors.fixWindow( new Coord2D( 0, 0 ), new Coord2D( maze.get( 0 ).length( ) - 1, maze.size( ) - 1 ) );
	}
	
	/**
	 * Counts the number of maze tiles that are energised, i.e., part of the
	 * trajectory of at least one laser beam, when firing a laser from the given
	 * starting configuration
	 * 
	 * @param start The starting position of the laser beam
	 * @param dir The direction in which the laser is fired
	 * @return The number of tiles that are energised by the laser or any of its
	 *   reflections
	 */
	public long countEnergised( final Coord2D start, final Direction dir ) {
		// fire the laser beam from the given origin in the specified direction and
		// keep track of all positions visited during its projection
		final Set<Laser> visited = new HashSet<>( );
		fire( new Laser( start, dir ), visited );
		
		// get unique visits per tile using a set. That is, we are no longer
		// interested in direction, only position of the lasers. Using a set of
		// coordinates will filter all duplicate positions
		final Set<Coord2D> C = new HashSet<>( );
		for( final Laser v : visited ) C.add( v.pos );		
		return C.size( );
	}
	
	/**
	 * Finds the laser starting configuration that energises the most tiles in
	 * the maze.
	 * 
	 * @return The amount of tiles energised in the configuration that maximises
	 *   this
	 */
	public long maximiseEnergised( ) {
		// build a set of all possible starting coordinates and directions
		final Window2D W = mirrors.window( );
		final List<Laser> S = new ArrayList<>( );
		for( int y = W.getMinY( ); y <= W.getMaxY( ); y++ ) {
			S.add( new Laser( new Coord2D( W.getMinX( ), y ), Direction.East ) );
			S.add( new Laser( new Coord2D( W.getMaxX( ), y ), Direction.West ) );
		}
		for( int x = W.getMinX( ); x <= W.getMaxX( ); x++ ) {
			S.add( new Laser( new Coord2D( x, W.getMinY( ) ), Direction.South ) );
			S.add( new Laser( new Coord2D( x, W.getMaxY( ) ), Direction.North ) );
		}
		
		// try firing a laser from any of the edge coordinates and find the
		// configuration that energises the most tiles
		return S.stream( ).mapToLong( v -> countEnergised( v.pos, v.dir ) ).max( ).getAsLong( );
	}
	
	/**
	 * Simulates the firing of a laser by continuously moving its front particle
	 * in the direction it is facing until it encounters any mirror that reflects
	 * or splits the beam. Hitting a mirror will result in one or two new lasers
	 * being emitted and simulated. This process continues until either the laser
	 * goes outside of the cave bounds or it ends up in a cycle, i.e., a position
	 * and direction that we've already seen in the simulation.
	 * 
	 * @param origin The starting vector of the laser beam particle
	 * @param visited The set of already seen vectors
	 */
	private void fire( final Laser origin, final Set<Laser> visited ) {
		// copy the laser so that we can modify it to move the particle
		Laser laser = new Laser( origin.pos, origin.dir );
		
		// continue simulation until we either run out of bounds or try to simulate
		// a known vector again
		while( mirrors.contains( laser.pos ) && !visited.contains( laser ) ) { 
			// new vector, register it
			visited.add( laser );
			
			// simulate the next step based on the tile in the maze
			final char ch = mirrors.get( laser.pos );
			switch( ch ) {
				case '\\':
				case '/':
					// reflect the laser
					final Direction newdir;
					switch( laser.dir ) {
						case North: newdir = ch == '\\' ? Direction.West : Direction.East; break;
						case South: newdir = ch == '\\' ? Direction.East : Direction.West; break;
						case West: newdir = ch == '\\' ? Direction.North : Direction.South; break;
						case East: newdir = ch == '\\' ? Direction.South : Direction.North; break;
						default: throw new RuntimeException( "Unknown direction: " + laser.dir );
					}
					fire( new Laser( laser.pos, newdir).move( ), visited );
					return;
				
				case '|':
					// stop this laser and fire two new beams
					if( laser.dir == Direction.East || laser.dir == Direction.West ) {
						fire( new Laser( laser.pos, Direction.North ).move( ), visited );
						fire( new Laser( laser.pos, Direction.South ).move( ), visited );
						return;
					}
					break;
					
				case '-':
					// stop this laser and fire two new beams
					if( laser.dir == Direction.North || laser.dir == Direction.South ) {
						fire( new Laser( laser.pos, Direction.West ).move( ), visited );
						fire( new Laser( laser.pos, Direction.East ).move( ), visited );
						return;
					}
					break;
			}

			// no action, simply move the laser beam along its trajectory
			laser = laser.move( );

		}
	}
	
	/**
	 * Simple, vector-like class that holds the position and direction of the
	 * laser beam's front particle
	 * 
	 * @author Joris
	 *
	 */
	private static class Laser { 
		/** The position of the laser beam front */
		public final Coord2D pos;
		
		/** The direction the laser is shot in */
		public final Direction dir;
		
		/**
		 * Creates a new laser
		 * 
		 * @param c The position of the laser beam's front particle
		 * @param d The direction it is travelling in
		 */
		public Laser( final Coord2D c, final Direction d ) {
			this.pos = c;
			this.dir = d;
		}
		
		/**
		 * Moves the head of the laser beam one step along its trajectory
		 * @return A new laser particle with the new position
		 */
		public Laser move( ) {
			return new Laser( pos.move( dir, 1 ), dir );
		}
		
		/**
		 * Tests this particle against another object for equality
		 * 
		 * @param obj The other object to test against
		 * @return True iff the other object is a valid Laser with the same
		 *   position and direction
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof Laser) ) return false;
			final Laser v = (Laser) obj;
			return v.pos.equals( pos ) && v.dir == dir;
		}
		
		/** @return The string description of the laser particle */
		@Override
		public String toString( ) {
			return pos.toString( ) + dir.toString( ).charAt( 0 );
		}
		
		/** @return The hash code of the (unique) toString result */
		@Override
		public int hashCode( ) {
			return toString( ).hashCode( );
		}
	}
} 
