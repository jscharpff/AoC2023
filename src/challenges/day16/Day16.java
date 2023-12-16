package challenges.day16;

import java.util.List;

import aocutil.geometry.Coord2D;
import aocutil.geometry.Direction;
import aocutil.io.FileReader;

public class Day16 {

	/**
	 * Day 16 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/16
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day16.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day16.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Fires a laser beam through a maze of mirrors from the top-left corner in
	 * an eastward direction and counts the number of maze tiles that are
	 * energised as a result. A tile is energised if at least one laser beam
	 * crosses it.
	 * 
	 * @param input The mirror maze as a list of strings
	 * @return The number of tiles energised by the laser
	 */
	private static long part1( final List<String> input ) {
		final MirrorMaze maze = new MirrorMaze( input );
		return maze.countEnergised( new Coord2D( 0, 0 ), Direction.East );
	}
	
	/**
	 * Finds the start-up configuration for the laser that maximises the number
	 * of energised tiles if fired.
	 * 
	 * @param input The mirror maze as a list of strings
	 * @return The number of tiles that are energised in the configuration that
	 *   maximises this value.
	 */
	private static long part2( final List<String> input ) {
		final MirrorMaze maze = new MirrorMaze( input );
		return maze.maximiseEnergised( );
	}
}