package challenges.day05;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import aocutil.collections.Range;
import aocutil.io.FileReader;

public class Day05 {

	/**
	 * Day 5 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/5
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day05.class.getResource( "example.txt" ) ).readLineGroups( ";" );
		final List<String> input = new FileReader( Day05.class.getResource( "input.txt" ) ).readLineGroups( ";" );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Converts the seed numbers in the input to their corresponding location 
	 * number using the almanac and returns the smallest location number.
	 * 
	 * @param input The input seed numbers followed by the almanac rules
	 * @return The lowest location number that result from mapping all seed
	 *   input numbers using the almanac
	 */
	private static long part1( final List<String> input ) {
		// reconstruct the almanac from the input
		final List<String> in = new ArrayList<>( input );
		final String seeds = in.remove( 0 ).split( ": " )[1];
		final Almanac A = Almanac.fromStringList( in );
		
		// transform every seed number to its location number using the almanac and
		// return the smallest
		return Stream.of( seeds.substring( 0, seeds.length( ) - 1 ).split( " " ) )
				.mapToLong( s -> A.getLocation( Long.parseLong( s ) ) )
				.min( ).getAsLong( );
	}
	
	/**
	 * Same as part 1 but now the almanac rules are applied to ranges of seed
	 * numbers, after which still the lowest location number is returned.
	 * 
	 * @param input The input seed number pairs followed by the almanac rules
	 * @return The lowest location number that result from mapping all seed
	 *   input number ranges using the almanac
	 */
	private static long part2( final List<String> input ) {
		// reconstruct the almanac from the input
		final List<String> in = new ArrayList<>( input );
		final String seeds = in.remove( 0 ).split( ": " )[1];
		final Almanac A = Almanac.fromStringList( in );
	
		// convert seed pairs into ranges
		final String[] s = seeds.substring( 0, seeds.length( ) - 1 ).split( " " );
		final List<Range> sr = new ArrayList<>( s.length / 2 );
		for( int i = 0; i < s.length; i += 2 ) {
			final long start = Long.parseLong( s[i] );
			final long range = Long.parseLong( s[i + 1] );
			
			sr.add( new Range( start, start + range - 1 ) );
		}
		
		// now keep transforming ranges until we have the set of output ranges and
		// then return the lowest possible output value
		long min = Long.MAX_VALUE;
		for( final Range r : sr ) {
			final List<Range> out = A.getLocationRanges( r );
			long minout = out.stream( ).mapToLong( o -> o.lowest ).min( ).getAsLong( );
			if( minout < min ) min = minout;
		}
		return min;
	}
}