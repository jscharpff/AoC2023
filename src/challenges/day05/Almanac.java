package challenges.day05;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import aocutil.collections.Range;

/**
 * The container for the almanac that describes seeds to (eventually..)
 * location numbers
 * 
 * @author Joris
 */
public class Almanac {
	/** The transformation maps in this almanac */
	private final List<AlmanacMap> maps;
	
	/**
	 * Creates a new, empty almanac
	 */
	protected Almanac( ) {
		this.maps = new ArrayList<>( );
	}
	
	/**
	 * Transforms a single seed number into its corresponding location number by
	 * applying all transformation maps
	 * 
	 * @param seed The seed number
	 * @return Its corresponding, mapped location number
	 */
	public long getLocation( final long seed ) {
		long curr = seed;
		for( final AlmanacMap m : maps ) curr = m.apply( curr );
		return curr;
	}
	
	/**
	 * Transforms a range of seed numbers into all possible output location
	 * ranges that result from applying the mapping rules on the range
	 * 
	 * @param range The range to transform
	 * @return The list of all output ranges
	 */
	public List<Range> getLocationRanges( final Range range ) {
		// start with a set containing only the initial range and keep transforming
		// the set until we know all output ranges
		final Stack<Range> R = new Stack<>( );		
		R.add( range );
		
		// apply every mapping to the set of ranges we have 
		for( final AlmanacMap map : maps ) {
			final Stack<Range> Rnew = new Stack<>( );
			while( !R.isEmpty( ) ) Rnew.addAll( map.applyRange( R.pop( ) ) );
			R.addAll( Rnew );
		}
		
		// return the set of output ranges
		return R;
	}

	/**
	 * Recreates the almanac from its string description, one line for every
	 * transformation map in the almanac. It is assumed the maps are given in
	 * their order of application.
	 * 
	 * @param input The list of transformation maps, one per string
	 * @return The almanac
	 */
	public static Almanac fromStringList( final List<String> input ) {
		final Almanac A = new Almanac( );
		for( final String in : input ) A.maps.add( AlmanacMap.fromString( in ) );
		return A;
	}
	
	
	/**
	 * Container for a transformation map described by the almanac
	 * 
	 * @author Joris
	 */
	protected static class AlmanacMap {
		/** The name of the mapping rule */
		final String name;
		
		/** The mapping rules */
		final List<AlmanacRule> rules;
		
		/**
		 * Creates a new mapping with the specified name and empty rule set
		 * 
		 * @param name The name of the map
		 */
		protected AlmanacMap( final String name ) {
			this.name = name;
			this.rules = new ArrayList<>( );
		}
		
		/**
		 * Applies the transformation map to a single number
		 * 
		 * @param in The input number
		 * @return The resulting mapped value
		 */
		protected long apply( final long in ) {
			// find the right rule to apply and returns the result
			for( final AlmanacRule r : rules )
				if( r.contains( in ) ) return r.apply( in );
			
			// no rule, simply pass on
			return in;
		}
		
		/**
		 * Applies the transformation map to a range of values
		 * 
		 * @param range The range to apply to
		 * @return A list of new ranges as a result of applying all mapping rules
		 *   to the original range
		 */
		protected Collection<Range> applyRange( final Range range ) {
			// start with an empty output set and begin at the start of the input
			// range
			final List<Range> R = new ArrayList<>( );
			long curr = range.lowest;
			
			// go over rules to determine all transformed ranges
			for( final AlmanacRule rule : rules ) {
				// did we reach the end of the input range before running out of rules?
				if( curr >= range.highest ) break;
				
				// does the current rule apply to this range? If not, skip it
				if( rule.range.highest < curr) continue;
				
				// yes, add range from current to start of this rule
				if( rule.range.lowest > curr ) R.add( new Range( curr, rule.range.lowest - 1 ) );
				
				// then add part corresponding to applying the rule (including the
				// actual transformation of values). Also make sure we do not expand
				// the range beyond the highest value in the input range.
				final long end = Math.min( rule.range.highest, range.highest ); 
				R.add( new Range( curr + rule.tr, end + rule.tr  ) );
				
				// move current index to just after the most recently added range
				curr = end + 1;
			}
			
			// add remaining range (if necessary)
			if( curr < range.highest ) R.add( new Range( curr, range.highest ) );
			return R;
		}
		
		/**
		 * Recreates the transformation map from a semicolon-separated string
		 * 
		 * @param input The map as a string. The first element is its name, all
		 *   elements thereafter are mapping rules
		 * @return The transformation map
		 */
		protected static AlmanacMap fromString( final String input ) {
			final String[] in = input.split( ";" );
			final AlmanacMap map = new AlmanacMap( in[0].substring( 0, in[0].length( ) - 1 ) );
			
			// parse all rules
			for( int i = 1; i < in.length; i++ )
				map.rules.add( AlmanacRule.fromString( in[i] ) );
			
			// sort rules on lowest value for processing later
			map.rules.sort( (x,y) -> x.range.compareTo( y.range ) );
			return map;
		}
	}
	
	/**
	 * A single rule of an almanac transformation map
	 * 
	 * @author Joris
	 */
	private static class AlmanacRule {
		/** The mapping range */
		protected final Range range;
		
		/** The scalar transformation to apply */ 
		protected final long tr;
		
		/**
		 * Creates a new transformation rule
		 * 
		 * @param start The input start value of this rule
		 * @param out The output start value of this rule 
		 * @param size The amount of numbers spanned by this rule from start to
		 *   start + size -1
		 */
		private AlmanacRule( final long start, final long out, final long size ) {
			this.range = new Range( start, start + size - 1 );
			this.tr = out - start;
		}
		
		/**
		 * Checks if this rule contains the given number
		 * 
		 * @param n The number to test
		 * @return True iff the number is within the range of this rule
		 */
		protected boolean contains( final long n ) {
			return range.contains( n );
		}
		
		/**
		 * Applies the transformation to the specified number
		 * 
		 * @param n The input number
		 * @return The transformed output value 
		 */
		protected long apply( final long n ) {
			return n + tr;
		}

		/**
		 * Reconstructs the rule from a triplet of integers
		 * 
		 * @param input The rule values given as [out] [start] [size]
		 * @return The rule corresponding to the input string
		 */
		protected final static AlmanacRule fromString( final String input ) {
			final String[] s = input.split( " " );
			return new AlmanacRule( Long.parseLong( s[1] ), Long.parseLong( s[0] ), Long.parseLong( s[2] ) );
		}
		
		/**
		 * @return The rule as a string [range] (transform)
		 */
		@Override
		public String toString( ) {
			return range.toString( ) + " (" + tr + ")";
		}
	}
}
