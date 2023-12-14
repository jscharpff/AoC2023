package challenges.day12;

import java.util.List;

import aocutil.io.FileReader;

public class Day12 {

	/**
	 * Day 12 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/12
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day12.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day12.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part2( ex_input, 1 ) );
		System.out.println( "Answer : " + part2( input, 1 ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input, 5 ) );
		System.out.println( "Answer : " + part2( input, 5 ) );
	}

	/**
	 * Counts the number of unique configurations of spring blocks for every line
	 * in the input list and returns the sum thereof.
	 * 
	 * @param input The list of spring layouts and block sizes to configure
	 * @param folds The number of times the input should be unfolded, i.e.,
	 *   repeated 
	 * @return The sum of unique configuration counts per (unfolded) line
	 */
	private static long part2( final List<String> input, final int folds ) {
		return input.stream( ).mapToLong( in -> SpringConfigurator.fromString( in ).countConfigurations( folds ) ).sum( );
	}
}