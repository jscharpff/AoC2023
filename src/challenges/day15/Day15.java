package challenges.day15;

import aocutil.io.FileReader;

public class Day15 {

	/**
	 * Day 15 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/15
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final String ex_input = new FileReader( Day15.class.getResource( "example.txt" ) ).readLines( ).get( 0 );
		final String input = new FileReader( Day15.class.getResource( "input.txt" ) ).readLines( ).get( 0 );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Computes the sum of hash codes for all strings in the input
	 * 
	 * @param input A list of comma-separated strings
	 * @return The sum of hash codes for each of the strings
	 */
	private static long part1( final String input ) {
		long sum = 0;
		for( final String in : input.split( "," ) ) {
			sum += LensBoxes.hash( in );
		}
		return sum;
	}
	
	/**
	 * Adds, updates and removes lenses from a set of hash-indexed boxes, almost
	 * like a plain old hash map...
	 * 
	 * @param input The set of instructions to perform on the list
	 * @return The total focus power after performing all lens operations
	 */
	private static long part2( final String input ) {
		final LensBoxes LB = new LensBoxes( );
		for( final String in : input.split( "," ) ) LB.execute( in );
		return LB.getFocusPower( );
	}
}