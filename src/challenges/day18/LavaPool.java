package challenges.day18;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.string.RegexMatcher;

/**
 * A pool that holds lava!
 * 
 * @author Joris
 */
public class LavaPool {
	/** The pool borders */
	protected final Set<Line> border;
	
	/**
	 * Creates a new, empty, borderless LavaPool
	 */
	private LavaPool( ) { 
		border = new HashSet<Line>( );
	}
	
	/**
	 * Determines the size of the pool area by iteratively 'collapsing' borders
	 * and summing the areas of the part we 'cut out'
	 * 
	 * @return The total area of the pool
	 */
	public long countPoolArea( ) {
		// make Q's of (remaining) line segments ordered on x and y coordinates
		final List<Line> X = new ArrayList<>(  );
		final List<Line> Y = new ArrayList<>(  );
		for( final Line l : border ) {
			if( l.isVertical( ) ) X.add( l );
			else Y.add( l );
		}
		X.sort( Line::sortX );
		Y.sort( Line::sortY );

		// move along the X axis and every time we encounter vertical segments we
		// move it to the next 'corner'
		long area = 0;
		while( !X.isEmpty( ) ) {
			// get next vertical segment
			final Line v = X.remove( 0 );
			
			// and determine at which next X a new vertical segment starts, that is
			// the x coordinate we want to collapse the segment to
			long nextX = v.minX;
			for( final Line lnext : X ) {
				if( lnext.minX > nextX ) {
					nextX = lnext.minX;
					break;
				}
			}
			
			// find out which two horizontal lines intersect with the segment
			final List<Line> intersect = new ArrayList<>( );
			for( final Line h : Y )
				if( h.minX == v.minX && (h.minY == v.maxY || h.maxY == v.minY) ) intersect.add( h );
			
			// make sure we have exactly 2 intersections, otherwise the algorithm has
			// produced an invalid state
			if( intersect.size( ) != 2 )
				throw new RuntimeException( "Line segment " + v + " does not intersect exactly two horizontals" );
			
			// now go over the pair and replace the original horizontal segments by
			// new ones so that rectangle from v.x to nextX is cut out along the
			// horizontal intersection lines			
			final Line h1 = intersect.get( 0 );
			final Line h2 = intersect.get( 1 );
			final long newX = Math.min( Math.min( h1.maxX, h2.maxX ), nextX );

			// count the area of the rectangle we cut out by this move
			area += (newX - v.maxX) * (h2.minY - h1.minY + 1);

			// replace or shorten the horizontal segments
			if( h1.maxX == newX ) { Y.remove( h1 ); } else { Y.set( Y.indexOf( h1 ), new Line( newX, h1.minY, h1.maxX, h1.maxY ) ); }
			if( h2.maxX == newX ) { Y.remove( h2 ); } else { Y.set( Y.indexOf( h2 ), new Line( newX, h2.minY, h2.maxX, h2.maxY ) ); }

			// determine the new vertical line segment to insert a build a list of
			// all segments that may interact with the new segment
			final List<Line> Xnew = new ArrayList<>( );
			Line vnew = new Line( newX, h1.minY, newX, h2.minY );
			Xnew.add( vnew );

			// get all segments that may interact with the new one
			for( int i = X.size( ) - 1; i >= 0; i-- ) {
				final Line xl = X.get( i );
				if( xl.minX != newX ) continue;
				if( xl.maxY < vnew.minY || xl.minY > vnew.maxY ) continue;
				Xnew.add( X.remove( i ) );
			}
			
			// go over the list and join/merge/split segments by pairwise processing
			for( int i = 0; i < Xnew.size( ) - 1; i++ ) {
				final Line l1 = Xnew.get( i );
				
				for( int j = 0; j < Xnew.size( ); j++ ) {
					if( i == j ) continue;
					final Line l2 = Xnew.get( j );
					
					if( l1.minY == l2.minY && l1.maxY == l2.maxY ) {
						// collapsed rectangle
						Xnew.remove( j );
						Xnew.remove( i );				
						area += l2.height( );
					} else if( l1.minY < l2.minY && l1.maxY > l2.maxY ) {
						// overlapping segment
						Xnew.set( i, new Line( newX, l1.minY, newX, l2.minY ) );
						Xnew.set( j, new Line( newX, l2.maxY, newX, l1.maxY ) );
						area += l2.height( ) - 2 /* corners */;						
					} else if( l1.minY == l2.maxY ) {
						// consecutive segment to merge
						Xnew.set( i, new Line( newX, l2.minY, newX, l1.maxY ) );
						Xnew.remove( j );
					} else if( l1.maxY == l2.minY ) {
						// consecutive segment to merge
						Xnew.set( i, new Line( newX, l1.minY, newX, l2.maxY ) );
						Xnew.remove( j );
					} else if( l1.minY == l2.minY && l1.maxY > l2.maxY ) { 
						// partial collapse (top)
						Xnew.set( i, new Line( newX, l2.maxY, newX, l1.maxY ) );
						Xnew.remove( j );
						area += l2.height( ) - 1 /* single corner */;												
					} else if( l1.maxY == l2.maxY && l1.minY < l2.minY ) { 
						// partial collapse (bottom)
						Xnew.set( i, new Line( newX, l1.minY, newX, l2.minY ) );
						Xnew.remove( j );
						area += l2.height( ) - 1 /* single corner */;												
					} else {
						// no action applied, continue next j
						continue;
					}
					
					// we did something, reconsider the current item
					i = Math.min( 0, i - 1 - (j < i ? 1 : 0) );
					break;
				}
			}
			
			// we've processed all segments and built a new segment list, re-add 
			// segments to the X list and sort again
			X.addAll( Xnew );
			X.sort( Line::sortX );
		}

		// return the area of the pool
		return area;
	}
	
	/**
	 * Digs out the borders of the pool based upon the given digging plan
	 * 
	 * @param plan The digging plan as a list of dig instructions
	 * @return The LavaPool with its borders as defined by the set
	 */
	public static LavaPool fromDigPlan( final List<String> plan ) {
		final LavaPool pool = new LavaPool( );
		
		// process instructions to dig the pool area
		Coord2D p = new Coord2D( 0, 0 );
		for( final String in : plan ) {
			// determine length and direction of next segment
			final String[] i = in.split( " " );
			final Direction d = Direction.fromLetter( i[0].charAt( 0 ) );
			final int len = Integer.parseInt( i[1] );
			
			// move in the given direction and add the border segment that was
			// spanned by the move
			final Coord2D newp = p.move( d, len );
			pool.border.add( new Line( p.x, p.y, newp.x, newp.y ) );
			p = p.move( d, len );				
		}
		
		return pool;
	}
	
	/**
	 * Digs out the borders of the pool based upon the given digging plan,
	 * however now the digging instructions use a hexadecimal encoding to
	 * describe the border sizes
	 * 
	 * @param plan The digging plan as a list of dig instructions
	 * @return The LavaPool with its borders as defined by the set
	 */
	public static LavaPool fromDigPlanHex( final List<String> plan ) {
		final LavaPool pool = new LavaPool( );
		
		// process instructions to dig the pool area
		Coord2D p = new Coord2D( 0, 0 );
		for( final String in : plan ) {
			final String i = RegexMatcher.extract( "\\(#([a-z0-9]{6})\\)", in );

			// determine length and direction of next segment
			final int len = Integer.parseInt( i.substring( 0, 5 ), 16 );			
			final Direction d;
			switch( i.charAt( 5 ) ) {
				case '0': d = Direction.East; break;
				case '1': d = Direction.South; break;
				case '2': d = Direction.West; break;
				case '3': d = Direction.North; break;
				default: throw new RuntimeException( "Unknown direction code: " + i.charAt( 5 ) );
			}
						
			// move in the given direction and add the border segment that was
			// spanned by the move
			final Coord2D newp = p.move( d, len );
			pool.border.add( new Line( p.x, p.y, newp.x, newp.y ) );
			p = p.move( d, len );
		}
		
		return pool;
	}

	/**
	 * Describes a single edge of the pool borders
	 * 
	 * @author Joris
	 */
	private static class Line {
		/** The smallest X coordinate */ 
		private final long minX;

		/** The largest X coordinate */ 
		private final long maxX;

		/** The smallest Y coordinate */ 
		private final long minY;

		/** The largest Y coordinate */ 
		private final long maxY;
		
		/** The segment ID for easy reference and list removal */ 
		private final int ID;
		
		/** The next ID to assign to a segment */
		private static int nextID = 0;
		
		/**
		 * Constructs a new line segment. The x and y coordinates will be assigned
		 * to their corresponding x/y min and max variables; 
		 * 
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 */
		protected Line( final long x1, final long y1, final long x2, final long y2 ) {
			this.minX = Math.min( x1, x2 ); 
			this.maxX = Math.max( x1, x2 ); 
			this.minY = Math.min( y1, y2 ); 
			this.maxY = Math.max( y1, y2 ); 
			this.ID = nextID++;
		}
		
		/**
		 * Returns the sorting compare value of this line and the given line, using
		 * a X-based sorting
		 * 
		 * @param l2 The line to compare against
		 * @return The natural ordering of l and l2 based upon X values
		 */
		public int sortX( final Line l2 ) {
			if( minX != l2.minX ) return Long.compare( minX, l2.minX );
			if( minY != l2.minY ) return Long.compare( minY, l2.minY );
			return Long.compare( maxY, l2.maxY );
		}

		/**
		 * Returns the sorting compare value of this line and the given line, using
		 * a Y-based sorting
		 * 
		 * @param l2 The line to compare against
		 * @return The natural ordering of l and l2 based upon Y values
		 */
		public int sortY( final Line l2 ) {
			if( minY != l2.minY ) return Long.compare( minY, l2.minY );
			if( minX != l2.minX ) return Long.compare( minX, l2.minX );
			return Long.compare( maxX, l2.maxX );
		}

		/** @return True if the segment is horizontal */
		public boolean isVertical( ) { return minX == maxX; }
		
		/** @return The height of the line as the count of coordinates it spans */
		public long height( ) { return maxY - minY + 1; }
		
		/**
		 * Tests if this line has the same ID as the given line
		 * 
		 * @param obj The object to compare against
		 * @return True iff the other object is a valid line and has the same ID
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof Line) ) return false;
			return ((Line)obj).ID == ID;
		}

		/** @return The ID of the line as unique hash code */
		@Override
		public int hashCode( ) { 
			return ID;
		}
		
		/** @return The string that describes the line as (xmin,ymin)-(xmax,ymax) */
		@Override
		public String toString( ) {
			return "(" + minX + "," + minY + ")-(" + maxX + "," + maxY + ")";
		}		
	}
	
}
