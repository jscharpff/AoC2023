package challenges.day22;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import aocutil.geometry.Coord3D;

/**
 * A tower of sand bricks, stacked on top of each other
 * 
 * @author Joris
 */
public class BrickTower {
	/** The list of bricks in the tower */
	protected final List<Brick> bricks;
	
	/**
	 * Creates a new tower of bricks from a snapshot of falling bricks. First
	 * reads the bricks from the input and then drops them
	 * 
	 * @param snapshot The list of strings describing a snapshot of bricks
	 */
	public BrickTower( final List<String> snapshot ) {
		this.bricks = new ArrayList<>( snapshot.size( ) );
		for( final String s : snapshot ) bricks.add( Brick.fromString( s ) );
		
		drop( );
	}
	
	/**
	 * Drops the bricks of the snapshot and returns the number of bricks that
	 * could be removed
	 */
	public void drop( ) {
		// make a stack of bricks, order such that lowest Z pops first
		final Stack<Brick> B = new Stack<>( );
		B.addAll( bricks );
		B.sort( (a,b) -> b.minZ( ) - a.minZ( ) );
		bricks.clear( );
		
		// find max X and Y coordinates of bricks
		int maxX = 0; int maxY = 0;
		for( final Brick b : B ) {
			if( b.maxX( ) > maxX ) maxX = b.maxX( );
			if( b.maxY( ) > maxY ) maxY = b.maxY( );
		}

		// The height map as seen from on top
		final int[][] heights = new int[maxX+1][maxY+1];
		
		// do the falling part
		while( !B.isEmpty( ) ) {
			final Brick b = B.pop( );
	
			// determine the new Z position from current height map
			int newz = 0;
			for( final Coord3D c : b.getCoords( ) )
				if( heights[c.x][c.y] > newz ) newz = heights[c.x][c.y];
			
			// found lowest we can go, update brick and height map
			final Brick newb = b.dropTo( newz + 1 );
			for( final Coord3D c : newb.getCoords( ) ) heights[c.x][c.y] = newb.maxZ( );
			bricks.add( newb );
		}
		
		
		// determine how bricks are stacked on top of one another after falling
		for( final Brick b1 : bricks ) {
			for( final Brick b2 : bricks ) {
				if( b1.equals( b2 ) ) continue;
				
				if( b2.restsOn( b1 ) ) b1.support( b2 );
			}
		}

	}
	
	/**
	 * Counts the number of bricks that could be removed without toppling the
	 * brick stack
	 * 
	 * @return The count of removable bricks
	 */
	public long countRemovable( ) {
		return removable( ).size( );
	}
	
	/**
	 * Tests whether a brick is removable, i.e., all bricks resting on it have at
	 * least one other brick supporting it
	 * 
	 * @return True iff the brick does not cause other bricks to fall if removed
	 */
	private Set<Brick> removable( ) {
		final Set<Brick> rem = new HashSet<>( );
		
		// then find all bricks for which the bricks it supports are also supported
		// by another brick. In other words, it can be removed!
		for( final Brick b : bricks )
			if( b.supports.stream( ).allMatch( b2 -> b2.reston.size( ) > 1 ) ) rem.add( b );
		
		return rem;
	}
	
	/**
	 * Sums the number of bricks that fall when removing each of the bricks in
	 * stack individually
	 * 
	 * @return The total sum of falling bricks
	 */
	public long countMaxFall( ) {
		// get set of bricks that are non removable
		final Set<Brick> B = new HashSet<>( bricks );
		B.removeAll( removable( ) );
		
		// count the number of bricks that would fall if we remove this one
		return B.stream( ).mapToInt( b -> b.tumble( ).size( ) - 1 /* do not count myself */ ).sum( );
	}
	
	/**
	 * A simple brick that spans a line of 3D points 
	 * 
	 * @author Joris
	 */
	private static class Brick {
		/** The coordinates of this brick */
		protected final Coord3D a, b;
		
		/** The set of bricks that rest on me */
		protected final Set<Brick> supports;
		
		/** The set of bricks I rest upon */
		protected final Set<Brick> reston;
		
		/** The (cached) set of bricks resting on me, including indirectly */
		private Set<Brick> supportsIndirect;
		
		/**
		 * Constructs a new brick from two coordinates
		 * 
		 * @param a
		 * @param b
		 */
		public Brick( final Coord3D a, final Coord3D b ) {
			this.a = a;
			this.b = b;
			this.supports = new HashSet<>( );
			this.reston = new HashSet<>( );
		}
		
		/**
		 * Stores that this brick supports b2, will also mark b2 as resting on this
		 * brick
		 * 
		 * @param b2 The brick that is supported by me
		 */
		public void support( final Brick b2 ) {
			supports.add( b2 );
			b2.reston.add( this );
		}
		
		/**
		 * Will tumble the brick, making all bricks supported by this one tumble if
		 * all of their support is gone
		 * 
		 * @return The set of bricks that tumble down as a result of tumbling this
		 * brick
		 */
		public Set<Brick> tumble( ) {
			if( supportsIndirect != null ) return supportsIndirect;

			final Stack<Brick> B = new Stack<>( );
			final Set<Brick> F = new HashSet<>( );
			F.add( this );
			
			// start by trying to tumble all bricks resting on me
			for( final Brick b : supports )
				if( b.shouldFall( F ) ) B.push( b );
			
			// keep on tumbling bricks until all bricks have fallen or the remaining
			// bricks are supported by at least one brick that is not affected by
			// this tumble
			while( !B.isEmpty( ) ) {
				// tumble the next unsupported brick and add all new falling bricks to
				// the set of fallen so far
				final Brick b = B.pop( );
				F.addAll( b.tumble( ) );
				
				// then use the set of fallen bricks to determine potential new bricks
				// that now lack support, which are tumbled in the next iteration
				for( final Brick bf : F ) {
					for( final Brick bs : bf.supports )
						if( !F.contains( bs ) && bs.shouldFall( F ) ) B.push( bs );
				}
			}
			
			// store result for future calls
			supportsIndirect = new HashSet<>( F );
			return supportsIndirect;
		}
		
		/**
		 * Determines whether this brick should fall, given the set of already
		 * fallen bricks
		 * 
		 * @param fallen The set of bricks already fallen down
		 * @return True iff all supporting bricks of this one have tumbled down
		 */
		private boolean shouldFall( final Set<Brick> fallen ) {
			return fallen.containsAll( reston );
		}
		
		/**
		 * @return The set of 3D coordinates that are spanned by this brick
		 */
		public List<Coord3D> getCoords( ) {
			final List<Coord3D> C = new ArrayList<>( );
			if( a.x != b.x ) {
				for( int x = minX( ); x <= maxX( ); x++ )
					C.add( new Coord3D( x, a.y, a.z ) );
			} else if( a.y != b.y) {
				for( int y = minY( ); y <= maxY( ); y++ )
					C.add( new Coord3D( a.x, y, a.z ) );
			} else {
				for( int z = minZ( ); z <= maxZ( ); z++ )
					C.add( new Coord3D( a.x, a.y, z ) );
			}
			return C;
		}
		
		/**
		 * Returns the brick that results when dropping this one to the specified height
		 * 
		 * @param z The height to drop to
		 * @return The new brick positioned at the new height
		 */
		public Brick dropTo( final int z ) {
			final int dz = z - minZ( );
			return new Brick( new Coord3D( a.x, a.y, a.z + dz ), new Coord3D( b.x, b.y, b.z + dz ) );
		}
		
		/**
		 * Determines whether this brick rests on another brick b2. A brick is said
		 * to rest on another brick if it is on top of it. That is, its smallest Z
		 * coordinate is only one higher than the largest of b2 and there must be
		 * some intersection on the 2D x-y plane
		 * 
		 * @param b2 The other brick that may or may not support this brick
		 * @return True iff this brick rests on b2
		 */
		public boolean restsOn( final Brick b2 ) {
			// can only rest on the brick if it is at most one higher
			if( minZ( ) != b2.maxZ( ) + 1 ) return false;
			
			// now check whether there is overlap on x and y
			if( !((b2.minX( ) <= minX( ) && minX( ) <= b2.maxX( )) || (b2.minX( ) <= maxX( ) && maxX() <= b2.maxX( ) ) || (minX( ) <= b2.minX( ) && maxX( ) >= b2.maxX( ) )) ) return false;
			if( !((b2.minY( ) <= minY( ) && minY( ) <= b2.maxY( )) || (b2.minY( ) <= maxY( ) && maxY() <= b2.maxY( ) ) || (minY( ) <= b2.minY( ) && maxY( ) >= b2.maxY( ) )) ) return false;
			
			// yes, this brick rests on the other one
			return true;
		}
		
		/** @return The lowest X coordinate */
		public int minX( ) { return a.x < b.x ? a.x : b.x; }

		/** @return The highest X coordinate */
		public int maxX( ) { return a.x > b.x ? a.x : b.x; }
		
		/** @return The lowest Y coordinate */
		public int minY( ) { return a.y < b.y ? a.y : b.y; }

		/** @return The highest Y coordinate */
		public int maxY( ) { return a.y > b.y ? a.y : b.y; }

		/** @return The lowest Z coordinate */
		public int minZ( ) { return a.z < b.z ? a.z : b.z; }

		/** @return The highest Z coordinate */
		public int maxZ( ) { return a.z > b.z ? a.z : b.z; }
		
		/**
		 * Reconstructs a brick from a pair of 3D coordinates in a tilde-separated string
		 * @param input The coordinates
		 * @return The brick
		 */
		public static Brick fromString( final String input ) {
			final String[] b = input.split( "~" );
			return new Brick( Coord3D.fromString( b[0] ), Coord3D.fromString( b[1] ) );
		}
		
		/** @return The string description of the brick */
		@Override
		public String toString( ) {
			return a + "~" + b;
		}
		
		/**
		 * Tests whether this brick has the same coordinates as the other object
		 * 
		 * @param obj The object to test against
		 * @return True iff obj is a valid brick with the same coordinates
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof Brick) ) return false;
			final Brick br = (Brick)obj;
			return a.equals( br.a ) && b.equals( br.b );
		}
		
		/** @return The has code of the unique string */
		@Override
		public int hashCode( ) {
			return toString( ).hashCode( );
		}
	}
}
