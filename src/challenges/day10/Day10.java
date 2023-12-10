package challenges.day10;

import java.util.List;

import aocutil.io.FileReader;

public class Day10 {

	/**
	 * Day 10 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/10
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day10.class.getResource( "example.txt" ) ).readLines( );
		final List<String> ex2_input = new FileReader( Day10.class.getResource( "example2.txt" ) ).readLines( );
		final List<String> ex3_input = new FileReader( Day10.class.getResource( "example3.txt" ) ).readLines( );
		final List<String> ex4_input = new FileReader( Day10.class.getResource( "example4.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day10.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex2_input ) );
		System.out.println( "Example: " + part2( ex3_input ) );
		System.out.println( "Example: " + part2( ex4_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Determines the farthest point of the main pipe loop from the starting
	 * position
	 * 
	 * @param input The maze layout as a textual grid, including starting point
	 * @return The point on the main pipe loop that is farthest away from the
	 *   starting point
	 */
	private static long part1( final List<String> input ) {
		final PipeMaze PM = new PipeMaze( input );
		return PM.findFarthestPoint( );
	}
	
	/**
	 * Determine the number of tiles enclosed by the main loop
	 * 
	 * @param input The maze layout as a textual grid, including starting point
	 * @return The count of tiles that are fully enclosed by the main pipe loop,
	 * i.e., not reachable from outside of the loop
	 */
	private static long part2( final List<String> input ) {
		final PipeMaze PM = new PipeMaze( input );
		return PM.countEnclosedTiles( );
	}
}