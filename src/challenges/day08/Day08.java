package challenges.day08;

import java.util.ArrayList;
import java.util.List;

import aocutil.io.FileReader;

public class Day08 {

	/**
	 * Day 8 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/8
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day08.class.getResource( "example.txt" ) ).readLines( );
		final List<String> ex_input2 = new FileReader( Day08.class.getResource( "example2.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day08.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input2 ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Determines the number of steps required to navigate a desert from position
	 * AAA to ZZZ
	 * 
	 * @param input The navigation moves and desert layout
	 * @return The minimal number of steps required to navigate the desert
	 */
	private static long part1( final List<String> input ) {
		return navigate( input, false );
	}
	
	/**
	 * Determines the number of steps a ghost would need to end up in only states
	 * ending at 'Z', at the same time, if it would start in all states ending
	 * with 'A' simultaneously.
	 * 
	 * @param input The navigation moves and desert layout
	 * @return The number of steps required to navigate the desert and end up in
	 * only terminal nodes if starting at all starting nodes at the same time
	 */
	private static long part2( final List<String> input ) {
		return navigate( input, true );
	}
	
	/**
	 * Function that is used to process the input into a maze and start the right
	 * navigation procedure
	 * 
	 * @param input The move list and desert maze string descriptions
	 * @param ghostly False to navigate from AAA to ZZZ, true to perform ghostly
	 *   navigation...
	 * @return The number of steps required by the chosen navigation method
	 */
	private static long navigate( final List<String> input, final boolean ghostly ) {
		// first reconstruct the move list and maze from the input
		final List<String> in = new ArrayList<>( input );
		final String moves = in.remove( 0 );
		in.remove( 0 );		
		final DesertMaze maze = DesertMaze.fromStringList( in );

		// then start the navigation!
		return !ghostly ? maze.navigateAToZ( moves ) : maze.navigateGhostly( moves );	
	}
}