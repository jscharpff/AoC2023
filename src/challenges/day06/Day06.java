package challenges.day06;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aocutil.io.FileReader;

public class Day06 {

	/**
	 * Day 6 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/6
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day06.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day06.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Computes the product of winning charging ranges for each race. The winning
	 * charging range is the span between the first and last charging time that
	 * will make the boat pass the record distance for that race, given the time
	 * limit. 
	 * 
	 * @param input Two lines containing the time limits and record distances of
	 *   the races
	 * @return The product of winning range sizes
	 */
	private static long part1( final List<String> input ) {
		// first parse the input times and distances into two lists
		final List<Double> times = new ArrayList<>( );
		final List<Double> dists = new ArrayList<>( );

		// using regex of course
		final Pattern p = Pattern.compile( "(\\d+)" );
		final Matcher m1 = p.matcher( input.get( 0 ) );
		while( m1.find( ) ) times.add( Double.parseDouble( m1.group( ) ) );
		final Matcher m2 = p.matcher( input.get( 1 ) );
		while( m2.find( ) ) dists.add( Double.parseDouble( m2.group( ) ) );

		// the compute the product of all winning ranges. A winning range is the
		// range that spans the first and last speed at which the boat will exceed
		// the current record distance
		long prod = 1;
		for( int i = 0; i < times.size( ); i++ )
			prod *= getWinningRange( times.get( i ), dists.get( i ) );
		
		return prod;
	}
	
	/**
	 * Simpler version of part 1 actually, where all whitespace-separated numbers
	 * in the input form a single number. We only have to solve the formula once
	 * 
	 * @param input The time and distance of a single race, inconveniently
	 *   separated by whitespace.
	 * @return The range of charging times that will result in beating the record
	 *   distance
	 */
	private static long part2( final List<String> input ) {	
		final double t = Double.parseDouble( input.get( 0 ).split( ": " )[1].replaceAll( "\\s", "" ) );
		final double d = Double.parseDouble( input.get( 1 ).split( ": " )[1].replaceAll( "\\s", "" ) );
			
		return getWinningRange( t, d );
	}


	
	/**
	 * Compute the winning range for the boat, given the maximum amount of time
	 * and current record distance. The winning range is compute by finding the
	 * first and last time at which the record distance will be surpassed by our
	 * boat when holding the charge button for x time.
	 * 
	 * @param t The maximum time of the race
	 * @param d The current record distance that needs to be surpassed
	 * @return The size of the range of charging times at which our boat will
	 *   pass the record distance.
	 */
	protected static long getWinningRange( final double t, final double d ) {
		// use quadratic formula -b ± sqrt(b²-4ac))/(2a) for when (t-x) *x > d to
		// determine the charging times that will pass the record distance. That
		// is, solve for the polynomial (t-x) * x - d = -x^2 + tx - d > 0 
		final double q0 = (-t - Math.sqrt( t*t - 4.0 * -1.0 * -d)) / (2.0*-1.0);
		final double q1 = (-t + Math.sqrt( t*t - 4.0 * -1.0 * -d)) / (2.0*-1.0);
		
		// get the range spanned between the points solved for. Add/subtract a
		// small amount to the solved x coordinates to enforce rounding of the
		// cutoff points to the next integer so that we solve for > 0. I.e., the
		// resulting distance must be greater than the record distance, not equal.
		return (long)(Math.floor( Math.max( q0, q1 ) - 0.00000001 ) - Math.ceil( Math.min( q0, q1 ) + 0.00000001 ) + 1);
		
	}
}