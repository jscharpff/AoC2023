package challenges.day09;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import aocutil.io.FileReader;

public class Day09 {

	/**
	 * Day 9 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/9
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day09.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day09.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + predictValue( ex_input, true ) );
		System.out.println( "Answer : " + predictValue( input, true ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + predictValue( ex_input, false ) );
		System.out.println( "Answer : " + predictValue( input, false ) );
	}

	/**
	 * Predicts the next or previous value of every series and sums these prediction
	 * 
	 * @param input
	 * @return
	 */
	private static long predictValue( final List<String> input, final boolean forward ) {
		return input.stream( )
				// for every line, get next or preceding prediction
				.mapToLong( s -> predict( Stream.of( s.split( " " ) ).mapToLong( Long::parseLong ).boxed( ).toList( ), forward ) )
				// and sum over them
				.sum( );
	}
	
	/**
	 * Predicts the next or preceding value of the series
	 * 
	 * @param values The series
	 * @param forward True to extrapolate the next value, false to extrapolate
	 *   backwards
	 * @return The sum of extrapolated values for every series
	 */
	private static long predict( final List<Long> values, final boolean forward ) {
		// all zeroes? return 0 as next prediction
		if( values.stream( ).filter( x -> x != 0 ).count( ) == 0 ) return 0;
		
		// nope, predict the next value (or preceding one)
		if( forward ) 
			return values.get( values.size( ) - 1 ) + predict( difflist( values ), forward );
		else
			return values.get( 0 ) - predict( difflist( values ), forward );
	}

	/**
	 * Builds a new series that contains the differences between every two
	 * successive values in the input series
	 * 
	 * @param values The input series
	 * @return A new series with size one smaller that contains the differences
	 */
	private static List<Long> difflist( final List<Long> values ) {
		final List<Long> diff = new ArrayList<>( values.size( ) - 1 );
		for( int i = 1; i < values.size( ); i++ ) diff.add( values.get( i ) - values.get( i - 1 ) );
		return diff;		
	}

}