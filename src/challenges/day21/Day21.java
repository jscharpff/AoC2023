package challenges.day21;

import java.util.List;

import aocutil.io.FileReader;

public class Day21 {

	/**
	 * Day 21 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/21
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day21.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day21.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input, 100 ) );
		System.out.println( "Example: " + part2( ex_input, 5000 ) );
		System.out.println( "Answer : " + part2( input, 26501365 ) );
	}

	/**
	 * Counts the number of spots a gardener can walk to in exactly 64 steps,
	 * given the garden layout specified.
	 * 
	 * @param input The garden layout as a list of strings
	 * @return The number of (unique) garden spots the gardener can end up after
	 *   64 steps
	 */
	private static long part1( final List<String> input ) {
		return new GardenPlan( input ).countVisitable( 64 );
	}

	/**
	 * Counts the number of spots the gardener can walk to given the number of
	 * steps and the garden layout. But wait! There's more... Not only will there
	 * be more steps, the garden layout also repeats infinitely...
	 * 
	 * @param input The garden layout as a list of strings
	 * @param steps The number of steps the gardener will walk
	 * @return The number of unique spots the gardener can reach in the infinite
	 *   gardens
	 */
	private static long part2( final List<String> input, final int steps ) {
		return new GardenPlan( input ).countVisitableInfinite( steps );
	}
}