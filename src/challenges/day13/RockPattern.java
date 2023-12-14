package challenges.day13;

import java.util.ArrayList;
import java.util.List;

/**
 * The rock pattern that has a mirror in there somewhere
 * 
 * @author Joris
 */
public class RockPattern {
	/** The pattern as a grid */
	protected final boolean[][] pattern;
	
	/** the width of the grid */
	protected final int width;
	
	/** The height of the grid */
	protected final int height;
	
	/**
	 * Creates a new rock pattern from its string description
	 * 
	 * @param input The input string that has lines of '#' and '.' symbols that
	 *   describe the rock layout, lines are separated by a newline symbol 
	 */
	public RockPattern( final String input ) {
		final String[] lines = input.split( "\n" );
		height = lines.length;
		width = lines[0].length( );

		pattern = new boolean[ width ][ height ];
		for( int y = 0; y < lines.length; y++ ) {
			final String line = lines[y];
			for( int x = 0; x < line.length( ); x++ )
				pattern[x][y] = line.charAt( x ) == '#';
		}
	}
	
	/**
	 * Finds the single mirror somewhere in the rock pattern and returns its
	 * score. N.B. this code assumes that there is exactly one mirror!
	 *   
	 * @return The score of the single mirror in the rock pattern
	 */
	public int getMirrorScore( ) {
		final List<Mirror> M = findMirrors( );
		return M.isEmpty( ) ? 0 : M.get( 0 ).score;
	}
	
	/**
	 * Goes over all columns and rows to find all potential mirrors in the rock
	 * pattern
	 * 
	 * @return The list of all valid horizontal and vertical mirror positions
	 *   given the rock pattern we have.
	 */
	private List<Mirror> findMirrors( ) {
		// check if there might be vertical mirror
		final List<Mirror> M = new ArrayList<>( );
		for( int x = 1; x < width; x++ ) {
			boolean reflects = true;
			for( int y = 0; y < height; y++ ) {
				for( int i = 0; i < width; i++ ) {
					if( !reflects || x - i - 1 < 0 || x + i >= width ) break;
					reflects &= pattern[ x - i - 1][ y ] == pattern[ x + i ][ y ];
				}
			}
			if( reflects ) M.add( new Mirror( false, x ) );
		}

		// or maybe a horizontal mirror
		for( int y = 1; y < height; y++ ) {
			boolean reflects = true;
			for( int x = 0; x < width; x++ ) {
				for( int i = 0; i < height; i++ ) {
					if( !reflects || y - i - 1 < 0 || y + i >= height ) break;
					reflects &= pattern[ x ][ y - i - 1 ] == pattern[ x ][ y + i ];
				}
			}
			if( reflects ) M.add( new Mirror( true, y ) );
		}
		
		return M;
	}
	
	/**
	 * Finds the new mirror score that results when any of the single tiles in
	 * the rock pattern is flipped from '.' to '#' or vice versa. 
	 *   
	 * @return The new score, i.e., the score of a mirror setup not equal to the
	 *   mirror setup found with the original pattern 
	 */
	public int getMirrorScoreSmudged( ) {
		// get current mirror setup
		final Mirror M = findMirrors( ).get( 0 );
		
		// try and change the score by fixing a smudge somewhere in the pattern 
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				// flip this coordinate and get all mirror positions
				pattern[x][y] = !pattern[x][y];	
				final List<Mirror> Mnew = findMirrors( );
				pattern[x][y] = !pattern[x][y];

				// remove the previous setup from the list
				Mnew.remove( M );

				// no mirrors?
				if( Mnew.isEmpty( ) ) continue;
				
				// we have a new setup, stop searching and return its score
				return Mnew.get( 0 ).score;				
			}
		}
		
		throw new RuntimeException( "No change in mirror position" );
	}
	
	/**
	 * @return The rock pattern as a matrix of '#' and '.' symbols
	 */
	@Override
	public String toString( ) {
		final StringBuilder sb = new StringBuilder( );
		for( int y = 0; y < height; y++ ) {
			for( int x = 0; x < width; x++ ) sb.append( pattern[x][y] ? '#' : '.' );
			if( y < height - 1 ) sb.append( '\n' );
		}
		return sb.toString( );
	}
	
	/**
	 * Struct to contain a single mirror configuration
	 * 
	 * @author Joris
	 */
	private static class Mirror {
		/** True if the mirror reflects horizontally, false for vertical */
		protected final boolean horizontal;
		
		/** The row or column index that the mirror is positioned at */
		protected final int index;
		
		/** The mirror score */
		protected final int score;
		
		/**
		 * Creates a new mirror setup
		 * 
		 * @param horizontal True for horizontal reflection, false for vertical
		 * @param index The column index (horizontal) or row index (vertical) of
		 *   the mirror
		 */
		public Mirror( final boolean horizontal, final int index ) {
			this.horizontal = horizontal;
			this.index = index;
			this.score = index * (horizontal ? 100 : 1); 
		}

		/**
		 * Compares this mirror to another object
		 * 
		 * @param obj The object to compare against
		 * @return True iff the other object is a mirror with the same reflection
		 *   and index
		 */
		@Override
		public boolean equals( final Object obj ) {
			if( obj == null || !(obj instanceof Mirror) ) return false;
			final Mirror m = (Mirror) obj;
			
			return m.horizontal == horizontal && m.index == index;
		}
		
		/** @return The string describing the mirror setup */
		@Override
		public String toString( ) {
			return (horizontal ? "H" : "V") + index;
		}
	}
}
