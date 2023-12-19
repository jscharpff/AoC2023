package challenges.day18;

import java.util.List;

import aocutil.io.FileReader;

public class Day18 {

	/**
	 * Day 18 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/18
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day18.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day18.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + countArea( ex_input, false) );
		System.out.println( "Answer : " + countArea( input, false ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + countArea( ex_input, true ) );
		System.out.println( "Answer : " + countArea( input, true) );
	}

	/**
	 * Determines the total area of our lava pool, after digging its borders
	 * using the specified instructions 
	 * 
	 * @param input The list of digging instructions to dig out the edges of the
	 *   lava pool
	 * @param usehex True will decode the digging distances in the digging plan
	 *   from the given hexadecimal part, otherwise the simple instructions will
	 *   be used
	 * @return The total area of the pool as a result of digging it 
	 */
	private static long countArea( final List<String> input, final boolean usehex ) {
		final LavaPool pool = !usehex ? LavaPool.fromDigPlan( input ) : LavaPool.fromDigPlanHex( input );
		return pool.countPoolArea( );
	}
}